package com.acguglielmo.accesslogmonitor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.BeforeClass;

import com.acguglielmo.accesslogmonitor.util.PropertiesHolder;

public class AbstractComponentTest {

    private static boolean accessLogTableCreated = false;
    
    private static boolean blockOccurencesTableCreated = false;

	@BeforeClass
    public static void beforeClass() throws IOException, ClassNotFoundException, SQLException {
    	
    	PropertiesHolder.createInstance("src/test/resources/application.properties");
    	
    	createAccessLogTable();
        
        createBlockOcurrencesTable();
    
    }

	private static void createAccessLogTable() throws SQLException, ClassNotFoundException {
		
		if ( !accessLogTableCreated  ) {
			
			final Connection connection = getConnection();
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

	private static void createBlockOcurrencesTable() throws SQLException, ClassNotFoundException {
		
		if ( !blockOccurencesTableCreated ) {
			
			final Connection connection = getConnection();
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
    
    protected static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.hsqldb.jdbcDriver");
        return DriverManager.getConnection("jdbc:hsqldb:mem:test", "sa", "");
    }

}
