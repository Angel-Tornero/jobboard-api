package com.jobboard.backend.config;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SimpleRateLimiterInterceptor implements HandlerInterceptor {

    private final Map<String, RequestInfo> requestCounts = new ConcurrentHashMap<>();
    private final long TIME_WINDOW = 60_000;
    private final int MAX_REQUESTS = 5;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = getClientIp(request);
        long now = Instant.now().toEpochMilli();

        requestCounts.putIfAbsent(clientIp, new RequestInfo(0, now));
        RequestInfo info = requestCounts.get(clientIp);

        if (now - info.startTime > TIME_WINDOW) {
            info.startTime = now;
            info.requestCount = 1;
        } else {
            info.requestCount++;
        }

        if (info.requestCount > MAX_REQUESTS) {
            response.setStatus(429); // too many requests
            response.getWriter().write("Rate limit exceeded");
            return false;
        }

        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        return xfHeader == null ? request.getRemoteAddr() : xfHeader.split(",")[0];
    }

    private static class RequestInfo {
        int requestCount;
        long startTime;

        RequestInfo(int requestCount, long startTime) {
            this.requestCount = requestCount;
            this.startTime = startTime;
        }
    }
}
