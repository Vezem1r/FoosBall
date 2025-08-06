package com.vezem1r.foosball.domain;

import jakarta.validation.constraints.NotBlank;

public record LoginInput(
    @NotBlank(message = "Username or email is required") String usernameOrEmail,
    @NotBlank(message = "Password is required") String password) {}
