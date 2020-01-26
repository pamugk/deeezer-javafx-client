package api.events.authentication;

import api.events.DeezerEvent;

public class AuthenticationEvent extends DeezerEvent {
    private final boolean authenticationSuccessful;

    public AuthenticationEvent(boolean authenticationSuccessful) {
        this.authenticationSuccessful = authenticationSuccessful;
    }

    public boolean isAuthenticationSuccessful() {
        return authenticationSuccessful;
    }
}
