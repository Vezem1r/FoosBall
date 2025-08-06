package com.vezem1r.foosball.service;

import com.vezem1r.foosball.domain.*;
import com.vezem1r.foosball.domain.AuthResponse;
import com.vezem1r.foosball.domain.AuthResponseBuilder;
import com.vezem1r.foosball.model.User;
import com.vezem1r.foosball.repo.UserRepository;
import com.vezem1r.foosball.security.JwtUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
  }

  public AuthResponse register(RegisterInput input) {
    log.info("Attempting to register user with username: {}", input.username());

    if (userRepository.existsByUsername(input.username())) {
      throw new RuntimeException("Username already exists");
    }

    if (userRepository.existsByEmail(input.email())) {
      throw new RuntimeException("Email already exists");
    }

    User user = User.builder()
            .username(input.username())
            .email(input.email())
            .passwordHash(passwordEncoder.encode(input.password()))
            .avatarUrl(input.avatarUrl())
            .role(User.Role.PLAYER)
            .createdAt(LocalDateTime.now())
            .isActive(true)
            .isLookingForTeam(false)
            .build();

    User savedUser = userRepository.save(user);

    String token =
        jwtUtil.generateToken(
            savedUser.getId(), savedUser.getUsername(), savedUser.getRole().name());

    log.info("Successfully registered user: {}", savedUser.getUsername());

    return AuthResponseBuilder.builder()
        .token(token)
        .user(UserDTO.fromUser(savedUser))
        .message("Registration successful")
        .build();
  }

  public AuthResponse login(LoginInput input) {
    log.info("Attempting login for: {}", input.usernameOrEmail());

    Optional<User> userOpt = findUserByUsernameOrEmail(input.usernameOrEmail());

    if (userOpt.isEmpty()) {
      throw new RuntimeException("Invalid credentials");
    }

    User user = userOpt.get();

    if (!passwordEncoder.matches(input.password(), user.getPasswordHash())) {
      throw new RuntimeException("Invalid credentials");
    }

    userRepository.updateLastLogin(user.getId());
    user.setLastLoginAt(LocalDateTime.now());

    String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());

    log.info("Successfully logged in user: {}", user.getUsername());

    return AuthResponseBuilder.builder()
        .token(token)
        .user(UserDTO.fromUser(user))
        .message("Login successful")
        .build();
  }

  @Transactional(readOnly = true)
  public Optional<User> findById(Long id) {
    return userRepository.findById(id);
  }

  @Transactional(readOnly = true)
  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  @Transactional(readOnly = true)
  public List<UserDTO> findUsersWithoutTeam() {
    return userRepository.findUsersWithoutTeam().stream().map(UserDTO::fromUser).toList();
  }

  @Transactional(readOnly = true)
  public List<UserDTO> findAllUsers() {
    return userRepository.findAll().stream().map(UserDTO::fromUser).toList();
  }

  private Optional<User> findUserByUsernameOrEmail(String usernameOrEmail) {
    Optional<User> userByUsername = userRepository.findByUsername(usernameOrEmail);
    if (userByUsername.isPresent()) {
      return userByUsername;
    }

    return userRepository.findByEmail(usernameOrEmail);
  }
}
