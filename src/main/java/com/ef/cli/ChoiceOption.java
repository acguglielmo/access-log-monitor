package com.ef.cli;

import org.apache.commons.cli.Option;

import java.util.Arrays;

/**
 * The type Choice option.
 */
public class ChoiceOption extends Option {
    private final String[] choices;

    /**
     * Instantiates a new Choice option.
     *
     * @param opt         the opt
     * @param longOpt     the long opt
     * @param hasArg      the has arg
     * @param description the description
     * @param isRequired  the is required
     * @param choices     the choices
     * @throws IllegalArgumentException the illegal argument exception
     */
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

    /**
     * Check choice value.
     *
     * @throws RuntimeException the runtime exception
     */
    void checkChoiceValue() throws RuntimeException {
        final String value = super.getValue();
        if (Arrays.stream(choices).noneMatch(s -> s.equals(value))) {
            throw new RuntimeException( "Invalid choice for the option " + this.getLongOpt() + ":" + value);
        }
    }
}