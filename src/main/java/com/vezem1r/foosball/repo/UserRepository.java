package com.vezem1r.foosball.repo;

import com.vezem1r.foosball.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String UPSERT_SQL = """
        INSERT INTO users (username, email, avatar_url, role)
        VALUES (:username, :email, :avatarUrl, :role)
        ON CONFLICT (username, email) DO NOTHING
        RETURNING id
    """;

    public Long upsert(User user) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", user.getUsername());
        params.put("email", user.getEmail());
        params.put("avatarUrl", user.getAvatarUrl());
        params.put("role", user.getRole().name());

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(UPSERT_SQL, new String[] {"id"});
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getAvatarUrl());
            ps.setString(4, user.getRole().name());
            return ps;
        }, keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }
}
