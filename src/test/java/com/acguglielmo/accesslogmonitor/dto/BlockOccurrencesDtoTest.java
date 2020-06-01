package com.acguglielmo.accesslogmonitor.dto;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BlockOccurrencesDtoTest {

    @Test
    public void shouldPrintNicelyFormattedStringTest() throws Exception {

        final BlockOccurrencesDto dto = BlockOccurrencesDto.builder()
            .ip("192.168.0.1")
            .count(100)
            .build();

        assertEquals("192.168.0.1       100", dto.toString());

    }

}
