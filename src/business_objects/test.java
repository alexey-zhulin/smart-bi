package business_objects;

import java.sql.*;

public class test {

	public static void main(String[] args) {
		try {
			ConnectionHandler connection = new ConnectionHandler("10.0.3.50",
					"5432", "testdb", "postgres", "postgres");
			ResultSet resultSet = connection.CreateResultSet("select * from company");
			while (resultSet.next()) {
				System.out.println(resultSet.getString("name"));
			}
			connection.CloseConnection();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Operation completed...");
	}

}
