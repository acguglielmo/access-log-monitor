package com.acguglielmo.accesslogmonitor.cli;

import static com.acguglielmo.accesslogmonitor.cli.CommandLineHelper.DURATION;
import static com.acguglielmo.accesslogmonitor.cli.CommandLineHelper.START_DATE;
import static com.acguglielmo.accesslogmonitor.cli.CommandLineHelper.THRESHOLD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.apache.commons.cli.CommandLine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.acguglielmo.accesslogmonitor.threshold.HourlyThreshold;
import com.acguglielmo.accesslogmonitor.threshold.Threshold;

@ExtendWith(MockitoExtension.class)
public class ApplicationCommandLineTest {

    @Mock
    private CommandLine commandLine;
    
    @InjectMocks
    private ApplicationCommandLine instance;
    
    @Test
    public void shouldReturnCorrectlyConfiguredThresold() throws Exception {

        when( commandLine.getOptionValue(DURATION) )
        	.thenReturn("hourly");
    	
        when( commandLine.getOptionValue(START_DATE) )
            .thenReturn("2018-01-01.00:00:00");
        
        when( commandLine.getOptionValue(THRESHOLD) )
            .thenReturn("100");

        final Threshold result = instance.to();

        assertNotNull(result);

        assertEquals(LocalDateTime.of(2018, 1, 1, 0, 0, 0), result.getStartDate());

        assertTrue(result.getClass().isAssignableFrom(HourlyThreshold.class));
        
        assertEquals(100, result.getLimit());

    }

}
