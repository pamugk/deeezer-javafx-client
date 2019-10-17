package api.net;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class RequestExecutor {
    private Future<OAuth2AccessToken> accessTokenF;
    private CallbackServer callbackServer;
    private OAuth20Service service;

    public RequestExecutor() throws IOException {
        callbackServer = new CallbackServer("/callback");
        service = new ServiceBuilder("374404")
                .apiSecret("4920657ae1441883c46939d8d55c3105")
                .defaultScope("listening_history")
                .callback(callbackServer.getAuthUrl())
                .build(DeezerApi.instance());
    }

    public void authenticate() throws IOException, URISyntaxException {
        callbackServer.start();
        accessTokenF = callbackServer.getOAuth2Code().thenApply(oauthCode -> {
            try {
                return service.getAccessToken(oauthCode);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
        String authUrl = service.getAuthorizationUrl();
        Desktop.getDesktop().browse(new URI(authUrl));
    }

    Response execute(OAuthRequest request) throws ExecutionException, InterruptedException, IOException {
        service.signRequest(accessTokenF.get(), request);
        return service.execute(request);
    }
}

