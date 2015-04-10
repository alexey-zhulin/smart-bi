package sql_classes;

//http://www.tutorialspoint.com/postgresql/postgresql_java.htm

import java.sql.*;

public class ConnectionHandler {
	Connection connection;

	// ��� �������� ������� �������������� �����������
	public ConnectionHandler(String host, String port, String database,
			String username, String password) throws SQLException,
			ClassNotFoundException {
		connection = Connect(host, port, database, username, password);
	}

	// ������� �������� �����������
	public Connection Connect(String host, String port, String database,
			String username, String password) throws SQLException,
			ClassNotFoundException {
		Class.forName("org.postgresql.Driver");
		connection = DriverManager.getConnection("jdbc:postgresql://" + host
				+ ":" + port + "/" + database, username, password);
		return connection;
	}
	
	// ��������� �������� �����������
	public void CloseConnection() throws SQLException {
		connection.close();
	}
	
	// ��������� ���������� SQL �������
	public void ExecuteCommand(String queryText)
			throws SQLException {
		Statement statement = connection.createStatement();
		statement.executeUpdate(queryText);
		statement.close();
	}
	
	// ��������� ��������� �������
	public ResultSet CreateResultSet(String queryText) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(queryText);
		return resultSet;
	}
}
