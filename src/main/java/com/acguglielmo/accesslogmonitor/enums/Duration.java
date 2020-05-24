package com.acguglielmo.accesslogmonitor.enums;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Duration {

    HOURLY("hourly"), 
    DAILY("daily");

    private String name;

    public static Duration getByName(final String pName) {
        return Arrays.stream(Duration.values())
                .filter(e -> e.name.equals(pName))
                .findFirst()
                .orElse(null);
    }
}
