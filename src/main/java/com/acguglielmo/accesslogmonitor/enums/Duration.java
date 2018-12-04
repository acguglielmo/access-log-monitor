package com.acguglielmo.accesslogmonitor.enums;

import java.util.Arrays;

/**
 * The enum Duration.
 */
public enum Duration {
    /**
     * Hourly duration.
     */
    HOURLY("hourly"), /**
     * Daily duration.
     */
    DAILY("daily");

    private String name;

    Duration(final String name) {
        this.name = name;
    }

    /**
     * Gets by name.
     *
     * @param pName the p name
     * @return the by name
     */
    public static Duration getByName(final String pName) {
        return Arrays.stream(Duration.values())
                .filter(e -> e.name.equals(pName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }
}
