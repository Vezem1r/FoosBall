package com.vezem1r.foosball.service;

import com.vezem1r.foosball.domain.RegisterInput;

public interface UserService {

  Long register(RegisterInput input);
}
