package ru.smart_bi.sql_classes;

import java.sql.*;
import java.util.*;

public class TableHandler {
	String tableName;
	ConnectionHandler connectionHandler;

	// Инициализация объекта
	public TableHandler(String tableName, ConnectionHandler connectionHandler) {
		this.tableName = tableName;
		this.connectionHandler = connectionHandler;
	}

	// Изменение имени таблицы для переключения на другую таблицу
	public void SetTableName(String tableName) {
		this.tableName = tableName;
	}

	// Функция возвращает существует ли таблица с именем tableName
	public boolean TableExists() throws SQLException {

		ArrayList<FieldContentHandler> paramsArr = new ArrayList<FieldContentHandler>();
		paramsArr.add(FieldContentHandler.createFieldContent("object_name", tableName.toLowerCase()));
		ResultSet resultSet = connectionHandler
				.CreateResultSet("select 1 from information_schema.tables where table_name = ?", paramsArr);
		while (resultSet.next()) {
			return true;
		}
		return false;
	}

	// Создание таблицы
	public void CreateTable(ArrayList<FieldHandler> fieldsArr, String tableComment) throws SQLException {
		if (TableExists()) {
			return;
		}
		// Скрипт создания самой таблицы
		String queryText = "create table " + tableName + "(\n";
		int i;
		for (i = 0; i < fieldsArr.size(); i++) {
			queryText = queryText + fieldsArr.get(i).fieldName + " "
					+ fieldsArr.get(i).fieldType + " "
					+ ((fieldsArr.get(i).isNull) ? "NULL" : "NOT NULL")
					+ (((fieldsArr.size() - 1) > i) ? "," : "") + "\n";
		}
		queryText = queryText + ");\n";

		// Скрипт добавления первичного ключа
		// Подготовим поля для включения в PK, опираясь на поле primaryKeyIndex,
		// как параметр включения в массив и индекс сортировки
		Map<Integer, FieldHandler> fieldsMap = new TreeMap<Integer, FieldHandler>();
		for (i = 0; i < fieldsArr.size(); i++) {
			if (fieldsArr.get(i).primaryKeyIndex > 0) {
				fieldsMap.put(fieldsArr.get(i).primaryKeyIndex, fieldsArr.get(i));
			}
		}
		// Если в массиве полей есть элементы, то сформируем по ним скрипт для
		// первичного ключа
		i = 0;
		if (fieldsMap.size() > 0) {
			queryText = queryText + "alter table " + tableName
					+ " add constraint pk_" + tableName + " primary key (";
			for (Map.Entry<Integer, FieldHandler> entry : fieldsMap.entrySet()) {
				i++;
				FieldHandler fieldHanler = (FieldHandler) entry.getValue();
				queryText = queryText + fieldHanler.fieldName
						+ ((fieldsMap.size() > i) ? "," : "");
			}
			queryText = queryText + ");\n";
		}
		// Добавим комментарий к таблице
		if (tableComment != null) {
			queryText = queryText + "comment on table " + tableName + " is '" + tableComment + "';\n";
		}

		// Выполним скрипт
		Statement statement = null;
		statement = connectionHandler.connection.createStatement();
		statement.executeUpdate(queryText);
		statement.close();
	}

