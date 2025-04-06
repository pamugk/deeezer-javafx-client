package api.net;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class DeezerCallbackHandler implements HttpHandler {
    private final DeezerCallbackServer cbs;
    DeezerCallbackHandler(DeezerCallbackServer callbackServer) {
        cbs = callbackServer;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        Map<String, String> queryArgs = queryToMap(t.getRequestURI().getQuery());
        String oAuth2Code = queryArgs.get("code");
        if (oAuth2Code != null) {
            writeResponse(t, 200, "OK. Please, return to your application");
            cbs.setOAuth2Code(oAuth2Code);
        } else
            writeResponse(t, 400, "Error. Please, restart your application");
    }

    private void writeResponse(HttpExchange t, int returnCode, String response) throws IOException {
        t.sendResponseHeaders(returnCode, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (final String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }
}
