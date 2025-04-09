package api.net;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

class DeezerCallbackServer {
    private final HttpServer server;
    private final String authUrl;
    private CompletableFuture<String> oAuth2Code = new CompletableFuture<>();

    DeezerCallbackServer(String callbackContext, int port)
            throws IOException {
        authUrl = String.format("http://localhost:%d%s", port, callbackContext);
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), port), 0);
        server.createContext(callbackContext, new DeezerCallbackHandler(this));
        server.setExecutor(null);
        server.start();
    }

    String getAuthUrl() { return authUrl; }

    CompletableFuture<String> getOAuth2Code() {
        return oAuth2Code;
    }

    void resetOAuth2Code() {
        oAuth2Code = new CompletableFuture<>();
    }

    void setOAuth2Code(String value) {
        oAuth2Code.complete(value);
    }

    void stop() {
        server.stop(0);
    }
}

