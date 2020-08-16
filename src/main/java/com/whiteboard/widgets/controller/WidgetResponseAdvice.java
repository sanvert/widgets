package com.whiteboard.widgets.controller;

import com.whiteboard.widgets.service.RateLimiterService;
import com.whiteboard.widgets.util.Constants;
import com.whiteboard.widgets.util.Operation;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice(assignableTypes = {WidgetController.class})
public class WidgetResponseAdvice implements ResponseBodyAdvice<Object> {

    private final RateLimiterService rateLimiterService;

    public WidgetResponseAdvice(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
        Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
        ServerHttpResponse response) {
        Object operationAttribute = ((ServletServerHttpRequest) request).getServletRequest().getAttribute(Constants.OPERATION_KEYWORD);
        if (operationAttribute != null) {
            String operation = ((Operation) operationAttribute).name();
            response.getHeaders().addAll(rateLimiterService.generateHeaders(operation));
        }
        return body;
    }
}
