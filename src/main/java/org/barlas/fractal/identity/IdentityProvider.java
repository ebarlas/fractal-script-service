package org.barlas.fractal.identity;

public interface IdentityProvider {

    /**
     * Get identity from access token
     * @param token access token, not null, not empty
     * @return identity, not null
     */
    Identity getIdentity(String token);

    /**
     * Get identity provider name
     * @return provider name, not null
     */
    String getName();

}
