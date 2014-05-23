package org.barlas.fractal.web;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import org.barlas.fractal.identity.Identity;
import org.barlas.fractal.service.dynamo.DynamoUserService;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public abstract class AbstractRestIntegrationTest {

    protected static final int EMAIL_PREFIX_LENGTH = 15;
    protected static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    protected static final String AUTHORIZATION_HEADER_VALUE = "Bearer 123";
    protected static final String EMAIL_SUFFIX = "@test.fractalservice.org";
    protected static final String TEST_IDENTITY_URI = "/test/identity";

    protected final String baseUrl;
    protected final RestTemplate restTemplate;
    protected final Random random;
    protected final AmazonDynamoDBClient dynamo;
    protected final DynamoUserService userService;

    protected AbstractRestIntegrationTest() {
        baseUrl = "http://localhost:8080";
        restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Arrays.asList((ClientHttpRequestInterceptor)new TokenHeaderInterceptor()));
        random = new Random();
        dynamo = new AmazonDynamoDBClient(new ClasspathPropertiesFileCredentialsProvider());
        dynamo.setRegion(Region.getRegion(Regions.US_WEST_1));
        userService = new DynamoUserService();
        ReflectionTestUtils.setField(userService, "usersTableName", "usersTest");
        ReflectionTestUtils.setField(userService, "dynamo", dynamo);
    }

    protected Identity setIdentity() {
        return setIdentity(randomHexString(), randomHexString(), randomEmail());
    }

    protected Identity setIdentity(String id, String name, String email) {
        Identity identity = new Identity();
        identity.setId(id);
        identity.setName(name);
        identity.setEmail(email);
        return setIdentity(identity);
    }

    protected Identity setIdentity(Identity identity) {
        restTemplate.put(baseUrl + TEST_IDENTITY_URI, identity);
        return identity;
    }

    protected String randomEmail() {
        return randomHexString(EMAIL_PREFIX_LENGTH) + EMAIL_SUFFIX;
    }

    protected String randomHexString() {
        return Long.toHexString(randomLong());
    }

    protected String randomHexString(int length) {
        StringBuilder sb = new StringBuilder();
        while(sb.length() < length) {
            sb.append(Long.toHexString(randomLong()));
        }
        sb.delete(length, sb.length());
        return sb.toString();
    }

    protected long randomLong() {
        return Math.abs(random.nextLong());
    }

    static class TokenHeaderInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            request.getHeaders().set(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE);
            return execution.execute(request, body);
        }
    }

}
