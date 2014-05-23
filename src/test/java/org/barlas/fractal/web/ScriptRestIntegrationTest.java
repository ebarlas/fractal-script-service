package org.barlas.fractal.web;

import org.barlas.fractal.domain.User;
import org.barlas.fractal.identity.Identity;
import org.junit.Assert;
import org.junit.Test;

public class ScriptRestIntegrationTest extends AbstractRestIntegrationTest {

    private static final String SCRIPTS_URI = "/scripts";

    @Test
    public void testGetScripts() {
        Identity identity = setIdentity();
        ScriptView[] scripts = restTemplate.getForObject(baseUrl + SCRIPTS_URI, ScriptView[].class);
        Assert.assertEquals(0, scripts.length);
        User user = userService.getUserByEmail(identity.getEmail());
        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getId());
        Assert.assertEquals(identity.getEmail(), user.getEmail());
        Assert.assertEquals(identity.getName(), user.getName());
    }

}
