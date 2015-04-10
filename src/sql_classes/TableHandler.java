package sql_classes;

import java.sql.*;
import java.util.*;

public class TableHandler {
	String tableName;
	ConnectionHandler connectionHandler;

	// ������������� �������
	public TableHandler(String tableName, ConnectionHandler connectionHandler) {
		this.tableName = tableName;
		this.connectionHandler = connectionHandler;
	}

	// ��������� ����� ������� ��� ������������ �� ������ �������
	public void SetTableName(String tableName) {
		this.tableName = tableName;
	}

	// ������� ���������� ���������� �� ������� � ������ tableName
	boolean TableExists() throws SQLException {

		ResultSet resultSet = connectionHandler
				.CreateResultSet("select 1 from information_schema.tables where  table_name = lower('"
						+ tableName + "')");
		while (resultSet.next()) {
			return true;
		}
		return false;
	}

	// �������� �������
	public void CreateTable(FieldHandler[] fieldsArr) throws SQLException {
		if (TableExists()) {
			return;
		}
		// ������ �������� ����� �������
		String queryText = "create table " + tableName + "(\n";
		int i;
		for (i = 0; i < fieldsArr.length; i++) {
			queryText = queryText + fieldsArr[i].fieldName + " "
					+ fieldsArr[i].fieldType + " "
					+ ((fieldsArr[i].isNull) ? "NULL" : "NOT NULL")
					+ (((fieldsArr.length - 1) > i) ? "," : "") + "\n";
		}
		queryText = queryText + ");\n";

		// ������ ���������� ���������� �����
		// ���������� ���� ��� ��������� � PK, �������� �� ���� primaryKeyIndex,
		// ��� �������� ��������� � ������ � ������ ����������
		Map<Integer, FieldHandler> fieldsMap = new TreeMap<Integer, FieldHandler>();
		for (i = 0; i < fieldsArr.length; i++) {
			if (fieldsArr[i].primaryKeyIndex > 0) {
				fieldsMap.put(fieldsArr[i].primaryKeyIndex, fieldsArr[i]);
			}
		}
		// ���� � ������� ����� ���� ��������, �� ���������� �� ��� ������ ���
		// ���������� �����
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

		// �������� ������
		Statement statement = null;
		statement = connectionHandler.connection.createStatement();
		statement.executeUpdate(queryText);
		statement.close();
	}

	// ��������� ���������� �������
	public void CreateIndex(IndexHandler[] fieldsArr, String index_name,
			boolean isUnique) throws SQLException {
		if (!TableExists()) {
			return;
		}
		Statement statement = null;
		// ���������� ���� ��� ��������� � ������, �������� �� ����
		// indexPosition, ��� �������� ��������� � ������ � ������ ����������
		Map<Integer, IndexHandler> fieldsMap = new TreeMap<Integer, IndexHandler>();
		int i;
		for (i = 0; i < fieldsArr.length; i++) {
			fieldsMap.put(fieldsArr[i].indexPosition, fieldsArr[i]);
		}
		// ���� � ������� ����� ���� ��������, �� ���������� �� ��� ������ ���
		// ���������� �����
		i = 0;
		if (fieldsMap.size() > 0) {
			// ������ ������ � ����� �� �������������, ���� ���
			String queryText = "drop index if exists " + index_name;
			statement = connectionHandler.connection.createStatement();
			statement.executeUpdate(queryText);
			statement.close();
			// �������� �����
			queryText = "create " + ((isUnique) ? "unique" : "") + " index "
					+ index_name + " on " + tableName + "(\n";
			for (Map.Entry<Integer, IndexHandler> entry : fieldsMap.entrySet()) {
				i++;
				IndexHandler fieldHanler = (IndexHandler) entry.getValue();
				queryText = queryText + fieldHanler.fieldName
						+ ((fieldsMap.size() > i) ? "," : "");
			}
			queryText = queryText + ");\n";
			// �������� ������
			statement = connectionHandler.connection.createStatement();
			statement.executeUpdate(queryText);
			statement.close();
		}
	}

	// ��������� ���������� constraint
	public void CreateConstraint(String tableTo) throws SQLException {
		String queryText = null;
		String pk_list = null;
		String fk_list = null;
		String pk_queryText = "select ku.column_name"
				+ "from information_schema.table_constraints tc"
				+ "     , information_schema.key_column_usage ku"
				+ "where tc.table_name = ku.table_name"
				+ "      and tc.constraint_schema = ku.constraint_schema"
				+ "      and upper(tc.constraint_type) = 'PRIMARY KEY'"
				+ "      and lower(tc.table_name) = lower(" + tableTo + ")"
				+ "order by ku.ordinal_position";

		ResultSet resultSet = connectionHandler.CreateResultSet(pk_queryText);
		while (resultSet.next()) {
			pk_list = pk_list + resultSet.getString("column_name") + ", ";
			fk_list = fk_list + "f_" + resultSet.getString("column_name") + ", ";
		}

		Statement statement = null;
		statement = connectionHandler.connection.createStatement();
		statement.executeUpdate(queryText);
		statement.close();
	}

	// �������� �������
	public void DropTable() throws SQLException {
		Statement statement = null;
		statement = connectionHandler.connection.createStatement();
		String queryText = "drop table if exists " + tableName;
		statement.executeUpdate(queryText);
		statement.close();
	}
}
