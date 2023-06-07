package com.example.springbootmongodb.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

public class SkipPathRequestMatcher implements RequestMatcher {
    private OrRequestMatcher skipPathMatcher;
    private RequestMatcher processingMatcher;

    public SkipPathRequestMatcher(List<String> skipPaths, String processingPath) {
        Assert.notNull(skipPaths, "List of paths to skip is required.");
        processingMatcher = new AntPathRequestMatcher(processingPath);
        List<RequestMatcher> skipList = skipPaths.stream().map(skipPath -> new AntPathRequestMatcher(skipPath)).collect(Collectors.toList());
        skipPathMatcher = new OrRequestMatcher(skipList);
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        if (skipPathMatcher.matches(request)) {
            return false;
        }
        return processingMatcher.matches(request);
    }
}
