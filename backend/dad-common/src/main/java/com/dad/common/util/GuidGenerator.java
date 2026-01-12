package com.dad.common.util;

import java.util.UUID;

/**
 * GUID Generator
 */
public class GuidGenerator {

    /**
     * Generate random GUID (UUID without hyphens)
     *
     * @return GUID string
     */
    public static String generateGuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Generate GUID with prefix
     *
     * @param prefix Prefix (e.g., "dashboard-", "chart-")
     * @return GUID string with prefix
     */
    public static String generateGuid(String prefix) {
        return prefix + generateGuid();
    }

    /**
     * Generate short GUID (8 characters)
     *
     * @return Short GUID string
     */
    public static String generateShortGuid() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}
