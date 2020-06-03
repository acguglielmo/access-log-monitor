package com.acguglielmo.accesslogmonitor.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ProgressBarTest {

    @Test
    public void shouldReturnCorrectProgressBarAsStringTest() throws Exception {
        
        assertEquals("|=====                                             | 10% Done", ProgressBar.of(10.0).toString());

        assertEquals("|=======                                           | 15% Done", ProgressBar.of(15.0).toString());

        assertEquals("|===================================               | 70% Done", ProgressBar.of(70.0).toString());

    }
    
}
