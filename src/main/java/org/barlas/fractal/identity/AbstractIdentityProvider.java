package org.barlas.fractal.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestOperations;

public abstract class AbstractIdentityProvider implements IdentityProvider {

    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String BEARER_FORMAT = "Bearer %s";

    @Autowired
    private RestOperations restOperations;

    protected <T> T getIdentity(String url, String token, Class<T> clazz) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION_HEADER_NAME, String.format(BEARER_FORMAT, token));
        return restOperations.exchange(url, HttpMethod.GET, new HttpEntity(headers), clazz).getBody();
    }

}
