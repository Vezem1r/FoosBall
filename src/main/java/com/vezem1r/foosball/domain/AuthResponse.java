package com.vezem1r.foosball.domain;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record AuthResponse(String token, UserDTO user, String message) {}
