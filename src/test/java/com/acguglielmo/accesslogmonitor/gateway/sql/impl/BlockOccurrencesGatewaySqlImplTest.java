package com.acguglielmo.accesslogmonitor.gateway.sql.impl;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.acguglielmo.accesslogmonitor.AbstractComponentTestExtension;
import com.acguglielmo.accesslogmonitor.dto.BlockOccurrencesDto;
import com.acguglielmo.accesslogmonitor.gateway.sql.ConnectionFactory;
import com.acguglielmo.accesslogmonitor.threshold.HourlyThreshold;
import com.acguglielmo.accesslogmonitor.threshold.Threshold;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class BlockOccurrencesGatewaySqlImplTest {

	@Inject
	ConnectionFactory connectionFactory;

	@RegisterExtension
	AbstractComponentTestExtension extension = new AbstractComponentTestExtension(connectionFactory);
	
	@Inject
	BlockOccurrencesGatewaySqlImpl instance;
	
	@BeforeEach
	public void beforeEach() throws Exception {

		FixtureFactoryLoader.loadTemplates("com.acguglielmo.accesslogmonitor.template");

	}
	
    @Test
    public void insertTest() throws Exception {
    	
    	final Threshold threshold = Fixture.from(HourlyThreshold.class).gimme("2017-01-01.13:00:00, 34");
    	
        final List<BlockOccurrencesDto> list = new ArrayList<>();
        final BlockOccurrencesDto dto1 = BlockOccurrencesDto.builder()
        	.ip("192.168.90.4")
        	.threshold(100)
        	.count(34)
        	.startDate(threshold.getStartDate())
        	.endDate(threshold.getEndDate())
        	.build();
        list.add(dto1);

        for (int i = 1; i < 1200; i++) {
            final BlockOccurrencesDto dto = BlockOccurrencesDto.builder()
            	.ip("192.168.90.8")
            	.threshold(100)
            	.count(34)
            	.startDate(threshold.getStartDate())
            	.endDate(threshold.getEndDate())
            	.build();
            list.add(dto);
        }

		final Connection connection = connectionFactory.getConnection();
		final Statement statement = connection.createStatement();
		statement.execute("SET DATABASE SQL SYNTAX MYS TRUE");
		statement.execute("CREATE TABLE block_occurrences (\n" +
				"  ip varchar(15) NOT NULL,\n" +
				"  start_date datetime(3) NOT NULL,\n" +
				"  end_date datetime(3) NOT NULL,\n" +
				"  threshold int(11) NOT NULL,\n" +
				"  comment varchar(200) NOT NULL,\n" +
				"  PRIMARY KEY (ip)\n" +
				");");
		connection.commit();
		
        instance.insert(list);
    }

}