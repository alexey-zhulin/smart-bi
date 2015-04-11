package sql_classes;

//http://www.tutorialspoint.com/postgresql/postgresql_java.htm

import java.sql.*;
import java.util.ArrayList;

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
	public ResultSet CreateResultSet(String queryText, ArrayList<FieldContentHandler> paramsArr) throws SQLException {
		ResultSet resultSet = null;
		if (paramsArr.size() == 0) { // ������ ��� ����������
			Statement statement = connection.createStatement();
			resultSet = statement.executeQuery(queryText);
		}
		else {
			PreparedStatement statement = connection.prepareStatement(queryText);
			// ���������� ���������
			int i;
			for (i = 0; i < paramsArr.size(); i++) {
				Object curObject = paramsArr.get(i).fieldValue;
				if (curObject.getClass().equals(Integer.class)) {
					statement.setInt(i + 1, (int)paramsArr.get(i).fieldValue);
				}
				if (curObject.getClass().equals(String.class)) {
					statement.setString(i + 1, (String)paramsArr.get(i).fieldValue);
				}
				if (curObject.getClass().equals(Boolean.class)) {
					statement.setBoolean(i + 1, (Boolean)paramsArr.get(i).fieldValue);
				}
				if (curObject.getClass().equals(Double.class)) {
					statement.setDouble(i + 1, (Double)paramsArr.get(i).fieldValue);
				}
				if (curObject.getClass().equals(Date.class)) {
					statement.setDate(i + 1, (Date)paramsArr.get(i).fieldValue);
				}
			}
			resultSet = statement.executeQuery();
		}
		return resultSet;
	}
}
