package com.acguglielmo.accesslogmonitor.template;

import java.time.LocalDateTime;

import com.acguglielmo.accesslogmonitor.threshold.HourlyThreshold;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;

public class HourlyThresholdTemplateLoader implements TemplateLoader {

	@Override
    public void load() {

        Fixture.of(HourlyThreshold.class).addTemplate("2017-01-01.00:00:00, 1", new Rule() {{
            add("startDate", LocalDateTime.of(2017, 1, 1, 0, 0, 0));
            add("limit", 1);
        }});

        Fixture.of(HourlyThreshold.class).addTemplate("2017-01-01.00:00:00, 5", new Rule() {{
            add("startDate", LocalDateTime.of(2017, 1, 1, 0, 0, 0));
            add("limit", 5);
        }});

        Fixture.of(HourlyThreshold.class).addTemplate("2017-01-01.13:00:00, 34", new Rule() {{
            add("startDate", LocalDateTime.of(2017, 1, 1, 13, 0, 0));
            add("limit", 34);
        }});

	}

}
