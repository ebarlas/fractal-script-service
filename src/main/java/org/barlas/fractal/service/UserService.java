package org.barlas.fractal.service;

import org.barlas.fractal.domain.User;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Validated
public interface UserService {

    /**
     * Create new user
     * @param user new user
     */
    void createUser(@NotNull User user);

    /**
     * Get user by email
     * @param email user email
     * @return user
     */
    User getUserByEmail(@NotNull @NotEmpty String email);

}
