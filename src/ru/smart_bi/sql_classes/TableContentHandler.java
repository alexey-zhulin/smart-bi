package ru.smart_bi.sql_classes;

import java.sql.*;
import java.util.*;

import org.springframework.jdbc.core.JdbcTemplate;

public class TableContentHandler {
	String tableName;
	JdbcTemplate jdbcTemplate;

	// Инициализация объекта
	public TableContentHandler(String tableName, JdbcTemplate jdbcTemplate) {
		this.tableName = tableName;
		this.jdbcTemplate = jdbcTemplate;
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
		ArrayList<Object> params = new ArrayList<Object>();
		for (int i = 0; i < fieldContentArr.size(); i++) {
			queryText = queryText + "? "
					+ (((fieldContentArr.size() - 1) > i) ? "," : "");
			params.add(fieldContentArr.get(i).fieldValue);
		}
		queryText = queryText + ");\n";
		jdbcTemplate.update(queryText, params.toArray());
	}
	
	// Процедура изменения записи
	public void UpdateRecord(ArrayList<FieldContentHandler> fieldContentArr, ArrayList<FieldContentHandler> primaryKeyArr)
			throws SQLException {
		
		String queryText = "update " + tableName + "\n";
		queryText = queryText + "set\n";
		ArrayList<Object> params = new ArrayList<Object>();
		for (int i = 0; i < fieldContentArr.size(); i++) {
			queryText = queryText + fieldContentArr.get(i).fieldName + " = ?"
					+ (((fieldContentArr.size() - 1) > i) ? ",\n" : "\n");
			params.add(fieldContentArr.get(i).fieldValue);
		}
		queryText = queryText + "\n";
		queryText = queryText + "where\n";
		for (int i = 0; i < primaryKeyArr.size(); i++) {
			queryText = queryText + primaryKeyArr.get(i).fieldName + " = ?"
					+ (((primaryKeyArr.size() - 1) > i) ? "and \n" : "\n");
			params.add(primaryKeyArr.get(i).fieldValue);
		}
		jdbcTemplate.update(queryText, params.toArray());
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
		ArrayList<Object> params = new ArrayList<Object>();
		for (int i = 0; i < primaryKeyArr.size(); i++) {
			params.add(primaryKeyArr.get(i).fieldValue);
		}
		int rowCount = jdbcTemplate.queryForObject(queryText, params.toArray(),
				Integer.class);
		return (rowCount > 0);
	}
}
