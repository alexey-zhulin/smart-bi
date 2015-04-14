package business_objects;

import java.sql.*;
import java.util.ArrayList;

import business_objects.MetabaseHandler.ObjectClasses;
import sql_classes.*;

public class MetabaseObject {
	public int object_id;
	public String object_name;
	public String ext_id;
	ArrayList<ObjectField> fields;
	ConnectionHandler connection;
	int f_class_id;
	
	public MetabaseObject(ConnectionHandler connection) {
		this.connection = connection;
		f_class_id = ObjectClasses.Undefined.getValue();
		// Инициализируем массив полей
		fields = new ArrayList<ObjectField>();
	}
	
	// Процедура создания объекта (регистрация в таблице MetabaseObjects)
	void CreateObject() throws SQLException {
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
	
	// Энумератор для наименования префикса таблиц с данными
	public enum DataTablePrefixes {
		Dictionary("_d")
		, Revisions("_rr")
		, Facts("_rf")
		, Values("_rv")
		;
		
        private final String value;

        private DataTablePrefixes(final String newValue) {
            value = newValue;
        }

        public String getValue() { return value; }
	}
}
