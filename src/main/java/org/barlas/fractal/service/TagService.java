package org.barlas.fractal.service;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

@Validated
public interface TagService {

    /**
     * Create new tags for user script
     * @param userId user id
     * @param scriptId script id
     * @param tagNames tag names
     */
    void createTags(@NotNull @NotEmpty String userId, @NotNull @NotEmpty String scriptId, @NotNull @NotEmpty Set<String> tagNames);

    /**
     * Get tag names for user, indexed by script id
     * @param userId user id
     * @return tag names indexes by script id
     */
    @NotNull Map<String, Set<String>> getTagNames(@NotNull @NotEmpty String userId);

    /**
     * Get script tags
     * @param scriptId script id
     * @return tag names
     */
    @NotNull Set<String> getTags(@NotNull @NotEmpty String scriptId);

}
