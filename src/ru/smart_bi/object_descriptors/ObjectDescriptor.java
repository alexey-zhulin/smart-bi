package ru.smart_bi.object_descriptors;

import java.sql.*;
import java.util.ArrayList;

import ru.smart_bi.object_descriptors.MetabaseDescriptor.ObjectClasses;
import ru.smart_bi.sql_classes.*;

public class ObjectDescriptor {
	public int object_id;
	public String object_name;
	public String ext_id;
	public ArrayList<ObjectFieldDescriptor> fields;
	ConnectionHandler connection;
	int f_class_id;
	
	public ObjectDescriptor(ConnectionHandler connection) {
		this.connection = connection;
		f_class_id = ObjectClasses.Undefined.getValue();
		// Инициализируем массив полей
		fields = new ArrayList<ObjectFieldDescriptor>();
	}
	
	// Процедура создания объекта (регистрация в таблице MetabaseObjects)
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
	
	// Функция возвращает поле по его идентификатору в базе (наименованию поля в таблице)
	public ObjectFieldDescriptor FieldById(String id) {
		int i;
		for (i = 0; i < fields.size(); i ++) {
			if (fields.get(i).fieldHandler.fieldName.toLowerCase() == id.toLowerCase()) return fields.get(i);
		}
		// Если мы здесь - значит ничего не нашли
		return null;
	}
	
	// Энумератор для наименования префикса таблиц с данными
	public enum DataTablePrefixes {
		Dictionary("_d")
		, Revisions("_rr")
		, Facts("_rf")
		, Values("_rv")
		, CalendarLevels("_rc")
		;
		
        private final String value;

        private DataTablePrefixes(final String newValue) {
            value = newValue;
        }

        public String getValue() { return value; }
	}
}
