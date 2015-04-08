package business_objects;

//http://www.tutorialspoint.com/postgresql/postgresql_java.htm

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ConnectionHandler {

	// Открываем подключение к БД
	public Connection OpenConnection(String host, String port, String database,
			String username, String password) {
		Connection connection = null;
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection("jdbc:postgresql://"
					+ host + ":" + port + "/" + database, username, password);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		return connection;
	}

	// Закрываем подключение
	public void CloseConnection(Connection connection) {
		try {
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	// Выполняем SQL команду
	public void ExecuteCommand(Connection connection, String queryText) {
		Statement statement = null;
		try {
			statement = connection.createStatement();
			statement.executeUpdate(queryText);
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}
}
