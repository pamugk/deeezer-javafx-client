package api.events.authentication;

import api.events.DeezerEvent;

public class AuthenticationEvent extends DeezerEvent {
    private final boolean loggedIn;

    public AuthenticationEvent(boolean login) {
        this.loggedIn = login;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
}
