package business_objects;

import java.sql.*;
import java.util.ArrayList;

import sql_classes.*;

public class MetabaseObject {
	public int object_id;
	public String object_name;
	public String ext_id;
	public int f_class_id;
	ConnectionHandler connection;
	
	public MetabaseObject(ConnectionHandler connection) {
		this.connection = connection;
	}
	
	// Процедура создания объекта
	public void CreateObject() throws SQLException {
		// Добавим запись в таблицу MetabaseObjects
		String tableName = "MetabaseObjects";
		TableContentHandler tableContent = new TableContentHandler(tableName, connection);
		ArrayList<FieldContentHandler> fieldsArr = new ArrayList<FieldContentHandler>();
		fieldsArr.add(FieldContentHandler.createFieldContent("object_name", object_name));
		fieldsArr.add(FieldContentHandler.createFieldContent("ext_id", ext_id));
		fieldsArr.add(FieldContentHandler.createFieldContent("f_class_id", f_class_id));
		tableContent.AddRecord(fieldsArr);
	}
	
	// Процедура получения идентификатора объекта по внешнему идентификатору
	public int GetObjectId(String ext_id) throws SQLException {
		int id = -1;
		String queryText = "select object_id from MetabaseObjects where ext_id = ?";
		ArrayList<FieldContentHandler> paramsArr = new ArrayList<FieldContentHandler>();
		paramsArr.add(FieldContentHandler.createFieldContent("ext_id", ext_id));
		ResultSet resultSet = connection.CreateResultSet(queryText, paramsArr);
		while (resultSet.next()) {
			id = resultSet.getInt("object_id");
		}
		return id;
	}
}
