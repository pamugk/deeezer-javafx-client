package api.net;

import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.jdk.JDKHttpClient;
import com.github.scribejava.core.httpclient.jdk.JDKHttpClientConfig;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.AccessTokenRequestParams;
import com.github.scribejava.core.oauth2.clientauthentication.HttpBasicAuthenticationScheme;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

class DeezerTokenClient  implements Closeable {
    private final HttpClient httpClient;
    private final String tokenEndpoint;
    private final String apiKey;
    private final String apiSecret;

    DeezerTokenClient(String apiKey, String apiSecret, String tokenEndpoint) {
        httpClient = new JDKHttpClient(JDKHttpClientConfig.defaultConfig());
        this.tokenEndpoint = tokenEndpoint;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    OAuth2AccessToken getAccessToken(String code) throws IOException {
        AccessTokenRequestParams params = AccessTokenRequestParams.create(code);
        OAuthRequest request = createAccessTokenRequest(params);
        try (Response response = execute(request)) {
            String rawResponse = response.getBody();
            String access_token = rawResponse.split("&")[0].split("=")[1];
            return new OAuth2AccessToken(access_token, rawResponse);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException();
        }
    }

    private OAuthRequest createAccessTokenRequest(AccessTokenRequestParams params) {
        final OAuthRequest request = new OAuthRequest(Verb.GET, tokenEndpoint);

        HttpBasicAuthenticationScheme.instance().addClientAuthentication(request, apiKey, apiSecret);

        request.addParameter("app_id", apiKey);
        request.addParameter("secret", apiSecret);
        request.addParameter("code", params.getCode());
        request.addParameter(OAuthConstants.GRANT_TYPE, OAuthConstants.AUTHORIZATION_CODE);

        return request;
    }

    private Response execute(OAuthRequest request) throws InterruptedException, ExecutionException, IOException {
        return httpClient.execute(null, request.getHeaders(), request.getVerb(), request.getCompleteUrl(),
                request.getByteArrayPayload());
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }
}
