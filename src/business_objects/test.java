package business_objects;

import java.sql.Connection;

public class test {

	public static void main(String[] args) {
		ConnectionHandler cnHandler = new ConnectionHandler();
		Connection connection = cnHandler.OpenConnection("10.0.3.50", "5432", "testdb", "postgres", "postgres");
		cnHandler.CloseConnection(connection);
		System.out.println("Opened database successfully");
	}

}
