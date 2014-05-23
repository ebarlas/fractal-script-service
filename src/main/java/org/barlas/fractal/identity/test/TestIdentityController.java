package org.barlas.fractal.identity.test;

import org.barlas.fractal.identity.Identity;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicReference;

@Controller
@Profile("test")
public class TestIdentityController {

    @Resource(name = "testIdentity")
    private AtomicReference<Identity> identityReference;

    @RequestMapping(value = "/test/identity", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setIdentity(@RequestBody Identity identity) {
        identityReference.set(identity);
    }

}
