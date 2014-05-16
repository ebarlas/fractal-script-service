package org.barlas.fractal.web;

import org.barlas.fractal.domain.Script;
import org.barlas.fractal.domain.User;
import org.barlas.fractal.service.ScriptService;
import org.barlas.fractal.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Controller
public class ScriptController {

    @Autowired
    private ThreadLocal<User> userHolder;
    @Autowired
    private ScriptService scriptService;
    @Autowired
    private TagService tagService;

    @ResponseBody
    @RequestMapping(value = "/scripts", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public String createScript(@Valid @RequestBody CreateScriptRequest request) {
        String userId = userHolder.get().getId();

        Script script = new Script();
        script.setId(UUID.randomUUID().toString());
        script.setName(request.getName());
        script.setDescription(request.getDescription());
        script.setScript(request.getScript());
        script.setUserId(userId);

        scriptService.createScript(script);

        if(request.getTags() != null && !request.getTags().isEmpty()) {
            tagService.createTags(userId, script.getId(), request.getTags());
        }

        return script.getId();
    }

    @ResponseBody
    @RequestMapping(value = "/scripts", method = RequestMethod.GET)
    public List<ScriptView> getScripts() {
        String userId = userHolder.get().getId();
        List<Script> scripts = scriptService.getScripts(userId);
        Map<String, Set<String>> scriptTags = tagService.getTagNames(userId);

        List<ScriptView> views = new ArrayList<ScriptView>();
        for(Script script : scripts) {
            views.add(convert(script, scriptTags.get(script.getId())));
        }

        return views;
    }

    private ScriptView convert(Script script, Set<String> tags) {
        ScriptView view = new ScriptView();
        view.setId(script.getId());
        view.setName(script.getName());
        view.setDescription(script.getDescription());
        view.setScript(script.getScript());
        view.setTags(tags);
        return view;
    }

}
