package org.barlas.fractal.web;

import org.barlas.fractal.domain.Script;
import org.barlas.fractal.domain.User;
import org.barlas.fractal.dynamo.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Controller
public class ScriptController {

    @Autowired
    private ThreadLocal<User> userHolder;
    @Autowired
    private ScriptService scriptService;

    @ResponseBody
    @RequestMapping(value = "/scripts", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public String createScript(@Valid @RequestBody CreateScriptRequest request) {
        Script script = new Script();
        script.setId(UUID.randomUUID().toString());
        script.setName(request.getName());
        script.setDescription(request.getDescription());
        script.setScript(request.getScript());
        script.setUserId(userHolder.get().getId());

        scriptService.createScript(script);
        return script.getId();
    }

    @ResponseBody
    @RequestMapping(value = "/scripts", method = RequestMethod.GET)
    public List<Script> getScripts() {
        return scriptService.getScripts(userHolder.get().getId());
    }

}
