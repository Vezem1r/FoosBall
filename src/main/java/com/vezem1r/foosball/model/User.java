package com.vezem1r.foosball.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
  private Long id;
  private String username;
  private String email;
  private String passwordHash;
  private String avatarUrl;
  private String oauthProvider;
  private String oauthId;
  private Role role;
  private LocalDateTime createdAt;
  private LocalDateTime lastLoginAt;
  private boolean isLookingForTeam;
  private boolean isActive;

  public enum Role {
    PLAYER,
    ADMIN
  }
}
