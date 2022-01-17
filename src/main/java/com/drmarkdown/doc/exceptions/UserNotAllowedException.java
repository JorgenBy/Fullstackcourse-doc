package com.drmarkdown.doc.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserNotAllowedException extends Exception {
    private static final Logger logger = LoggerFactory.getLogger(UserNotAllowedException.class.getName());
    public UserNotAllowedException(String s) {
        super(s);
        logger.warn("sending message: " + s);
    }
}
