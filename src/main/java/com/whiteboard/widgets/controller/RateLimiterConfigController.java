package com.whiteboard.widgets.controller;

import com.whiteboard.widgets.model.RateLimit;
import com.whiteboard.widgets.service.RateLimiterService;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rateLimit")
public class RateLimiterConfigController {
    private RateLimiterService rateLimiterService;

    public RateLimiterConfigController(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @PutMapping
    public ResponseEntity<Void> updateRateLimit(@Valid RateLimit limit) {
        rateLimiterService.updateConfig(limit);
        return ResponseEntity.noContent().build();
    }
}
