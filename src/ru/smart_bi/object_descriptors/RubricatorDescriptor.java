package ru.smart_bi.object_descriptors;

import java.sql.SQLException;
import java.util.ArrayList;

import ru.smart_bi.object_descriptors.MetabaseDescriptor.ObjectClasses;
import ru.smart_bi.sql_classes.*;

public class RubricatorDescriptor extends ObjectDescriptor {

	public RubricatorDescriptor(ConnectionHandler connection) throws SQLException {
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
		String tableComment = "Revision data table for time series database [" + object_name + "; ext_id = " + ext_id + "]";
		tableHandler.CreateTable(fieldsArr, tableComment);
		// �������� ������� ������
		String factTableName = DataTablePrefixes.Facts.getValue() + nameSequence.GetNextVal();
		tableHandler.SetTableName(factTableName);
		fieldsArr = new ArrayList<FieldHandler>();
		fieldsArr.add(FieldHandler.createField("fact_id", "serial", false, 1));
		int i;
		for (i = 0; i < fields.size(); i++) {
			// ��������� ������ ������� ����
			if (fields.get(i).fieldType == ru.smart_bi.object_descriptors.MetabaseDescriptor.FieldTypes.Regular) {
				fieldsArr.add(fields.get(i).fieldHandler); // ��������� ���� �� ��������� ��������
			}
		}
		// ������� ���� � �������� ���������
		boolean fieldWasInserted = false;
		for (i = 0; i < fields.size(); i++) {
			// ��������� ������ ������� ����
			if ((fields.get(i).fieldType == ru.smart_bi.object_descriptors.MetabaseDescriptor.FieldTypes.RubrUnit) & (!fieldWasInserted)) {
				fieldsArr.add(fields.get(i).fieldHandler); // ��������� ���� �� ��������� ��������
				fieldWasInserted = true;
			}
		}
		tableComment = "Facts data table for time series database [" + object_name + "; ext_id = " + ext_id + "]";
		tableHandler.CreateTable(fieldsArr, tableComment);
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
		tableComment = "Values data table for time series database [" + object_name + "; ext_id = " + ext_id + "]";
		tableHandler.CreateTable(fieldsArr, tableComment);
		// �������� constraint �� ������� �������
		tableTo = revTableName;
		tableHandler.CreateConstraint(tableTo, true);
		// �������� constraint �� ������� ������
		tableTo = factTableName;
		tableHandler.CreateConstraint(tableTo, true);
		// �������� constraint �� ������� CalendarLevels
		tableTo = "CalendarLevels";
		tableHandler.CreateConstraint(tableTo, true);
		// ������� ������� ��� �������� ���������� �� ������� ��������� �����������
		String calendarLevelTableName = DataTablePrefixes.CalendarLevels.getValue() + nameSequence.GetNextVal();
		tableHandler.SetTableName(calendarLevelTableName);
		fieldsArr = new ArrayList<FieldHandler>();
		fieldsArr.add(FieldHandler.createField("calendar_level_id", "serial", false, 1));
		tableComment = "Calendar levels table for time series database [" + object_name + "; ext_id = " + ext_id + "]";
		tableHandler.CreateTable(fieldsArr, tableComment);
		// �������� constraint �� ������� ������� ���������
		tableTo = "CalendarLevels";
		tableHandler.CreateConstraint(tableTo, true);
		// �������� ���������� ������ �� ���� ������ ���������
		ArrayList<IndexHandler> indexfieldsArr = new ArrayList<IndexHandler>();
		indexfieldsArr.add(IndexHandler.createIndexField("f_level_id", 0));
		tableHandler.CreateIndex(indexfieldsArr, "idx_calendar_level", true);
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
		//
		fieldsContArr = new ArrayList<FieldContentHandler>();
		fieldsContArr.add(FieldContentHandler.createFieldContent("f_object_id", object_id));
		fieldsContArr.add(FieldContentHandler.createFieldContent("table_name", calendarLevelTableName));
		fieldsContArr.add(FieldContentHandler.createFieldContent("del_order", 3));
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
