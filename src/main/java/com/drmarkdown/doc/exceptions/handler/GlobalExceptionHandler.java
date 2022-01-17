package com.drmarkdown.doc.exceptions.handler;

import com.drmarkdown.doc.exceptions.FaultyInputException;
import com.drmarkdown.doc.exceptions.UserNotAllowedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;


@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class.getName());

    @ExceptionHandler({UserNotAllowedException.class})
    public ResponseEntity<Object> handleUserNotAllowedException(final UserNotAllowedException e, final WebRequest webRequest) {
        return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN, webRequest);
    }

    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<Object> handleNoSuchElementException(final UserNotAllowedException e, final WebRequest webRequest) {
        return handleExceptionInternal(e, "You are not allowed to execute this action", new HttpHeaders(), HttpStatus.NOT_FOUND, webRequest);
    }

    @ExceptionHandler({FaultyInputException.class})
    public ResponseEntity<Object> handleFaultyInputException(final FaultyInputException e, final WebRequest webRequest) {
        logger.warn("faulty input " + e.getMessage());
        return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);
    }
}