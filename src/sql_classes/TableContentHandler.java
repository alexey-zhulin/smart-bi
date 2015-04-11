package sql_classes;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class TableContentHandler {
	String tableName;
	ConnectionHandler connectionHandler;

	// Инициализация объекта
	public TableContentHandler(String tableName,
			ConnectionHandler connectionHandler) {
		this.tableName = tableName;
		this.connectionHandler = connectionHandler;
	}

	// Процедура добавления записи
	public void AddRecord(ArrayList<FieldContentHandler> feildContentArr)
			throws SQLException {
		String queryText = "insert into " + tableName + "\n" + "(";
		int i;
		for (i = 0; i < feildContentArr.size(); i++) {
			queryText = queryText + feildContentArr.get(i).fieldName
					+ (((feildContentArr.size() - 1) > i) ? "," : "");
		}
		queryText = queryText + ")\n";
		queryText = queryText + "values\n";
		queryText = queryText + "(";
		for (i = 0; i < feildContentArr.size(); i++) {
			queryText = queryText + "? "
					+ (((feildContentArr.size() - 1) > i) ? "," : "");
		}
		queryText = queryText + ");\n";
		PreparedStatement statement = null;
		statement = connectionHandler.connection.prepareStatement(queryText);
		// Подготовим параметры
		for (i = 0; i < feildContentArr.size(); i++) {
			Object curObject = feildContentArr.get(i).fieldValue;
			if (curObject.getClass().equals(Integer.class)) {
				statement.setInt(i + 1, (int)feildContentArr.get(i).fieldValue);
			}
			if (curObject.getClass().equals(String.class)) {
				statement.setString(i + 1, (String)feildContentArr.get(i).fieldValue);
			}
			if (curObject.getClass().equals(Boolean.class)) {
				statement.setBoolean(i + 1, (Boolean)feildContentArr.get(i).fieldValue);
			}
			if (curObject.getClass().equals(Double.class)) {
				statement.setDouble(i + 1, (Double)feildContentArr.get(i).fieldValue);
			}
			if (curObject.getClass().equals(Date.class)) {
				statement.setDate(i + 1, (Date)feildContentArr.get(i).fieldValue);
			}
		}
		// Выполним скрипт
		statement.executeUpdate();
		statement.close();
	}
}
