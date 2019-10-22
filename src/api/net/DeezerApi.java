package api.net;

import com.github.scribejava.core.builder.api.DefaultApi20;

class DeezerApi extends DefaultApi20 {
    private DeezerApi() {}

    private static class InstanceHolder {
        private static final DeezerApi INSTANCE = new DeezerApi();
    }

    static DeezerApi instance() {
        return InstanceHolder.INSTANCE;
    }

    static String getTokenEndpoint() { return "https://connect.deezer.com/oauth/access_token.php"; }

    @Override
    public String getAccessTokenEndpoint() {
        return getTokenEndpoint();
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return "https://connect.deezer.com/oauth/auth.php";
    }
}
