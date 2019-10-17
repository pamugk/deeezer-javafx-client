package api.net;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

class CallbackServer {
    private final HttpServer server;
    private String callbackContext;
    private final CompletableFuture<String> oAuth2Code = new CompletableFuture<>();

    CallbackServer(String callbackContext) throws IOException {
        this.callbackContext = callbackContext;
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 80), 0);
        server.createContext(callbackContext, new OAuth2CallbackHandler(this));
        server.setExecutor(null);
    }

    String getAuthUrl() {
        return String.format("http://localhost%s", callbackContext);
    }

    void start() {
        server.start();
    }

    CompletableFuture<String> getOAuth2Code() {
        return oAuth2Code;
    }

    void setOAuth2Code(String value) {
        oAuth2Code.complete(value);
        server.stop(0);
    }
}

