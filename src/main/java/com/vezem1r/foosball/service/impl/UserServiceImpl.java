package com.vezem1r.foosball.service.impl;

import com.vezem1r.foosball.domain.RegisterInput;
import com.vezem1r.foosball.models.User;
import com.vezem1r.foosball.models.enums.Roles;
import com.vezem1r.foosball.repo.UserRepository;
import com.vezem1r.foosball.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Long register(RegisterInput input) {
    User user =
        User.builder()
            .username(input.username())
            .email(input.email())
            .avatarUrl(input.avatarUrl())
            .role(Roles.PLAYER)
            .build();

    return userRepository.upsert(user);
  }
}
