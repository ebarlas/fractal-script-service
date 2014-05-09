package org.barlas.fractal.web;

import javax.validation.constraints.NotNull;
import java.util.List;

public class CreateScriptRequest {

    @NotNull
    private String script;
    @NotNull
    private String name;
    private String description;
    private List<String> tags;

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

}