	// Процедура добавления индекса
	public void CreateIndex(ArrayList<IndexHandler> fieldsArr, String index_name,
			boolean isUnique) throws SQLException {
		if (!TableExists()) {
			return;
		}
		Statement statement = null;
		// Подготовим поля для включения в индекс, опираясь на поле
		// indexPosition, как параметр включения в массив и индекс сортировки
		Map<Integer, IndexHandler> fieldsMap = new TreeMap<Integer, IndexHandler>();
		int i;
		for (i = 0; i < fieldsArr.size(); i++) {
			fieldsMap.put(fieldsArr.get(i).indexPosition, fieldsArr.get(i));
		}
		// Если в массиве полей есть элементы, то сформируем по ним скрипт для
		// первичного ключа
		i = 0;
		if (fieldsMap.size() > 0) {
			// Удалим индекс с таким же наименованием, если был
			String queryText = "drop index if exists " + index_name;
			statement = connectionHandler.connection.createStatement();
			statement.executeUpdate(queryText);
			statement.close();
			// Создадим новый
			queryText = "create " + ((isUnique) ? "unique" : "") + " index "
					+ index_name + " on " + tableName + "(\n";
			for (Map.Entry<Integer, IndexHandler> entry : fieldsMap.entrySet()) {
				i++;
				IndexHandler fieldHanler = (IndexHandler) entry.getValue();
				queryText = queryText + fieldHanler.fieldName
						+ ((fieldsMap.size() > i) ? "," : "");
			}
			queryText = queryText + ");\n";
			// Выполним скрипт
			statement = connectionHandler.connection.createStatement();
			statement.executeUpdate(queryText);
			statement.close();
		}
	}

	// Процедура добавления constraint
	public void CreateConstraint(String tableTo, boolean onDeleteCascade, String in_fk_list)
			throws SQLException {
		Statement statement = null;
		String queryText = "";
		String pk_list = "";
		String fk_list = "";
		String pk_queryText = "select ku.column_name\n"
				+ "from information_schema.table_constraints tc\n"
				+ "     , information_schema.key_column_usage ku\n"
				+ "where tc.table_name = ku.table_name\n"
				+ "      and tc.constraint_schema = ku.constraint_schema\n"
				+ "      and upper(tc.constraint_type) = 'PRIMARY KEY'\n"
				+ "      and lower(tc.table_name) = lower('" + tableTo + "')\n"
				+ "      and ku.position_in_unique_constraint is null\n"
				+ "order by ku.ordinal_position";

		ArrayList<FieldContentHandler> paramsArr = new ArrayList<FieldContentHandler>();
		ResultSet resultSet = connectionHandler.CreateResultSet(pk_queryText, paramsArr);
		while (resultSet.next()) {
			pk_list = pk_list + resultSet.getString("column_name") + ", ";
			fk_list = fk_list + "f_" + resultSet.getString("column_name")
					+ ", ";
			if (in_fk_list == "") {
				queryText = "alter table " + tableName + " add " + "f_" + resultSet.getString("column_name") + " int NOT NULL";
			}
			statement = connectionHandler.connection.createStatement();
			statement.executeUpdate(queryText);
			statement.close();
		}
		// Уберем последнюю запятую
		pk_list = pk_list.substring(0, pk_list.length() - 2);
		fk_list = fk_list.substring(0, fk_list.length() - 2);
		if (in_fk_list != "") {
			fk_list = in_fk_list;
		}

		queryText = "alter table " + tableName + " add constraint fk_"
				+ tableName + "_ref_" + tableTo + " foreign key (" + fk_list
				+ ") references " + tableTo + " (" + pk_list + ") on delete "
				+ ((onDeleteCascade) ? "cascade " : "restrict ")
				+ " on update restrict";
		statement = connectionHandler.connection.createStatement();
		statement.executeUpdate(queryText);
		statement.close();
	}
	
	public void CreateConstraint(String tableTo, boolean onDeleteCascade) throws SQLException {
		CreateConstraint(tableTo, onDeleteCascade, "");
	}

	// Удаление таблицы
	public void DropTable() throws SQLException {
		Statement statement = null;
		statement = connectionHandler.connection.createStatement();
		String queryText = "drop table if exists " + tableName;
		statement.executeUpdate(queryText);
		statement.close();
	}
	
	// Функция проверяет, существует ли указанное поле в таблице
	public boolean FieldExists(String fieldName) throws SQLException {
		String queryText = "select * from information_schema.columns where table_name = lower(?) and column_name = lower(?)";
		ArrayList<FieldContentHandler> paramsArr = new ArrayList<FieldContentHandler>();
		//paramsArr.add(arg0)
		ResultSet resultSet = connectionHandler.CreateResultSet(queryText, paramsArr);
		while (resultSet.next()) {
			return true;
		}
		// Если мы здесь - значит поле в таблице не найдено
		return false;
	}
}
