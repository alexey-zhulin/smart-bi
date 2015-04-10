package sql_classes;

//http://www.tutorialspoint.com/postgresql/postgresql_java.htm

import java.sql.*;

public class ConnectionHandler {
	Connection connection;

	// При создании объекта инициализируем подключение
	public ConnectionHandler(String host, String port, String database,
			String username, String password) throws SQLException,
			ClassNotFoundException {
		connection = Connect(host, port, database, username, password);
	}

	// Функция создания подключения
	public Connection Connect(String host, String port, String database,
			String username, String password) throws SQLException,
			ClassNotFoundException {
		Class.forName("org.postgresql.Driver");
		connection = DriverManager.getConnection("jdbc:postgresql://" + host
				+ ":" + port + "/" + database, username, password);
		return connection;
	}
	
	// Процедура закрытия подключения
	public void CloseConnection() throws SQLException {
		connection.close();
	}
	
	// Процедура выполнения SQL команды
	public void ExecuteCommand(String queryText)
			throws SQLException {
		Statement statement = connection.createStatement();
		statement.executeUpdate(queryText);
		statement.close();
	}
	
	// Процедура получения курсора
	public ResultSet CreateResultSet(String queryText) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(queryText);
		return resultSet;
	}
}
