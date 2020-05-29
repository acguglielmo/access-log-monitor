package com.acguglielmo.accesslogmonitor.template;

import java.time.LocalDateTime;

import com.acguglielmo.accesslogmonitor.enums.Duration;
import com.acguglielmo.accesslogmonitor.threshold.Threshold;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;

public class ThresholdTemplateLoader implements TemplateLoader {

	@Override
    public void load() {

        Fixture.of(Threshold.class).addTemplate("2017-01-01.00:00:00, hourly, 1", new Rule() {{
            add("startDate", LocalDateTime.of(2017, 1, 1, 0, 0, 0));
            add("duration", Duration.HOURLY);
            add("limit", 1);
        }});

        Fixture.of(Threshold.class).addTemplate("2017-01-01.00:00:00, daily, 5", new Rule() {{
            add("startDate", LocalDateTime.of(2017, 1, 1, 0, 0, 0));
            add("duration", Duration.DAILY);
            add("limit", 5);
        }});

        Fixture.of(Threshold.class).addTemplate("2017-01-01.13:00:00, hourly, 34", new Rule() {{
            add("startDate", LocalDateTime.of(2017, 1, 1, 13, 0, 0));
            add("duration", Duration.HOURLY);
            add("limit", 34);
        }});

	}

}
