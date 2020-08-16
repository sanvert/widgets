package com.whiteboard.widgets.controller;

import com.whiteboard.widgets.service.RateLimiterService;
import com.whiteboard.widgets.util.Constants;
import com.whiteboard.widgets.util.Operation;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(assignableTypes = {WidgetController.class})
public class WidgetExceptionAdvice extends ResponseEntityExceptionHandler {

    private final RateLimiterService rateLimiterService;

    public WidgetExceptionAdvice(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @ExceptionHandler({ RequestNotPermitted.class })
    public ResponseEntity<Object> handleTimeoutException(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Object operationAttribute = request.getAttribute(Constants.OPERATION_KEYWORD, 0);
        if (operationAttribute != null) {
            String operation = ((Operation) operationAttribute).name();
            headers = rateLimiterService.generateHeaders(operation);
        }
        return new ResponseEntity<>("Rate limit is achieved.", headers, HttpStatus.TOO_MANY_REQUESTS);
    }

}
