package server;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {

    protected void sendText(HttpExchange exchange, String text, int code) throws IOException {
        if (text == null) {
            text = "[]";
        }

        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");

        try {
            exchange.sendResponseHeaders(code, resp.length);
            exchange.getResponseBody().write(resp);
        } finally {
            exchange.close();
        }

        System.out.println(">>> sendText: code=" + code + ", length=" + resp.length +
                ", text=" + (text.length() > 200 ? text.substring(0, 200) + "..." : text));
    }

    protected void sendNotFound(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, message, 404);
    }

    protected void sendHasInteractions(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, message, 406);
    }
}
