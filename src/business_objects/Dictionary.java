package business_objects;

import java.sql.SQLException;
import java.util.ArrayList;

import business_objects.MetabaseHandler.ObjectClasses;
import sql_classes.ConnectionHandler;
import sql_classes.FieldContentHandler;
import sql_classes.FieldHandler;
import sql_classes.SequenceHandler;
import sql_classes.TableContentHandler;
import sql_classes.TableHandler;

public class Dictionary extends MetabaseObject {

	public Dictionary(ConnectionHandler connection) {
		super(connection);
		f_class_id = ObjectClasses.Dictionary.getValue();
	}
	
	// Процедура создания словаря в метабазе
	public void CreateDictionary() throws SQLException {
		// Определим наименование таблицы для хранения данных
		SequenceHandler nameSequence = new SequenceHandler("TableName_Seq", connection);
		String dictTableName = DataTablePrefixes.Common.getValue() + nameSequence.GetNextVal();
		// Создадим таблицу для хранения данных
		TableHandler tableHandler = new TableHandler(dictTableName, connection);
		ArrayList<FieldHandler> fieldsArr = new ArrayList<FieldHandler>();
		int i;
		for (i = 0; i < fields.size(); i++) {
			fieldsArr.add(fields.get(i).fieldHandler);
		}
		tableHandler.CreateTable(fieldsArr);
		// Запишем базовые параметры объекта в таблицу MetabaseObjects
		CreateObject();
		int object_id = GetObjectId(ext_id);
		// Запишем информацию о созданной таблице в ObjectTables
		String tableName = "ObjectTables";
		TableContentHandler tableContent = new TableContentHandler(tableName, connection);
		ArrayList<FieldContentHandler> fieldsContArr = new ArrayList<FieldContentHandler>();
		fieldsContArr.add(FieldContentHandler.createFieldContent("f_object_id", object_id));
		fieldsContArr.add(FieldContentHandler.createFieldContent("table_name", dictTableName));
		tableContent.AddRecord(fieldsContArr);
		// Запишем информацию о полях объекта в таблице ObjectFields
		tableContent.SetTableName("ObjectFields");
		for (i = 0; i < fields.size(); i++) {
			fieldsContArr = new ArrayList<FieldContentHandler>();
			fieldsContArr.add(FieldContentHandler.createFieldContent("field_alias", fields.get(i).fieldAlias));
			fieldsContArr.add(FieldContentHandler.createFieldContent("table_name", dictTableName));
			fieldsContArr.add(FieldContentHandler.createFieldContent("field_name", fields.get(i).fieldHandler.fieldName));
			fieldsContArr.add(FieldContentHandler.createFieldContent("f_object_id", object_id));
			tableContent.AddRecord(fieldsContArr);
		}
	}
}
