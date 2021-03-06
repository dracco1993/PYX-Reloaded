package com.gianlu.pyxreloaded.handlers;

import com.gianlu.pyxreloaded.Consts;
import com.gianlu.pyxreloaded.data.JsonWrapper;
import com.gianlu.pyxreloaded.data.User;
import com.gianlu.pyxreloaded.server.Parameters;
import io.undertow.server.HttpServerExchange;
import org.jetbrains.annotations.NotNull;

public class PongHandler extends BaseHandler {
    public static final String OP = Consts.Operation.PONG.toString();

    @NotNull
    @Override
    public JsonWrapper handle(User user, Parameters params, HttpServerExchange exchange) {
        user.userReceivedEvents();
        return JsonWrapper.EMPTY;
    }
}
