package business_objects;

import java.sql.SQLException;
import java.util.ArrayList;

import business_objects.MetabaseHandler.ObjectClasses;
import sql_classes.*;

public class Rubricator extends MetabaseObject {

	public Rubricator(ConnectionHandler connection) throws SQLException {
		super(connection);
		f_class_id = ObjectClasses.Rubricator.getValue();
	}
	
	// ��������� �������� �����������
	public void CreateRubricator(ConnectionHandler connection) throws SQLException {
		// �������� ������� �������
		SequenceHandler nameSequence = new SequenceHandler("TableName_Seq", connection);
		String revTableName = DataTablePrefixes.Revisions.getValue() + nameSequence.GetNextVal();;
		TableHandler tableHandler = new TableHandler(revTableName, connection);
		ArrayList<FieldHandler> fieldsArr = new ArrayList<FieldHandler>();
		fieldsArr.add(FieldHandler.createField("revision_id", "serial", false, 1));
		fieldsArr.add(FieldHandler.createField("revision_name", "text", false, 0));
		fieldsArr.add(FieldHandler.createField("revision_date", "timestamp without time zone", false, 0));
		tableHandler.CreateTable(fieldsArr);
		// �������� ������� ������
		String factTableName = DataTablePrefixes.Facts.getValue() + nameSequence.GetNextVal();
		tableHandler.SetTableName(factTableName);
		fieldsArr = new ArrayList<FieldHandler>();
		fieldsArr.add(FieldHandler.createField("fact_id", "serial", false, 1));
		int i;
		for (i = 0; i < fields.size(); i++) {
			// ��������� ������ ������� ����
			if (fields.get(i).fieldType == business_objects.MetabaseHandler.FieldTypes.Regular) {
				fieldsArr.add(fields.get(i).fieldHandler); // ��������� ���� �� ��������� ��������
			}
		}
		// ������� ���� � �������� ���������
		boolean fieldWasInserted = false;
		for (i = 0; i < fields.size(); i++) {
			// ��������� ������ ������� ����
			if ((fields.get(i).fieldType == business_objects.MetabaseHandler.FieldTypes.RubrUnit) & (!fieldWasInserted)) {
				fieldsArr.add(fields.get(i).fieldHandler); // ��������� ���� �� ��������� ��������
				fieldWasInserted = true;
			}
		}
		// ������� ���� � ������� ���������
		fieldWasInserted = false;
		for (i = 0; i < fields.size(); i++) {
			// ��������� ������ ������� ����
			if ((fields.get(i).fieldType == business_objects.MetabaseHandler.FieldTypes.RubrDateLevel) & (!fieldWasInserted)) {
				fieldsArr.add(fields.get(i).fieldHandler); // ��������� ���� �� ��������� ��������
				fieldWasInserted = true;
			}
		}
		tableHandler.CreateTable(fieldsArr);
		// �������� constraint �� ������� �������
		String tableTo = revTableName;
		tableHandler.CreateConstraint(tableTo, true);
		// �������� ������� ��������
		String valuesTableName = DataTablePrefixes.Values.getValue() + nameSequence.GetNextVal();
		tableHandler.SetTableName(valuesTableName);
		fieldsArr = new ArrayList<FieldHandler>();
		fieldsArr.add(FieldHandler.createField("value_id", "serial", false, 1));
		fieldsArr.add(FieldHandler.createField("value_date", "timestamp without time zone", false, 0));
		fieldsArr.add(FieldHandler.createField("value", "real", false, 0));
		tableHandler.CreateTable(fieldsArr);
		// �������� constraint �� ������� �������
		tableTo = revTableName;
		tableHandler.CreateConstraint(tableTo, true);
		// �������� constraint �� ������� ������
		tableTo = factTableName;
		tableHandler.CreateConstraint(tableTo, true);
		// ������� ������� ��������� ������� � ������� MetabaseObjects
		CreateObject();
		int object_id = GetObjectId(ext_id);
		// ������� ���������� � ��������� �������� � ObjectTables
		String tableName = "ObjectTables";
		TableContentHandler tableContent = new TableContentHandler(tableName, connection);
		ArrayList<FieldContentHandler> fieldsContArr = new ArrayList<FieldContentHandler>();
		fieldsContArr.add(FieldContentHandler.createFieldContent("f_object_id", object_id));
		fieldsContArr.add(FieldContentHandler.createFieldContent("table_name", valuesTableName));
		fieldsContArr.add(FieldContentHandler.createFieldContent("del_order", 0));
		tableContent.AddRecord(fieldsContArr);
		//
		fieldsContArr = new ArrayList<FieldContentHandler>();
		fieldsContArr.add(FieldContentHandler.createFieldContent("f_object_id", object_id));
		fieldsContArr.add(FieldContentHandler.createFieldContent("table_name", factTableName));
		fieldsContArr.add(FieldContentHandler.createFieldContent("del_order", 1));
		tableContent.AddRecord(fieldsContArr);
		//
		fieldsContArr = new ArrayList<FieldContentHandler>();
		fieldsContArr.add(FieldContentHandler.createFieldContent("f_object_id", object_id));
		fieldsContArr.add(FieldContentHandler.createFieldContent("table_name", revTableName));
		fieldsContArr.add(FieldContentHandler.createFieldContent("del_order", 2));
		tableContent.AddRecord(fieldsContArr);
		// ������� ���������� � ����� ������� � ������� ObjectFields (��� ���������������� ���� ��������� � ������� ������)
		tableContent.SetTableName("ObjectFields");
		for (i = 0; i < fields.size(); i++) {
			fieldsContArr = new ArrayList<FieldContentHandler>();
			fieldsContArr.add(FieldContentHandler.createFieldContent("field_alias", fields.get(i).fieldAlias));
			fieldsContArr.add(FieldContentHandler.createFieldContent("table_name", factTableName));
			fieldsContArr.add(FieldContentHandler.createFieldContent("field_name", fields.get(i).fieldHandler.fieldName));
			fieldsContArr.add(FieldContentHandler.createFieldContent("f_object_id", object_id));
			fieldsContArr.add(FieldContentHandler.createFieldContent("f_field_type_id", fields.get(i).fieldType.getValue()));
			fieldsContArr.add(FieldContentHandler.createFieldContent("linked_table_name", fields.get(i).linked_table_name));
			tableContent.AddRecord(fieldsContArr);
		}
	}
}
