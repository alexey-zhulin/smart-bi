package business_objects;

import java.sql.Connection;
import java.sql.DriverManager;

public class MetabaseHandler {
	public Connection OpenConnection(String host, String port, String database, String username, String password) {
		Connection connection = null;
	      try {
		         Class.forName("org.postgresql.Driver");
		         connection = DriverManager
		            .getConnection("jdbc:postgresql://" + host + ":" + port + "/" + database,
		            		username, password);
		      } catch (Exception e) {
		         e.printStackTrace();
		         System.err.println(e.getClass().getName()+": "+e.getMessage());
		         System.exit(0);
		      }
		return connection;
	}
	public void CloseConnection(Connection connection) {
	      try {
	    	  	connection.close();
		      } catch (Exception e) {
		         e.printStackTrace();
		         System.err.println(e.getClass().getName()+": "+e.getMessage());
		         System.exit(0);
		      }
	}
}
