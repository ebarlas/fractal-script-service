package org.barlas.fractal.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestOperations;

public class GoogleService {

    private static final String GOOGLE_API_BASE = "https://www.googleapis.com";
    private static final String GOOGLE_PLUS_ME_URI = "/plus/v1/people/me";
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String BEARER_TYPE = "Bearer";
    private static final String DELIMITER = " ";

    @Autowired
    private RestOperations restOperations;

    public GoogleUser getIdentity(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION_HEADER_NAME, BEARER_TYPE + DELIMITER + accessToken);
        return restOperations.exchange(GOOGLE_API_BASE + GOOGLE_PLUS_ME_URI, HttpMethod.GET, new HttpEntity(headers), GoogleUser.class).getBody();
    }

}
