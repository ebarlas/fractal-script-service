package org.barlas.fractal.identity;

import org.apache.log4j.Logger;
import org.barlas.fractal.domain.SocialNetwork;
import org.barlas.fractal.domain.User;
import org.barlas.fractal.dynamo.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IdentityFilter extends GenericFilterBean {

    private final Logger logger = Logger.getLogger(getClass());

    private static final String GOOGLE_NETWORK = "google";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\s*Bearer\\s*(.*)", Pattern.CASE_INSENSITIVE);

    @Autowired
    private GoogleService googleService;
    @Autowired
    private UserService userService;
    @Autowired
    private ThreadLocal<User> userHolder;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;

        // get auth header
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if(header == null) {
            throw new UnauthenticatedException();
        }

        // parse bearer token
        Matcher matcher = TOKEN_PATTERN.matcher(header);
        if(!matcher.matches()) {
            throw new UnauthenticatedException();
        }

        // query google plus identity
        String accessToken = matcher.group(1);
        GoogleUser googleUser = googleService.getIdentity(accessToken);

        // query user by social id
        String userId = userService.getUserId(googleUser.getId());

        // query complete user, if possible
        User user = null;
        if(userId != null) {
            user = userService.getUser(userId);
        }

        // create new user, if needed
        if(user == null) {
            user = new User();
            user.setId(UUID.randomUUID().toString());
            user.setDisplayName(googleUser.getDisplayName());
            userService.createUser(user);

            SocialNetwork socialNetwork = new SocialNetwork();
            socialNetwork.setId(googleUser.getId());
            socialNetwork.setNetwork(GOOGLE_NETWORK);
            socialNetwork.setUserId(user.getId());
            userService.createSocialNetwork(socialNetwork);

            logger.info("created user with social network, userId=" + user.getId() + ", socialId=" + socialNetwork.getId());
        }

        // set user context
        userHolder.set(user);

        filterChain.doFilter(req, res);
    }

}
