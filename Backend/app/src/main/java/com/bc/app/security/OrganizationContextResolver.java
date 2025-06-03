package com.bc.app.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Component
public class OrganizationContextResolver {

    /**
     * Extract organization ID from path variable
     * For URLs like /api/organizations/{organizationId}/...
     */
    public Optional<Long> getOrganizationIdFromPath() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return Optional.empty();
        }

        String uri = request.getRequestURI();
        
        // Extract organizationId from common patterns
        if (uri.contains("/organizations/")) {
            return extractIdAfterPattern(uri, "/organizations/");
        }
        
        if (uri.contains("/org/")) {
            return extractIdAfterPattern(uri, "/org/");
        }

        return Optional.empty();
    }

    /**
     * Extract organization ID from request parameter
     */
    public Optional<Long> getOrganizationIdFromParameter() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return Optional.empty();
        }

        String orgIdParam = request.getParameter("organizationId");
        if (orgIdParam == null) {
            orgIdParam = request.getParameter("orgId");
        }

        if (orgIdParam != null) {
            try {
                return Optional.of(Long.parseLong(orgIdParam));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    /**
     * Extract organization ID from request header
     */
    public Optional<Long> getOrganizationIdFromHeader() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return Optional.empty();
        }

        String orgIdHeader = request.getHeader("X-Organization-Id");
        if (orgIdHeader == null) {
            orgIdHeader = request.getHeader("Organization-Id");
        }

        if (orgIdHeader != null) {
            try {
                return Optional.of(Long.parseLong(orgIdHeader));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    /**
     * Try to resolve organization ID from path, parameter, or header (in that order)
     */
    public Optional<Long> resolveOrganizationId() {
        return getOrganizationIdFromPath()
                .or(this::getOrganizationIdFromParameter)
                .or(this::getOrganizationIdFromHeader);
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private Optional<Long> extractIdAfterPattern(String uri, String pattern) {
        int index = uri.indexOf(pattern);
        if (index == -1) {
            return Optional.empty();
        }

        int start = index + pattern.length();
        int end = uri.indexOf('/', start);
        if (end == -1) {
            end = uri.length();
        }

        String idStr = uri.substring(start, end);
        try {
            return Optional.of(Long.parseLong(idStr));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
} 