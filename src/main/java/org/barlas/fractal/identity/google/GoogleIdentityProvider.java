package org.barlas.fractal.identity.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.barlas.fractal.identity.AbstractIdentityProvider;
import org.barlas.fractal.identity.Identity;

public class GoogleIdentityProvider extends AbstractIdentityProvider {

    private static final String NETWORK = "google";
    private static final String API_BASE = "https://www.googleapis.com";
    private static final String PLUS_ME_URI = "/userinfo/v2/me";

    @Override
    public Identity getIdentity(String token) {
        GoogleUser gu = getIdentity(API_BASE + PLUS_ME_URI, token, GoogleUser.class);
        return new Identity(gu.id, gu.name, gu.email);
    }

    @Override
    public String getName() {
        return NETWORK;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GoogleUser {
        @JsonProperty
        String id;
        @JsonProperty
        String name;
        @JsonProperty
        String email;
    }

}
