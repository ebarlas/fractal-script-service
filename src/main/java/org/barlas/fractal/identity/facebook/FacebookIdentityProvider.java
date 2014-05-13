package org.barlas.fractal.identity.facebook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.barlas.fractal.identity.AbstractIdentityProvider;
import org.barlas.fractal.identity.Identity;

public class FacebookIdentityProvider extends AbstractIdentityProvider {

    private static final String NETWORK = "facebook";
    private static final String API_BASE = "https://graph.facebook.com";
    private static final String ME_URI = "/v1.0/me";

    @Override
    public Identity getIdentity(String accessToken) {
        FacebookUser fu = getIdentity(API_BASE + ME_URI, accessToken, FacebookUser.class);
        return new Identity(fu.id, fu.name, fu.email);
    }

    @Override
    public String getName() {
        return NETWORK;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FacebookUser {
        @JsonProperty
        String id;
        @JsonProperty
        String name;
        @JsonProperty
        String email;
    }

}
