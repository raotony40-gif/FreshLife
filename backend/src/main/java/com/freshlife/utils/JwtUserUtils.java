package com.freshlife.utils;

import com.freshlife.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtUserUtils {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtils jwtUtils;

    public JwtUserUtils(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    public Long getCurrentUserId(HttpServletRequest request) {
        String authorization = request == null ? null : request.getHeader(AUTHORIZATION_HEADER);
        String token = parseToken(authorization);
        if (!jwtUtils.validateToken(token)) {
            throw new BusinessException(401, "token无效");
        }

        try {
            return Long.valueOf(jwtUtils.getSubject(token));
        } catch (Exception exception) {
            throw new BusinessException(401, "token无效");
        }
    }

    private String parseToken(String authorization) {
        if (!StringUtils.hasText(authorization) || !authorization.startsWith(BEARER_PREFIX)) {
            throw new BusinessException(401, "token无效");
        }
        return authorization.substring(BEARER_PREFIX.length());
    }
}
