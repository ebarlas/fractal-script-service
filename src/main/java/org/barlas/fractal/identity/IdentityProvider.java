package org.barlas.fractal.identity;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClientException;

import javax.validation.constraints.NotNull;

@Validated
public interface IdentityProvider {

    /**
     * Get identity from access token
     * @param token access token
     * @return identity user identity
     * @throws RestClientException exception occurred querying identity
     */
    @NotNull Identity getIdentity(@NotEmpty @NotNull String token);

    /**
     * Get identity provider name
     * @return provider name
     */
    @NotNull String getName();

}
