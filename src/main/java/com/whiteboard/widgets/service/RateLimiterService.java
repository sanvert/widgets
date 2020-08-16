package com.whiteboard.widgets.service;

import com.whiteboard.widgets.model.RateLimit;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.internal.AtomicRateLimiter.AtomicRateLimiterMetrics;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

    private RateLimiterRegistry registry;

    public RateLimiterService() {
        RateLimiterConfig defaultConfig = RateLimiterConfig.custom()
            .timeoutDuration(Duration.ZERO)
            .limitRefreshPeriod(Duration.ofMinutes(1))
            .limitForPeriod(5).build();
        this.registry = RateLimiterRegistry.of(defaultConfig);
    }

    public void updateConfig(RateLimit limit) {
        registry.rateLimiter(limit.getOperation().name()).changeLimitForPeriod(limit.getLimit());
    }

    public HttpHeaders generateHeaders(String operation) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Rate-Limit", String.valueOf(getRateLimit(operation)));
        headers.add("X-Rate-Limit-Remaining", String.valueOf(getAvailablePermissions(operation)));
        headers.add("X-Rate-Limit-Time-To-Refresh", String.valueOf(getRemainingTimeForRefresh(operation)));
        return headers;
    }

    private int getRateLimit(String operation) {
        return registry.rateLimiter(operation).getRateLimiterConfig().getLimitForPeriod();
    }

    private int getAvailablePermissions(String operation) {
        return registry.rateLimiter(operation).getMetrics().getAvailablePermissions();
    }

    private long getRemainingTimeForRefresh(String operation) {
        return TimeUnit.NANOSECONDS
            .toSeconds(((AtomicRateLimiterMetrics) registry.rateLimiter(operation).getMetrics()).getNanosToWait());
    }

    public<I, O> Function<I, O> generateDecorator(String name, Function<I, O> rawFunction) {
        return RateLimiter.decorateFunction(registry.rateLimiter(name), rawFunction);
    }
}
