package api;

public class Configuration {
    private final String callbackContext;
    private final String apiKey;
    private final String apiSecret;

    public Configuration(String callbackContext, String apiKey, String apiSecret) {
        this.callbackContext = callbackContext;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public String getCallbackContext() {
        return callbackContext;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }
}
