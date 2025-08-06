package com.vezem1r.foosball.repo;

import com.vezem1r.foosball.model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepository {

  private final JdbcTemplate jdbcTemplate;

  private static final String INSERT_USER_SQL =
      """
            INSERT INTO users (username, email, password_hash, avatar_url, oauth_provider, oauth_id, role, created_at, is_active)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

  private static final String GET_USER_BY_ID_SQL =
      """
            SELECT id, username, email, password_hash, avatar_url, oauth_provider, oauth_id,
                   role, created_at, last_login_at, is_looking_for_team, is_active
            FROM users
            WHERE id = ? AND is_active = true
            """;

  private static final String GET_USER_BY_USERNAME_SQL =
      """
            SELECT id, username, email, password_hash, avatar_url, oauth_provider, oauth_id,
                   role, created_at, last_login_at, is_looking_for_team, is_active
            FROM users
            WHERE username = ? AND is_active = true
            """;

  private static final String GET_USER_BY_EMAIL_SQL =
      """
            SELECT id, username, email, password_hash, avatar_url, oauth_provider, oauth_id,
                   role, created_at, last_login_at, is_looking_for_team, is_active
            FROM users
            WHERE email = ? AND is_active = true
            """;

  private static final String UPDATE_LAST_LOGIN_SQL =
      """
            UPDATE users
            SET last_login_at = ?
            WHERE id = ?
            """;

  private static final String GET_USERS_WITHOUT_TEAM_SQL =
      """
            SELECT u.id, u.username, u.email, u.password_hash, u.avatar_url, u.oauth_provider, u.oauth_id,
                   u.role, u.created_at, u.last_login_at, u.is_looking_for_team, u.is_active
            FROM users u
            WHERE u.is_looking_for_team = true
            AND u.is_active = true
            AND NOT EXISTS (
                SELECT 1 FROM teams t
                WHERE t.captain_id = u.id OR t.partner_id = u.id
            )
            """;

  private static final String GET_ALL_USERS_SQL =
      """
            SELECT id, username, email, password_hash, avatar_url, oauth_provider, oauth_id,
                   role, created_at, last_login_at, is_looking_for_team, is_active
            FROM users
            ORDER BY created_at DESC
            """;

  private static final String CHECK_USERNAME_EXISTS_SQL =
      """
            SELECT COUNT(*) FROM users WHERE username = ? AND is_active = true
            """;

  private static final String CHECK_EMAIL_EXISTS_SQL =
      """
            SELECT COUNT(*) FROM users WHERE email = ? AND is_active = true
            """;

  private final RowMapper<User> userRowMapper = new UserRowMapper();

  public User save(User user) {
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps = connection.prepareStatement(INSERT_USER_SQL, new String[] {"id"});
          ps.setString(1, user.getUsername());
          ps.setString(2, user.getEmail());
          ps.setString(3, user.getPasswordHash());
          ps.setString(4, user.getAvatarUrl());
          ps.setString(5, user.getOauthProvider());
          ps.setString(6, user.getOauthId());
          ps.setString(7, user.getRole().name().toLowerCase());
          ps.setTimestamp(8, Timestamp.valueOf(user.getCreatedAt()));
          ps.setBoolean(9, user.isActive());
          return ps;
        },
        keyHolder);

    user.setId(keyHolder.getKey().longValue());
    return user;
  }

  public Optional<User> findById(Long id) {
    try {
      User user = jdbcTemplate.queryForObject(GET_USER_BY_ID_SQL, userRowMapper, id);
      return Optional.ofNullable(user);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  public Optional<User> findByUsername(String username) {
    try {
      User user = jdbcTemplate.queryForObject(GET_USER_BY_USERNAME_SQL, userRowMapper, username);
      return Optional.ofNullable(user);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  public Optional<User> findByEmail(String email) {
    try {
      User user = jdbcTemplate.queryForObject(GET_USER_BY_EMAIL_SQL, userRowMapper, email);
      return Optional.ofNullable(user);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  public void updateLastLogin(Long userId) {
    jdbcTemplate.update(UPDATE_LAST_LOGIN_SQL, Timestamp.valueOf(LocalDateTime.now()), userId);
  }

  public List<User> findUsersWithoutTeam() {
    return jdbcTemplate.query(GET_USERS_WITHOUT_TEAM_SQL, userRowMapper);
  }

  public List<User> findAll() {
    return jdbcTemplate.query(GET_ALL_USERS_SQL, userRowMapper);
  }

  public boolean existsByUsername(String username) {
    Integer count = jdbcTemplate.queryForObject(CHECK_USERNAME_EXISTS_SQL, Integer.class, username);
    return count != null && count > 0;
  }

  public boolean existsByEmail(String email) {
    Integer count = jdbcTemplate.queryForObject(CHECK_EMAIL_EXISTS_SQL, Integer.class, email);
    return count != null && count > 0;
  }

  private static class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
      return User.builder()
          .id(rs.getLong("id"))
          .username(rs.getString("username"))
          .email(rs.getString("email"))
          .passwordHash(rs.getString("password_hash"))
          .avatarUrl(rs.getString("avatar_url"))
          .oauthProvider(rs.getString("oauth_provider"))
          .oauthId(rs.getString("oauth_id"))
          .role(User.Role.valueOf(rs.getString("role").toUpperCase()))
          .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
          .lastLoginAt(
              rs.getTimestamp("last_login_at") != null
                  ? rs.getTimestamp("last_login_at").toLocalDateTime()
                  : null)
          .isLookingForTeam(rs.getBoolean("is_looking_for_team"))
          .isActive(rs.getBoolean("is_active"))
          .build();
    }
  }
}
