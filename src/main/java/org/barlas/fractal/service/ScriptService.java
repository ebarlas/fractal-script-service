package org.barlas.fractal.service;

import org.barlas.fractal.domain.Script;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
public interface ScriptService {

    /**
     * Create script
     * @param script new script
     */
    void createScript(@NotNull Script script);

    /**
     * Get scripts for user
     * @param userId user id
     * @return scripts for user
     */
    @NotNull List<Script> getScripts(@NotNull @NotEmpty String userId);

}
