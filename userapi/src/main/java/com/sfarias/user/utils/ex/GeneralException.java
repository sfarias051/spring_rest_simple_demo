package com.sfarias.user.utils.ex;

import com.sfarias.user.utils.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class GeneralException extends RuntimeException {

    public GeneralException(String message) {
        super(Constants.ERROR_GENERAL + message);
    }
}
