package net.socialgamer.cah.handlers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.socialgamer.cah.Constants.AjaxOperation;
import net.socialgamer.cah.Constants.AjaxResponse;
import net.socialgamer.cah.Constants.ReturnableData;
import net.socialgamer.cah.RequestWrapper;
import net.socialgamer.cah.data.Game;
import net.socialgamer.cah.data.GameManager;
import net.socialgamer.cah.data.User;

import com.google.inject.Inject;


public class LeaveGameHandler extends GameHandler {

  public static final String OP = AjaxOperation.LEAVE_GAME.toString();

  @Inject
  public LeaveGameHandler(final GameManager gameManager) {
    super(gameManager);
  }

  @Override
  public Map<ReturnableData, Object> handle(final RequestWrapper request,
      final HttpSession session, final User user, final Game game) {
    final Map<ReturnableData, Object> data = new HashMap<ReturnableData, Object>();

    game.removePlayer(user);
    // Return the game ID back to the client. It should in theory be able to figure out which leave
    // was successfull but whatever.
    data.put(AjaxResponse.GAME_ID, game.getId());
    return data;
  }
}