package com.vezem1r.foosball.controllers;

import com.vezem1r.foosball.models.User;
import org.springframework.graphql.data.method.annotation.QueryMapping;

public class UserController {

    @QueryMapping
    public User getUserProfile() {
        return null;
    }
}
