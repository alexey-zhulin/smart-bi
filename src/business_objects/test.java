package business_objects;

import java.sql.Connection;

public class test {

	public static void main(String[] args) {
		MetabaseHandler mbHandler = new MetabaseHandler();
		Connection connection = mbHandler.OpenConnection("10.0.3.50", "5432", "testdb", "postgres", "postgres");
		mbHandler.CloseConnection(connection);
		System.out.println("Opened database successfully");
	}

}
