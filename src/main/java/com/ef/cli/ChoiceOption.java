package com.ef.cli;

import org.apache.commons.cli.Option;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Arrays;

public class ChoiceOption extends Option {
    private final String[] choices;

    public ChoiceOption(
            final String opt,
            final String longOpt,
            final boolean hasArg,
            final String description,
            final String... choices) throws IllegalArgumentException {
        super(opt, longOpt, hasArg, description + ' ' + Arrays.toString(choices));
        this.choices = choices;
    }

    public void checkChoiceValue() throws RuntimeException {
        final String value = super.getValue();
        if (value == null) {
            throw new RuntimeException("A value must be informed for the option " + this.getLongOpt());
        }
        if (!Arrays.stream(choices).anyMatch(s -> s.equals(value))) {
            throw new RuntimeException( "Invalid choice for the option " + this.getLongOpt() + ":" + value);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return new EqualsBuilder().appendSuper(super.equals(o))
                .append(choices, ((ChoiceOption) o).choices)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(choices).toHashCode();
    }
}