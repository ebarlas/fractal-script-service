package org.barlas.fractal.identity;

import org.apache.log4j.Logger;
import org.barlas.fractal.domain.User;
import org.barlas.fractal.dynamo.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.GenericFilterBean;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IdentityFilter extends GenericFilterBean {

    private final Logger logger = Logger.getLogger(getClass());

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String OAUTH_PROVIDER = "OAuth-Provider";
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\s*Bearer\\s*(.*)", Pattern.CASE_INSENSITIVE);

    @Resource(name = "idps")
    private List<? extends IdentityProvider> idps;
    @Autowired
    private UserService userService;
    @Autowired
    private ThreadLocal<User> userHolder;

    private Map<String, IdentityProvider> idpTable = new HashMap<String, IdentityProvider>();

    @PostConstruct
    public void init() {
        for(IdentityProvider idp : idps) {
            idpTable.put(idp.getName().toLowerCase(), idp);
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;

        // get authorization header
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        if(authorizationHeader == null) {
            throw new UnauthenticatedException();
        }

        // parse bearer token
        Matcher matcher = TOKEN_PATTERN.matcher(authorizationHeader);
        if(!matcher.matches()) {
            throw new UnauthenticatedException();
        }

        // extract access token from authorization header
        String accessToken = matcher.group(1);

        // examine provider header
        IdentityProvider idp = null;
        String providerHeader = request.getHeader(OAUTH_PROVIDER);
        if(providerHeader != null) {
            idp = idpTable.get(providerHeader);
        }

        // fallback to idp[0]
        if(idp == null) {
            idp = idps.get(0);
        }

        // get identity
        Identity identity = idp.getIdentity(accessToken);

        // query user by email
        User user = userService.getUserByEmail(identity.getEmail());

        // create new user, if needed
        if(user == null) {
            user = new User();
            user.setId(UUID.randomUUID().toString());
            user.setName(identity.getName());
            user.setEmail(identity.getEmail());
            userService.createUser(user);

            logger.info("created user with social network, userId=" + user.getId());
        }

        // set user context
        userHolder.set(user);

        filterChain.doFilter(req, res);
    }

}
