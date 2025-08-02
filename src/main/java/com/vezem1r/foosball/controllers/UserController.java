package com.vezem1r.foosball.controllers;

import com.vezem1r.foosball.domain.RegisterInput;
import com.vezem1r.foosball.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Component;

@Component
public class UserController {

    private final UserService userService;

    public UserController (UserService userService) {
        this.userService = userService;
    }

    @MutationMapping
    public Long registerUser(@Argument RegisterInput input) {
        return userService.register(input);
    }
}
