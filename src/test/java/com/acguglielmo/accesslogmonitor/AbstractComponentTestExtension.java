package com.acguglielmo.accesslogmonitor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.acguglielmo.accesslogmonitor.gateway.sql.ConnectionFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AbstractComponentTestExtension implements BeforeAllCallback {

    private boolean accessLogTableCreated = false;
    
    private boolean blockOccurencesTableCreated = false;

    final ConnectionFactory connectionFactory;
    
	@Override
	public void beforeAll(ExtensionContext context) throws Exception {

    	createAccessLogTable();

        createBlockOcurrencesTable();

    }

	private void createAccessLogTable() throws SQLException, ClassNotFoundException {
		
		if ( !accessLogTableCreated  ) {
			
			final Connection connection = connectionFactory.getConnection();
			final Statement statement = connection.createStatement();
			statement.execute("SET DATABASE SQL SYNTAX MYS TRUE");
			statement.execute("CREATE TABLE access_log (\n" +
					"  date datetime(3) NOT NULL,\n" +
					"  ip varchar(15) NOT NULL,\n" +
					"  request varchar(45) NOT NULL,\n" +
					"  status int(11) NOT NULL,\n" +
					"  user_agent varchar(200) NOT NULL,\n" +
					"  PRIMARY KEY (date, ip),\n" +
					");\n");
			connection.commit();
			
			accessLogTableCreated = true;
		}
		
	}

	private void createBlockOcurrencesTable() throws SQLException, ClassNotFoundException {
		
		if ( !blockOccurencesTableCreated ) {
			
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
			
			blockOccurencesTableCreated = true;
			
		}
		
	}

}
