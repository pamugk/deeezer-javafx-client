package api;

import api.net.RequestExecutor;
import api.objects.utils.User;

import java.io.IOException;
import java.net.URISyntaxException;

public class Deezer {
    private final RequestExecutor requestExecutor;
    private static final String apiUrlPrefix = "https://api.deezer.com/";

    public Deezer() throws IOException {
        requestExecutor = new RequestExecutor();
    }

    public User login() throws IOException, URISyntaxException {
        requestExecutor.authenticate();
        return null;
    }

    public void logout() {

    }
}
