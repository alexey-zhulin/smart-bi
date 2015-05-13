package ru.smart_bi.object_descriptors;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import ru.smart_bi.object_descriptors.MetabaseDescriptor.ObjectClasses;
import ru.smart_bi.sql_classes.*;

public class ObjectDescriptor {
	public Object object_id;
	public Object parent_object_id;
	public Object object_name;
	public Object ext_id;
	public ArrayList<ObjectFieldDescriptor> fields;
	JdbcTemplate jdbcTemplate;
	public int f_class_id;
	public String class_name;
	
	public ObjectDescriptor(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		f_class_id = ObjectClasses.Undefined.getValue();
		// Инициализируем массив полей
		fields = new ArrayList<ObjectFieldDescriptor>();
	}
	
	// Процедура создания объекта (регистрация в таблице MetabaseObjects)
	public void CreateObject() throws SQLException {
		// Добавим запись в таблицу MetabaseObjects
		String tableName = "MetabaseObjects";
		TableContentHandler tableContent = new TableContentHandler(tableName, jdbcTemplate);
		ArrayList<FieldContentHandler> fieldsArr = new ArrayList<FieldContentHandler>();
		fieldsArr.add(FieldContentHandler.createFieldContent("object_name", object_name));
		fieldsArr.add(FieldContentHandler.createFieldContent("ext_id", ext_id));
		fieldsArr.add(FieldContentHandler.createFieldContent("f_class_id", f_class_id));
		fieldsArr.add(FieldContentHandler.createFieldContent("parent_object_id", parent_object_id));
		tableContent.AddRecord(fieldsArr);
		// Установим значение идентификатора
		object_id = GetObjectId((String) ext_id);
	}
	
	// Процедура получения идентификатора объекта по внешнему идентификатору
	public int GetObjectId(String ext_id) throws SQLException {
		int id = -1;
		String queryText = "select object_id from MetabaseObjects where ext_id = ?";
		List<Integer> valuesList = jdbcTemplate.query(queryText, new Object[]{ext_id},
				new ResultSetExtractor<List<Integer>>() {
					@Override
					public List<Integer> extractData(ResultSet rs)
							throws SQLException, DataAccessException {

						List<Integer> list = new ArrayList<Integer>();
						while (rs.next()) {
							list.add(rs.getInt("object_id"));
						}
						return list;
					}
				});

		for (int curVal: valuesList) {
			id = curVal;
		}
		return id;
	}
	
	// Функция возвращает поле по его идентификатору в базе (наименованию поля в таблице)
	public ObjectFieldDescriptor FieldById(String id) {
		for (int i = 0; i < fields.size(); i ++) {
			if (fields.get(i).fieldHandler.fieldName.toLowerCase() == id.toLowerCase()) return fields.get(i);
		}
		// Если мы здесь - значит ничего не нашли
		return null;
	}
	
	public String toString() {
		return object_name + " [" + ext_id + "]";
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
