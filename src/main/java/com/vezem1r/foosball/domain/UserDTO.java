package com.vezem1r.foosball.domain;

import com.vezem1r.foosball.model.User;
import io.soabase.recordbuilder.core.RecordBuilder;
import java.time.LocalDateTime;

@RecordBuilder
public record UserDTO(
    Long id,
    String username,
    String email,
    String avatarUrl,
    String role,
    LocalDateTime createdAt,
    LocalDateTime lastLoginAt,
    boolean isLookingForTeam,
    boolean isActive) {

  public static UserDTO fromUser(User user) {
    return UserDTOBuilder.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .avatarUrl(user.getAvatarUrl())
        .role(user.getRole().name())
        .createdAt(user.getCreatedAt())
        .lastLoginAt(user.getLastLoginAt())
        .isLookingForTeam(user.isLookingForTeam())
        .isActive(user.isActive())
        .build();
  }
}
