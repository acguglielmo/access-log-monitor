package com.ef.enums;

import java.util.Arrays;

public enum Duration {
    HOURLY("hourly"), DAILY("daily");

    private String name;

    private Duration(final String name) {
        this.name = name;
    }

    public static Duration getByName(final String pName) {
        return Arrays.stream(Duration.values())
                .filter(e -> e.name.equals(pName))
                .findFirst()
                .orElse(null);
    }

    public String getName() {
        return name;
    }
}
