package ru.smart_bi.object_descriptors;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import ru.smart_bi.object_descriptors.MetabaseDescriptor.ObjectClasses;
import ru.smart_bi.sql_classes.*;

public class DictionaryDescriptor extends ObjectDescriptor {

	public DictionaryDescriptor(JdbcTemplate jdbcTemplate) throws Exception {
		super(jdbcTemplate);
		//throw new Exception("Error example");
		f_class_id = ObjectClasses.Dictionary.getValue();
		// Создадим обязательные поля
		fields.add(ObjectFieldDescriptor.createField("ID", FieldHandler.createField("id", "serial", false, 1), true, ru.smart_bi.object_descriptors.MetabaseDescriptor.FieldTypes.Regular, null));
		fields.add(ObjectFieldDescriptor.createField("NAME", FieldHandler.createField("name", "text", false, 0), true, ru.smart_bi.object_descriptors.MetabaseDescriptor.FieldTypes.Regular, null));
		fields.add(ObjectFieldDescriptor.createField("PARENT_ID", FieldHandler.createField("parent_id", "int", true, 0), true, ru.smart_bi.object_descriptors.MetabaseDescriptor.FieldTypes.Regular, null));
	}
	
	// Процедура создания словаря в метабазе
	public void CreateDictionary() throws SQLException {
		// Определим наименование таблицы для хранения данных
		SequenceHandler nameSequence = new SequenceHandler("TableName_Seq", jdbcTemplate);
		String dictTableName = DataTablePrefixes.Dictionary.getValue() + nameSequence.GetNextVal();
		// Создадим таблицу для хранения данных
		TableHandler tableHandler = new TableHandler(dictTableName, jdbcTemplate);
		ArrayList<FieldHandler> fieldsArr = new ArrayList<FieldHandler>();
		for (int i = 0; i < fields.size(); i++) {
			// Добавляем только обычные поля, чтобы исключить ошибочно добавленные поля другого типа
			if (fields.get(i).fieldType == ru.smart_bi.object_descriptors.MetabaseDescriptor.FieldTypes.Regular) {
				fieldsArr.add(fields.get(i).fieldHandler);
			}
		}
		String tableComment = "Data table for dictionary [" + object_name + "; ext_id = " + ext_id + "]";
		tableHandler.CreateTable(fieldsArr, tableComment);
		// Запишем базовые параметры объекта в таблицу MetabaseObjects
		CreateObject();
		int object_id = GetObjectId(ext_id);
		// Запишем информацию о созданной таблице в ObjectTables
		String tableName = "ObjectTables";
		TableContentHandler tableContent = new TableContentHandler(tableName, jdbcTemplate);
		ArrayList<FieldContentHandler> fieldsContArr = new ArrayList<FieldContentHandler>();
		fieldsContArr.add(FieldContentHandler.createFieldContent("f_object_id", object_id));
		fieldsContArr.add(FieldContentHandler.createFieldContent("table_name", dictTableName));
		fieldsContArr.add(FieldContentHandler.createFieldContent("del_order", 0));
		tableContent.AddRecord(fieldsContArr);
		// Запишем информацию о полях объекта в таблице ObjectFields
		tableContent.SetTableName("ObjectFields");
		for (int i = 0; i < fields.size(); i++) {
			fieldsContArr = new ArrayList<FieldContentHandler>();
			fieldsContArr.add(FieldContentHandler.createFieldContent("field_alias", fields.get(i).fieldAlias));
			fieldsContArr.add(FieldContentHandler.createFieldContent("table_name", dictTableName));
			fieldsContArr.add(FieldContentHandler.createFieldContent("field_name", fields.get(i).fieldHandler.fieldName));
			fieldsContArr.add(FieldContentHandler.createFieldContent("f_object_id", object_id));
			fieldsContArr.add(FieldContentHandler.createFieldContent("f_field_type_id", fields.get(i).fieldType.getValue()));
			fieldsContArr.add(FieldContentHandler.createFieldContent("linked_table_name", fields.get(i).linked_table_name));
			tableContent.AddRecord(fieldsContArr);
		}
	}
	
	// Функция возвращает имя таблицы, в которой хранятся данные объекта
	public String GetTableName() throws SQLException {
		String tableName = "";
		String queryText = "select table_name from ObjectTables where f_object_id = ?";
		List<String> tableList = jdbcTemplate.query(queryText, new Object[]{GetObjectId(ext_id)},
				new ResultSetExtractor<List<String>>() {
					@Override
					public List<String> extractData(ResultSet rs)
							throws SQLException, DataAccessException {

						List<String> list = new ArrayList<String>();
						while (rs.next()) {
							list.add(rs.getString("table_name"));
						}
						return list;
					}
				});
		for (String curTable: tableList) {
			tableName = curTable;
		}
		return tableName;
	}
	
	// Процедура создает индекс по выбранным полям справочника
	public void CreateIndexForFields(ArrayList<ObjectFieldDescriptor> indexFields, String index_name, boolean isUnique) throws SQLException {
		if (indexFields.size() == 0) return;
		ArrayList<IndexHandler> indexList = new ArrayList<IndexHandler>();
		for (int i = 0; i < indexFields.size(); i++) {
			indexList.add(IndexHandler.createIndexField(indexFields.get(i).fieldHandler.fieldName, i));
		}
		TableHandler tableHandler = new TableHandler(GetTableName(), jdbcTemplate);
		tableHandler.CreateIndex(indexList, index_name, isUnique);
	}
}
