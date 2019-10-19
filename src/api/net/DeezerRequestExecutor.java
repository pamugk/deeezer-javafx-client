package api.net;

import api.events.authentication.AuthenticationEvent;
import api.events.base.DeezerEventHandler;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth20Service;

import javax.crypto.NoSuchPaddingException;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class DeezerRequestExecutor {
    private DeezerCallbackServer callbackServer;
    private DeezerTokenClient tokenClient;
    private OAuth20Service service;
    private Future<OAuth2AccessToken> authTokenF;
    private final String permissionsParam;
    private final SessionStorage storage;
    private DeezerEventHandler<AuthenticationEvent> authenticationEventHandler;

    private static final Map<Permissions, String> permissionCodes = Map.ofEntries(
            Map.entry(Permissions.BASIC_ACCESS, "basic_access"),
            Map.entry(Permissions.EMAIL, "email"),
            Map.entry(Permissions.OFFLINE_ACCESS, "offline_access"),
            Map.entry(Permissions.MANAGE_LIBRARY, "manage_library"),
            Map.entry(Permissions.MANAGE_COMMUNITY, "manage_community"),
            Map.entry(Permissions.DELETE_LIBRARY, "delete_library"),
            Map.entry(Permissions.LISTENING_HISTORY, "listening_history")
    );

    public DeezerRequestExecutor(String callbackContext,
        String apiKey, String apiSecret, List<Permissions> requiredPermissions)
            throws IOException, NoSuchAlgorithmException, NoSuchPaddingException {
        callbackServer = new DeezerCallbackServer(callbackContext);
        storage = new SessionStorage("storage");
        tokenClient = new DeezerTokenClient(apiKey, apiSecret, DeezerApi.getTokenEndpoint());
        service = new ServiceBuilder(apiKey)
                .apiSecret(apiSecret)
                .callback(callbackServer.getAuthUrl())
                .build(DeezerApi.instance());
        permissionsParam = String.format("perms=%s", summarizePermissions(requiredPermissions));
    }

    SessionStorage getStorage() { return storage; }

    private String summarizePermissions(List<Permissions> permissions) {
        return permissions.stream().map(permissionCodes::get).collect(Collectors.joining(","));
    }

    public void authenticate() throws IOException, URISyntaxException {
        callbackServer.start();
        authTokenF = callbackServer.getOAuth2Code().thenApply(oauthCode -> {
            boolean success = true;
            try {
                OAuth2AccessToken token = tokenClient.getAccessToken(oauthCode);
                storage.setAccessToken(token);
                return token;
            } catch (Exception e) {
                success = false;
                throw new CompletionException(e);
            }
            finally {
                authenticationEventHandler.invoke(new AuthenticationEvent(success));
            }
        });
        String authUrl = service.getAuthorizationUrl() + "&" + permissionsParam;
        Desktop.getDesktop().browse(new URI(authUrl));
    }

    public Response execute(OAuthRequest request) throws ExecutionException, InterruptedException, IOException {
        request.addParameter("access_token", authTokenF.get().getAccessToken());
        return service.execute(request);
    }

    public void setAuthenticationEventHandler(DeezerEventHandler<AuthenticationEvent> authenticationEventHandler) {
        this.authenticationEventHandler = authenticationEventHandler;
    }
}

