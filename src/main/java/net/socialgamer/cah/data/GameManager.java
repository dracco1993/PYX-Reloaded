package net.socialgamer.cah.data;

import net.socialgamer.cah.data.Game.TooManyPlayersException;
import net.socialgamer.cah.task.BroadcastGameListUpdateTask;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Manage games for the server.
 *
 * @author Andy Janata (ajanata@socialgamer.net)
 */
public class GameManager {
    private static final Logger logger = Logger.getLogger(GameManager.class);
    private final int maxGames;
    private final Map<Integer, Game> games = new TreeMap<>();
    private final GameProvider gameProvider;
    private final BroadcastGameListUpdateTask broadcastUpdate;

    /**
     * Create a new game manager.
     *
     * @param gameProvider Provider for new {@code Game} instances.
     * @param maxGames     Provider for maximum number of games allowed on the server.
     */
    public GameManager(GameProvider gameProvider, int maxGames, BroadcastGameListUpdateTask broadcastUpdate) {
        this.gameProvider = gameProvider;
        this.maxGames = maxGames;
        this.broadcastUpdate = broadcastUpdate;
    }

    public static int generateGameId() {
        return ThreadLocalRandom.current().nextInt();
    }

    private int getMaxGames() {
        return maxGames;
    }

    /**
     * Creates a new game, if there are free game slots. Returns {@code null} if there are already the
     * maximum number of games in progress.
     *
     * @return Newly created game, or {@code null} if the maximum number of games are in progress.
     */
    private Game createGame() {
        synchronized (games) {
            if (games.size() >= getMaxGames()) return null;
            Game game = gameProvider.create(this);
            if (game.getId() < 0) return null;
            games.put(game.getId(), game);
            return game;
        }
    }

    /**
     * Creates a new game and puts the specified user into the game, if there are free game slots.
     * Returns {@code null} if there are already the maximum number of games in progress.
     * <p>
     * Creating the game and adding the user are done atomically with respect to another game getting
     * created, or even getting the list of active games. It is impossible for another user to join
     * the game before the requesting user.
     *
     * @param user User to place into the game.
     * @return Newly created game, or {@code null} if the maximum number of games are in progress.
     * @throws IllegalStateException If the user is already in a game and cannot join another.
     */
    public Game createGameWithPlayer(final User user) throws IllegalStateException {
        synchronized (games) {
            Game game = createGame();
            if (game == null) return null;

            try {
                game.addPlayer(user);
                logger.info(String.format("Created new game %d by user %s.", game.getId(), user.toString()));
            } catch (IllegalStateException ise) {
                destroyGame(game.getId());
                throw ise;
            } catch (TooManyPlayersException tmpe) {
                // this should never happen -- we just made the game
                throw new Error("Impossible exception: Too many players in new game.", tmpe);
            }

            broadcastGameListRefresh();
            return game;
        }
    }

    /**
     * This probably will not be used very often in the server: Games should normally be deleted when
     * all players leave it. I'm putting this in if only to help with testing.
     * <p>
     * Destroys a game immediately. This will almost certainly cause errors on the client for any
     * players left in the game. If {@code gameId} isn't valid, this method silently returns.
     *
     * @param gameId ID of game to destroy.
     */
    public void destroyGame(int gameId) {
        synchronized (games) {
            final Game game = games.remove(gameId);
            if (game == null) return;

            // remove the players from the game
            List<User> usersToRemove = game.getUsers();
            for (User user : usersToRemove) {
                game.removePlayer(user);
                game.removeSpectator(user);
            }

            logger.info(String.format("Destroyed game %d.", game.getId()));
            broadcastGameListRefresh();
        }
    }

    /**
     * Broadcast an event to all users that they should refresh the game list.
     */
    public void broadcastGameListRefresh() {
        broadcastUpdate.needsUpdate();
    }

    /**
     * @return A copy of the list of all current games.
     */
    public Collection<Game> getGameList() {
        synchronized (games) {
            return new ArrayList<>(games.values());
        }
    }

    /**
     * Gets the game with the specified id, or {@code null} if there is no game with that id.
     *
     * @param id Id of game to retrieve.
     * @return The Game, or {@code null} if there is no game with that id.
     */
    public Game getGame(final int id) {
        synchronized (games) {
            return games.get(id);
        }
    }

    Map<Integer, Game> getGames() {
        return games;
    }

    public interface GameProvider {
        Game create(GameManager manager);
    }
}
