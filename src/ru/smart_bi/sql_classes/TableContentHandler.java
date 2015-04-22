package ru.smart_bi.sql_classes;

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

	// Изменение имени таблицы для переключения на другую таблицу
	public void SetTableName(String tableName) {
		this.tableName = tableName;
	}

	// Процедура добавления записи
	public void AddRecord(ArrayList<FieldContentHandler> fieldContentArr)
			throws SQLException {
		String queryText = "insert into " + tableName + "\n" + "(";
		for (int i = 0; i < fieldContentArr.size(); i++) {
			queryText = queryText + fieldContentArr.get(i).fieldName
					+ (((fieldContentArr.size() - 1) > i) ? "," : "");
		}
		queryText = queryText + ")\n";
		queryText = queryText + "values\n";
		queryText = queryText + "(";
		for (int i = 0; i < fieldContentArr.size(); i++) {
			queryText = queryText + "? "
					+ (((fieldContentArr.size() - 1) > i) ? "," : "");
		}
		queryText = queryText + ");\n";
		PreparedStatement statement = null;
		statement = connectionHandler.connection.prepareStatement(queryText);
		// Подготовим параметры
		for (int i = 0; i < fieldContentArr.size(); i++) {
			Object curObject = fieldContentArr.get(i).fieldValue;
			if (curObject == null) {
				statement.setNull(i + 1, Types.NULL);
			}
			else {
				if (curObject.getClass().equals(Integer.class)) {
					statement.setInt(i + 1, (int)fieldContentArr.get(i).fieldValue);
				}
				if (curObject.getClass().equals(String.class)) {
					statement.setString(i + 1, (String)fieldContentArr.get(i).fieldValue);
				}
				if (curObject.getClass().equals(Boolean.class)) {
					statement.setBoolean(i + 1, (Boolean)fieldContentArr.get(i).fieldValue);
				}
				if (curObject.getClass().equals(Double.class)) {
					statement.setDouble(i + 1, (Double)fieldContentArr.get(i).fieldValue);
				}
				if (curObject.getClass().equals(Date.class)) {
					statement.setDate(i + 1, (Date)fieldContentArr.get(i).fieldValue);
				}
			}
		}
		// Выполним скрипт
		statement.executeUpdate();
		statement.close();
	}
	
	// Процедура изменения записи
	public void UpdateRecord(ArrayList<FieldContentHandler> fieldContentArr, ArrayList<FieldContentHandler> primaryKeyArr)
			throws SQLException {
		
		String queryText = "update " + tableName + "\n";
		queryText = queryText + "set\n";
		for (int i = 0; i < fieldContentArr.size(); i++) {
			queryText = queryText + fieldContentArr.get(i).fieldName + " = ?"
					+ (((fieldContentArr.size() - 1) > i) ? ",\n" : "\n");
		}
		queryText = queryText + "\n";
		queryText = queryText + "where\n";
		for (int i = 0; i < primaryKeyArr.size(); i++) {
			queryText = queryText + primaryKeyArr.get(i).fieldName + " = ?"
					+ (((primaryKeyArr.size() - 1) > i) ? "and \n" : "\n");
			
		}
		PreparedStatement statement = null;
		statement = connectionHandler.connection.prepareStatement(queryText);
		// Подготовим параметры (изменяемые значения)
		for (int i = 0; i < fieldContentArr.size(); i++) {
			Object curObject = fieldContentArr.get(i).fieldValue;
			if (curObject == null) {
				statement.setNull(i + 1, Types.NULL);
			}
			else {
				if (curObject.getClass().equals(Integer.class)) {
					statement.setInt(i + 1, (int)fieldContentArr.get(i).fieldValue);
				}
				if (curObject.getClass().equals(String.class)) {
					statement.setString(i + 1, (String)fieldContentArr.get(i).fieldValue);
				}
				if (curObject.getClass().equals(Boolean.class)) {
					statement.setBoolean(i + 1, (Boolean)fieldContentArr.get(i).fieldValue);
				}
				if (curObject.getClass().equals(Double.class)) {
					statement.setDouble(i + 1, (Double)fieldContentArr.get(i).fieldValue);
				}
				if (curObject.getClass().equals(Date.class)) {
					statement.setDate(i + 1, (Date)fieldContentArr.get(i).fieldValue);
				}
			}
		}
		// Подготовим параметры (первичный ключ)
		for (int i = 0; i < primaryKeyArr.size(); i++) {
			Object curObject = primaryKeyArr.get(i).fieldValue;
			if (curObject == null) {
				statement.setNull(i + fieldContentArr.size() + 1, Types.NULL);
			}
			else {
				if (curObject.getClass().equals(Integer.class)) {
					statement.setInt(i + fieldContentArr.size() + 1, (int)primaryKeyArr.get(i).fieldValue);
				}
				if (curObject.getClass().equals(String.class)) {
					statement.setString(i + fieldContentArr.size() + 1, (String)primaryKeyArr.get(i).fieldValue);
				}
				if (curObject.getClass().equals(Boolean.class)) {
					statement.setBoolean(i + fieldContentArr.size() + 1, (Boolean)primaryKeyArr.get(i).fieldValue);
				}
				if (curObject.getClass().equals(Double.class)) {
					statement.setDouble(i + fieldContentArr.size() + 1, (Double)primaryKeyArr.get(i).fieldValue);
				}
				if (curObject.getClass().equals(Date.class)) {
					statement.setDate(i + fieldContentArr.size() + 1, (Date)primaryKeyArr.get(i).fieldValue);
				}
			}
		}
		// Выполним скрипт
		statement.executeUpdate();
		statement.close();
	}
	
	// Функция возвращает, существует ли запись в таблице с указанным значением ключевого поля
	public boolean ValueExists(ArrayList<FieldContentHandler> primaryKeyArr) throws SQLException {
		if (primaryKeyArr.size() == 0) return false;
		String queryText = "select * from " + tableName + "\n";
		queryText = queryText + "where\n";
		for (int i = 0; i < primaryKeyArr.size(); i++) {
			queryText = queryText + primaryKeyArr.get(i).fieldName + " = ?"
					+ (((primaryKeyArr.size() - 1) > i) ? "and \n" : "\n");
			
		}
		ResultSet resultSet = connectionHandler.CreateResultSet(queryText, primaryKeyArr);
		while (resultSet.next()) {
			return true;
		}
		// Если мы здесь, то значение не найдено
		return false;
	}
}
