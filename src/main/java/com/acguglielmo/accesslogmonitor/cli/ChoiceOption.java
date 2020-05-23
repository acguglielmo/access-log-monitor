package com.acguglielmo.accesslogmonitor.cli;

import org.apache.commons.cli.Option;

import java.util.Arrays;

public class ChoiceOption extends Option {
	private static final long serialVersionUID = 723485642511703539L;
	
	private final String[] choices;

    ChoiceOption(
            final String opt,
            final String longOpt,
            final boolean hasArg,
            final String description,
            boolean isRequired,
            final String... choices) throws IllegalArgumentException {
        super(opt, longOpt, hasArg, description + ' ' + Arrays.toString(choices));
        super.setRequired(isRequired);
        this.choices = choices;
    }

    void checkChoiceValue() throws RuntimeException {
        final String value = super.getValue();
        if (Arrays.stream(choices).noneMatch(s -> s.equals(value))) {
            throw new RuntimeException( "Invalid choice for the option " + this.getLongOpt() + ":" + value);
        }
    }
}