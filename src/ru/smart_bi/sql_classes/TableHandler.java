package ru.smart_bi.sql_classes;

import java.sql.*;
import java.util.*;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

public class TableHandler {
	String tableName;
	JdbcTemplate jdbcTemplate;

	// Инициализация объекта
	public TableHandler(String tableName, JdbcTemplate jdbcTemplate) {
		this.tableName = tableName;
		this.jdbcTemplate = jdbcTemplate;
	}

	// Изменение имени таблицы для переключения на другую таблицу
	public void SetTableName(String tableName) {
		this.tableName = tableName;
	}

	// Функция возвращает существует ли таблица с именем tableName
	public boolean TableExists() throws SQLException {
		String queryText = "select count(*) from information_schema.tables where table_name = ?";
		int rowCount = jdbcTemplate.queryForObject(queryText,
				new Object[] { tableName.toLowerCase() }, Integer.class);
		return (rowCount > 0);
	}

	// Создание таблицы
	public void CreateTable(ArrayList<FieldHandler> fieldsArr,
			String tableComment) throws SQLException {
		if (TableExists()) {
			return;
		}
		// Скрипт создания самой таблицы
		String queryText = "create table " + tableName + "(\n";
		for (int i = 0; i < fieldsArr.size(); i++) {
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
		for (int i = 0; i < fieldsArr.size(); i++) {
			if (fieldsArr.get(i).primaryKeyIndex > 0) {
				fieldsMap.put(fieldsArr.get(i).primaryKeyIndex,
						fieldsArr.get(i));
			}
		}
		// Если в массиве полей есть элементы, то сформируем по ним скрипт для
		// первичного ключа
		int pos = 0;
		if (fieldsMap.size() > 0) {
			queryText = queryText + "alter table " + tableName
					+ " add constraint pk_" + tableName + " primary key (";
			for (Map.Entry<Integer, FieldHandler> entry : fieldsMap.entrySet()) {
				pos++;
				FieldHandler fieldHanler = (FieldHandler) entry.getValue();
				queryText = queryText + fieldHanler.fieldName
						+ ((fieldsMap.size() > pos) ? "," : "");
			}
			queryText = queryText + ");\n";
		}
		// Добавим комментарий к таблице
		if (tableComment != null) {
			queryText = queryText + "comment on table " + tableName + " is '"
					+ tableComment + "';\n";
		}

		// Выполним скрипт
		jdbcTemplate.execute(queryText);
	}

	// Процедура добавления индекса
	public void CreateIndex(ArrayList<IndexHandler> fieldsArr,
			String index_name, boolean isUnique) throws SQLException {
		if (!TableExists()) {
			return;
		}
		// Подготовим поля для включения в индекс, опираясь на поле
		// indexPosition, как параметр включения в массив и индекс сортировки
		Map<Integer, IndexHandler> fieldsMap = new TreeMap<Integer, IndexHandler>();
		for (int i = 0; i < fieldsArr.size(); i++) {
			fieldsMap.put(fieldsArr.get(i).indexPosition, fieldsArr.get(i));
		}
		// Если в массиве полей есть элементы, то сформируем по ним скрипт для
		// первичного ключа
		int pos = 0;
		if (fieldsMap.size() > 0) {
			// Удалим индекс с таким же наименованием, если был
			String queryText = "drop index if exists " + index_name;
			jdbcTemplate.execute(queryText);
			// Создадим новый
			queryText = "create " + ((isUnique) ? "unique" : "") + " index "
					+ index_name + " on " + tableName + "(\n";
			for (Map.Entry<Integer, IndexHandler> entry : fieldsMap.entrySet()) {
				pos++;
				IndexHandler fieldHanler = (IndexHandler) entry.getValue();
				queryText = queryText + fieldHanler.fieldName
						+ ((fieldsMap.size() > pos) ? "," : "");
			}
			queryText = queryText + ");\n";
			// Выполним скрипт
			jdbcTemplate.execute(queryText);
		}
	}

	// Процедура добавления constraint
	public void CreateConstraint(String tableTo, boolean onDeleteCascade,
			String in_fk_list) throws SQLException {
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

		List<String> column_list = jdbcTemplate.query(pk_queryText,
				new ResultSetExtractor<List<String>>() {
					@Override
					public List<String> extractData(ResultSet rs)
							throws SQLException, DataAccessException {

						List<String> list = new ArrayList<String>();
						while (rs.next()) {
							list.add(rs.getString("column_name"));
						}
						return list;
					}
				});
		for (String column_name: column_list) {
			pk_list = pk_list + column_name + ", ";
			fk_list = fk_list + "f_" + column_name
					+ ", ";
			if (in_fk_list == "") {
				queryText = "alter table " + tableName + " add " + "f_"
						+ column_name + " int NOT NULL";
			}
			jdbcTemplate.execute(queryText);
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
		jdbcTemplate.execute(queryText);
	}

	public void CreateConstraint(String tableTo, boolean onDeleteCascade)
			throws SQLException {
		CreateConstraint(tableTo, onDeleteCascade, "");
	}

	// Удаление таблицы
	public void DropTable() throws SQLException {
		String queryText = "drop table if exists " + tableName;
		jdbcTemplate.execute(queryText);
	}

	// Функция проверяет, существует ли указанное поле в таблице
	public boolean FieldExists(String fieldName) throws SQLException {
		String queryText = "select count(*) from information_schema.columns where table_name = lower(?) and column_name = lower(?)";
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(tableName);
		params.add(fieldName);
		int rowCount = jdbcTemplate.queryForObject(queryText, params.toArray(),
				Integer.class);
		return (rowCount > 0);
	}
}
