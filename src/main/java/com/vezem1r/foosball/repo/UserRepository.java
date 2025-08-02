package com.vezem1r.foosball.repo;

import com.vezem1r.foosball.models.User;
import lombok.NonNull;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

  private final NamedParameterJdbcTemplate jdbc;

  public UserRepository(NamedParameterJdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  private static final String UPSERT_SQL =
      """
        INSERT INTO users (username, email, avatar_url, role)
        VALUES (:username, :email, :avatarUrl, :role)
        ON CONFLICT (username, email) DO NOTHING
        RETURNING id
    """;

  public Long upsert(@NonNull User user) {
    MapSqlParameterSource params =
        new MapSqlParameterSource()
            .addValue("username", user.getUsername())
            .addValue("email", user.getEmail())
            .addValue("avatarUrl", user.getAvatarUrl())
            .addValue("role", user.getRole().name());

    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

    jdbc.update(UPSERT_SQL, params, keyHolder, new String[] {"id"});

    return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
  }
}
