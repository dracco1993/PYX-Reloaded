package com.gianlu.pyxreloaded.handlers;

import com.gianlu.pyxreloaded.Consts;
import com.gianlu.pyxreloaded.JsonWrapper;
import com.gianlu.pyxreloaded.data.Game;
import com.gianlu.pyxreloaded.data.GameManager;
import com.gianlu.pyxreloaded.data.User;
import com.gianlu.pyxreloaded.servlets.Annotations;
import com.gianlu.pyxreloaded.servlets.BaseCahHandler;
import com.gianlu.pyxreloaded.servlets.Parameters;
import io.undertow.server.HttpServerExchange;

public class GameOptionsSuggestionDecisionHandler extends GameWithPlayerHandler {
    public final static String OP = Consts.Operation.GAME_OPTIONS_SUGGESTION_DECISION.toString();

    public GameOptionsSuggestionDecisionHandler(@Annotations.GameManager GameManager gameManager) {
        super(gameManager);
    }

    @Override
    public JsonWrapper handleWithUserInGame(User user, Game game, Parameters params, HttpServerExchange exchange) throws BaseCahHandler.CahException {
        if (game.getHost() != user) throw new BaseCahHandler.CahException(Consts.ErrorCode.NOT_GAME_HOST);

        String suggestedId = params.get(Consts.GameSuggestedOptionsData.ID);
        if (suggestedId == null || suggestedId.isEmpty())
            throw new BaseCahHandler.CahException(Consts.ErrorCode.BAD_REQUEST);

        if (!params.has(Consts.GameSuggestedOptionsData.DECISION))
            throw new BaseCahHandler.CahException(Consts.ErrorCode.BAD_REQUEST);

        if (params.getBoolean(Consts.GameSuggestedOptionsData.DECISION, false))
            game.applySuggestedOptions(suggestedId);
        else
            game.declineSuggestedOptions(suggestedId);

        return JsonWrapper.EMPTY;
    }
}
