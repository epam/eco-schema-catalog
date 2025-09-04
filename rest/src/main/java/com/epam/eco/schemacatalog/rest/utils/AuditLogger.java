package com.epam.eco.schemacatalog.rest.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class AuditLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogger.class);
    private static final String AUDIT_PREFIX = "AUDIT:";
    public static final String VERSION_PREFIX = " | Version : ";

    public static void logSchemaRegistration(
            String subject,
            Integer version
    ) {
        logAction("REGISTER_SCHEMA", subject, version);
    }

    public static void logSchemaDeletion(
            String subject,
            Integer version
    ) {
        logAction("DELETE_SCHEMA", subject, version);
    }

    public static void logSubjectDeletion(String subject) {
        logAction("DELETE_SUBJECT", subject, null);
    }

    private static void logAction(
            String action,
            String subject,
            Integer version
    ) {
        try {
            String username = getCurrentUser();
            String ip = getClientIP();

            String logMessage = String.format(
                    "%s User: %s | IP: %s | Action: %s | Subject: %s%s",
                    AUDIT_PREFIX,
                    username != null ? username : "anonymous",
                    ip != null ? ip : "unknown",
                    action,
                    subject,
                    version != null ? VERSION_PREFIX + version : EMPTY
            );

            LOGGER.info(logMessage);
        } catch (Exception e) {
            LOGGER.error("Failed to log audit message", e);
        }
    }

    private static String getCurrentUser() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

    }

    private static String getClientIP() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes())
                .getRequest();

        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}
