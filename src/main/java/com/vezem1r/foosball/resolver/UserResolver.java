package com.vezem1r.foosball.resolver;

import com.vezem1r.foosball.domain.AuthResponse;
import com.vezem1r.foosball.domain.LoginInput;
import com.vezem1r.foosball.domain.RegisterInput;
import com.vezem1r.foosball.domain.UserDTO;
import com.vezem1r.foosball.service.UserService;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Controller
@Slf4j
@Validated
public class UserResolver extends DataFetcherExceptionResolverAdapter {

  private final UserService userService;

  public UserResolver(UserService userService) {
    this.userService = userService;
  }

  @MutationMapping
  public AuthResponse registerUser(@Argument @Valid RegisterInput input) {
    try {
      return userService.register(input);
    } catch (Exception e) {
      log.error("Registration failed for username: {}", input.username(), e);
      throw new RuntimeException("Registration failed: " + e.getMessage());
    }
  }

  @MutationMapping
  public AuthResponse loginUser(@Argument @Valid LoginInput input) {
    try {
      return userService.login(input);
    } catch (Exception e) {
      log.error("Login failed for: {}", input.usernameOrEmail(), e);
      throw new RuntimeException("Login failed: " + e.getMessage());
    }
  }

  @QueryMapping
  public List<UserDTO> usersWithoutTeam() {
    try {
      return userService.findUsersWithoutTeam();
    } catch (Exception e) {
      log.error("Failed to fetch users without team", e);
      throw new RuntimeException("Failed to fetch users without team: " + e.getMessage());
    }
  }

  @QueryMapping
  public List<UserDTO> allUsers() {
    try {
      return userService.findAllUsers();
    } catch (Exception e) {
      log.error("Failed to fetch all users", e);
      throw new RuntimeException("Failed to fetch all users: " + e.getMessage());
    }
  }

  @Override
  protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
    if (ex instanceof RuntimeException) {
      return GraphqlErrorBuilder.newError()
          .errorType(ErrorType.BAD_REQUEST)
          .message(ex.getMessage())
          .path(env.getExecutionStepInfo().getPath())
          .location(env.getField().getSourceLocation())
          .build();
    }

    return GraphqlErrorBuilder.newError()
        .errorType(ErrorType.INTERNAL_ERROR)
        .message("Internal server error")
        .path(env.getExecutionStepInfo().getPath())
        .location(env.getField().getSourceLocation())
        .build();
  }
}
