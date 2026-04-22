package com.wave.user_service.utils.exceptions;

import org.springframework.security.core.AuthenticationException;

public class UserAlreadyExistException extends AuthenticationException {

    public UserAlreadyExistException(final String msg) {
        super(msg);
    }

}
