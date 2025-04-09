package api;

public record Configuration(
        String callbackContext,
        String apiKey,
        String apiSecret
) {
}
