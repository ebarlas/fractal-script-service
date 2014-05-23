package org.barlas.fractal.identity.test;

import org.barlas.fractal.identity.Identity;
import org.barlas.fractal.identity.IdentityProvider;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.concurrent.atomic.AtomicReference;

public class TestIdentityProvider implements IdentityProvider {

    private static final String NETWORK = "test";

    @Resource(name = "testIdentity")
    private AtomicReference<Identity> identityReference;

    @Override
    public Identity getIdentity(@NotEmpty @NotNull String token) {
        return identityReference.get();
    }

    @Override
    public String getName() {
        return NETWORK;
    }

}
