package business_objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import business_objects.MetabaseHandler.ObjectClasses;
import sql_classes.*;

public class Dictionary extends MetabaseObject {

	public Dictionary(ConnectionHandler connection) {
		super(connection);
		f_class_id = ObjectClasses.Dictionary.getValue();
		// �������� ������������ ����
		fields.add(ObjectField.createField("ID", FieldHandler.createField("id", "int", false, 1), true, business_objects.MetabaseHandler.FieldTypes.Regular, null));
		fields.add(ObjectField.createField("NAME", FieldHandler.createField("name", "text", false, 0), true, business_objects.MetabaseHandler.FieldTypes.Regular, null));
		fields.add(ObjectField.createField("PARENT_ID", FieldHandler.createField("parent_id", "int", true, 0), true, business_objects.MetabaseHandler.FieldTypes.Regular, null));
	}
	
	// ��������� �������� ������� � ��������
	public void CreateDictionary() throws SQLException {
		// ��������� ������������ ������� ��� �������� ������
		SequenceHandler nameSequence = new SequenceHandler("TableName_Seq", connection);
		String dictTableName = DataTablePrefixes.Dictionary.getValue() + nameSequence.GetNextVal();
		// �������� ������� ��� �������� ������
		TableHandler tableHandler = new TableHandler(dictTableName, connection);
		ArrayList<FieldHandler> fieldsArr = new ArrayList<FieldHandler>();
		int i;
		for (i = 0; i < fields.size(); i++) {
			// ��������� ������ ������� ����, ����� ��������� �������� ����������� ���� ������� ����
			if (fields.get(i).fieldType == business_objects.MetabaseHandler.FieldTypes.Regular) {
				fieldsArr.add(fields.get(i).fieldHandler);
			}
		}
		tableHandler.CreateTable(fieldsArr);
		// ������� ������� ��������� ������� � ������� MetabaseObjects
		CreateObject();
		int object_id = GetObjectId(ext_id);
		// ������� ���������� � ��������� ������� � ObjectTables
		String tableName = "ObjectTables";
		TableContentHandler tableContent = new TableContentHandler(tableName, connection);
		ArrayList<FieldContentHandler> fieldsContArr = new ArrayList<FieldContentHandler>();
		fieldsContArr.add(FieldContentHandler.createFieldContent("f_object_id", object_id));
		fieldsContArr.add(FieldContentHandler.createFieldContent("table_name", dictTableName));
		fieldsContArr.add(FieldContentHandler.createFieldContent("del_order", 0));
		tableContent.AddRecord(fieldsContArr);
		// ������� ���������� � ����� ������� � ������� ObjectFields
		tableContent.SetTableName("ObjectFields");
		for (i = 0; i < fields.size(); i++) {
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
	
	// ������� ���������� ��� �������� � ������� �������� ������ �������
	public String GetTableName() throws SQLException {
		String tableName = "";
		String queryText = "select table_name from ObjectTables where f_object_id = ?";
		ArrayList<FieldContentHandler> paramsArr = new ArrayList<FieldContentHandler>();
		paramsArr.add(FieldContentHandler.createFieldContent("f_object_id", GetObjectId(ext_id)));
		ResultSet resultSet = connection.CreateResultSet(queryText, paramsArr);
		while (resultSet.next()) {
			tableName = resultSet.getString("table_name");
		}
		return tableName;
	}
}
