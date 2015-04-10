package business_objects;

import java.sql.SQLException;

import sql_classes.*;

public class MetabaseHandler {

	// ��������� �������� ��������� ��������
	void DeleteMetabase(ConnectionHandler connection) throws SQLException {
		String tableName = "MetabaseObjects";
		TableHandler tableHandler = new TableHandler(tableName, connection);
		tableHandler.DropTable();
		//
		tableName = "ObjectClasses";
		tableHandler.SetTableName(tableName);
	}

	// ��������� �������� ������� ObjectClasses (������� �������� ��������)
	void CreateObjectClasses(ConnectionHandler connection) throws SQLException {
		// �������� ���� �������
		String tableName = "ObjectClasses";
		TableHandler tableHandler = new TableHandler(tableName, connection);
		FieldHandler[] fieldsArr = new FieldHandler[2];
		fieldsArr[0] = FieldHandler.createField("id", "int", false, 1);
		fieldsArr[1] = FieldHandler.createField("class_name", "text", false, 0);
		tableHandler.CreateTable(fieldsArr);
		// �������� ���������� ������ �� ������������
		IndexHandler[] indexfieldsArr = new IndexHandler[1];
		indexfieldsArr[0] = IndexHandler.createIndexField("class_name", 0);
		tableHandler.CreateIndex(indexfieldsArr, "idx_class_name", true);
		// �������������� ��������
	}

	// ��������� �������� ������� MetabaseObjects (�������� ��������)
	void CreateMetabaseObjects(ConnectionHandler connection) throws SQLException {
		// �������� ���� �������
		String tableName = "MetabaseObjects";
		TableHandler tableHandler = new TableHandler(tableName, connection);
		FieldHandler[] fieldsArr = new FieldHandler[2];
		fieldsArr[0] = FieldHandler.createField("id", "int", false, 1);
		fieldsArr[1] = FieldHandler.createField("object_name", "text", false, 0);
		tableHandler.CreateTable(fieldsArr);
	}

	// ��������� ������������� ��������
	public void CreateMetabase(boolean recreate, String server, String port,
			String datebase, String user, String password)
			throws ClassNotFoundException, SQLException {
		// �������� �����������
		ConnectionHandler connection = new ConnectionHandler(server, port,
				datebase, user, password);
		if (recreate) {
			// ��� ������� ����� "������������" ������ ������ ������� ��������
		}
		// �������� ������� ObjectClasses
		CreateObjectClasses(connection);
		// �������� ������� MetabaseObjects
		CreateMetabaseObjects(connection);
		// ������� �����������
		connection.CloseConnection();
	}
}
