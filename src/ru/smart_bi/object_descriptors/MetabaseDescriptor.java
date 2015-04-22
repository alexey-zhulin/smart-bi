package ru.smart_bi.object_descriptors;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ru.smart_bi.sql_classes.*;

public class MetabaseDescriptor {

	// ��������� �������� ��������� ��������
	public void DeleteMetabase(ConnectionHandler connection) throws SQLException {
		// ������ �������
		TableHandler tableHandler = new TableHandler("", connection);
		ArrayList<String> tableList = new ArrayList<String>();
		// ������� ����������� �������� ��������
		tableHandler.SetTableName("ObjectTables");
		if (tableHandler.TableExists()) {
			String queryText = "select table_name from ObjectTables order by del_order";
			ArrayList<FieldContentHandler> paramsArr = new ArrayList<FieldContentHandler>();
			ResultSet resultSet = connection.CreateResultSet(queryText, paramsArr);
			while (resultSet.next()) {
				tableList.add(resultSet.getString("table_name"));
			}
		}
		// ������� �������
		tableList.add("ObjectTables");
		tableList.add("ObjectFields");
		tableList.add("MetabaseObjects");
		tableList.add("ObjectClasses");
		tableList.add("ObjectFieldTypes");
		tableList.add("CalendarLevels");
		// ������ ��������� �������
		int i;
		for (i = 0; i < tableList.size(); i++) {
			tableHandler.SetTableName(tableList.get(i));
			tableHandler.DropTable();
		}
		// ������ ���������
		SequenceHandler sequenceHandler = new SequenceHandler("", connection);
		ArrayList<String> sequenceList = new ArrayList<String>();
		// ������� ��������� ��� ��������
		sequenceList.add("TableName_Seq");
		// ������ ���������
		for (i = 0; i < sequenceList.size(); i++) {
			sequenceHandler.SetSequenceName(sequenceList.get(i));
			sequenceHandler.DropSequence();
		}
	}

	// ��������� ���������� ������ � ������� ObjectClasses
	void AddObjectClass(ConnectionHandler connection, int class_id, String class_name) throws SQLException {
		String tableName = "ObjectClasses";
		TableContentHandler tableContent = new TableContentHandler(tableName, connection);
		ArrayList<FieldContentHandler> fieldsContArr = new ArrayList<FieldContentHandler>();
		fieldsContArr.add(FieldContentHandler.createFieldContent("class_id", class_id));
		fieldsContArr.add(FieldContentHandler.createFieldContent("class_name", class_name));
		tableContent.AddRecord(fieldsContArr);
	}
	
	// ��������� �������� ������� ObjectTables (�������, � ������� �������� ������ �������� ��������)
	void CreateObjectTables(ConnectionHandler connection) throws SQLException {
		// �������� ���� �������
		String tableName = "ObjectTables";
		TableHandler tableHandler = new TableHandler(tableName, connection);
		ArrayList<FieldHandler> fieldsArr = new ArrayList<FieldHandler>();
		fieldsArr.add(FieldHandler.createField("table_id", "serial", false, 1));
		fieldsArr.add(FieldHandler.createField("table_name", "text", false, 0));
		fieldsArr.add(FieldHandler.createField("del_order", "integer", false, 0)); // ������� ������������ (����� � ����� ��������)
		String tableComment = "Metabase object list table";
		tableHandler.CreateTable(fieldsArr, tableComment);
		// �������� ���������� ������ �� ������������
		ArrayList<IndexHandler> indexfieldsArr = new ArrayList<IndexHandler>();
		indexfieldsArr.add(IndexHandler.createIndexField("table_name", 0));
		tableHandler.CreateIndex(indexfieldsArr, "idx_table_name", true);
		// �������� constraint
		String tableTo = "MetabaseObjects";
		tableHandler.CreateConstraint(tableTo, false);
	}

	// ��������� ���������� ������ � ������� ObjectClasses
	void AddObjectFieldType(ConnectionHandler connection, int field_type_id, String field_type_name) throws SQLException {
		String tableName = "ObjectFieldTypes";
		TableContentHandler tableContent = new TableContentHandler(tableName, connection);
		ArrayList<FieldContentHandler> fieldsContArr = new ArrayList<FieldContentHandler>();
		fieldsContArr.add(FieldContentHandler.createFieldContent("field_type_id", field_type_id));
		fieldsContArr.add(FieldContentHandler.createFieldContent("field_type_name", field_type_name));
		tableContent.AddRecord(fieldsContArr);
	}
	
	// ��������� �������� ������� ObjectFieldTypes (�������, � ������� ������ ���� �����)
	void CreateObjectFieldTypes(ConnectionHandler connection) throws SQLException {
		// �������� ���� �������
		String tableName = "ObjectFieldTypes";
		TableHandler tableHandler = new TableHandler(tableName, connection);
		ArrayList<FieldHandler> fieldsArr = new ArrayList<FieldHandler>();
		fieldsArr.add(FieldHandler.createField("field_type_id", "int", false, 1));
		fieldsArr.add(FieldHandler.createField("field_type_name", "text", false, 0));
		String tableComment = "Object field list table";
		tableHandler.CreateTable(fieldsArr, tableComment);
		// �������������� ��������
		AddObjectFieldType(connection, FieldTypes.Regular.getValue(), "Regular");
		AddObjectFieldType(connection, FieldTypes.RubrUnit.getValue(), "RubrUnit");
	}
	
	// ��������� ��������� ������ � ������� AddObjectCalendarLevel
	void AddObjectCalendarLevel(ConnectionHandler connection, int level_id, String level_name) throws SQLException {
		String tableName = "CalendarLevels";
		TableContentHandler tableContent = new TableContentHandler(tableName, connection);
		ArrayList<FieldContentHandler> fieldsContArr = new ArrayList<FieldContentHandler>();
		fieldsContArr.add(FieldContentHandler.createFieldContent("level_id", level_id));
		fieldsContArr.add(FieldContentHandler.createFieldContent("level_name", level_name));
		tableContent.AddRecord(fieldsContArr);
	}
	
	// ��������� �������� ������� CalendarLevels (�������, �� ���������� ������ ���������)
	void CreateCalendarLevels(ConnectionHandler connection) throws SQLException {
		// �������� ���� �������
		String tableName = "CalendarLevels";
		TableHandler tableHandler = new TableHandler(tableName, connection);
		ArrayList<FieldHandler> fieldsArr = new ArrayList<FieldHandler>();
		fieldsArr.add(FieldHandler.createField("level_id", "int", false, 1));
		fieldsArr.add(FieldHandler.createField("level_name", "text", false, 0));
		String tableComment = "Calendar level dictionary";
		tableHandler.CreateTable(fieldsArr, tableComment);
		// �������������� ��������
		AddObjectCalendarLevel(connection, CalendarLevels.Day.getValue(), "Day");
		AddObjectCalendarLevel(connection, CalendarLevels.Week.getValue(), "Week");
		AddObjectCalendarLevel(connection, CalendarLevels.Month.getValue(), "Month");
		AddObjectCalendarLevel(connection, CalendarLevels.Quarter.getValue(), "Quarter");
		AddObjectCalendarLevel(connection, CalendarLevels.Year.getValue(), "Year");
	}
	
	// ��������� �������� ������� ObjectFields (�������, � ������� ������ ��������� ����� �������)
	void CreateObjectFields(ConnectionHandler connection) throws SQLException {
		// �������� ���� �������
		String tableName = "ObjectFields";
		TableHandler tableHandler = new TableHandler(tableName, connection);
		ArrayList<FieldHandler> fieldsArr = new ArrayList<FieldHandler>();
		fieldsArr.add(FieldHandler.createField("field_id", "serial", false, 1));
		fieldsArr.add(FieldHandler.createField("field_alias", "text", false, 0));
		fieldsArr.add(FieldHandler.createField("table_name", "text", false, 0));
		fieldsArr.add(FieldHandler.createField("field_name", "text", false, 0));
		fieldsArr.add(FieldHandler.createField("linked_table_name", "text", true, 0));
		String tableComment = "Object field list table";
		tableHandler.CreateTable(fieldsArr, tableComment);
		// �������� constraint
		String tableTo = "MetabaseObjects";
		tableHandler.CreateConstraint(tableTo, false);
		tableTo = "ObjectFieldTypes";
		tableHandler.CreateConstraint(tableTo, false);
	}
	
	// ��������� �������� ��������� ��� ������������ ������ � �������
	void CreateTableNameSeq(ConnectionHandler connection) throws SQLException {
		String sequenceName = "TableName_Seq";
		SequenceHandler sequenceHandler = new SequenceHandler(sequenceName, connection);
		sequenceHandler.CreateSequence();
	}
	
	// ��������� �������� ������� ObjectClasses (������� �������� ��������)
	void CreateObjectClasses(ConnectionHandler connection) throws SQLException {
		// �������� ���� �������
		String tableName = "ObjectClasses";
		TableHandler tableHandler = new TableHandler(tableName, connection);
		ArrayList<FieldHandler> fieldsArr = new ArrayList<FieldHandler>();
		fieldsArr.add(FieldHandler.createField("class_id", "int", false, 1));
		fieldsArr.add(FieldHandler.createField("class_name", "text", false, 0));
		String tableComment = "Metabase classes dictionary";
		tableHandler.CreateTable(fieldsArr, tableComment);
		// �������� ���������� ������ �� ������������
		ArrayList<IndexHandler> indexfieldsArr = new ArrayList<IndexHandler>();
		indexfieldsArr.add(IndexHandler.createIndexField("class_name", 0));
		tableHandler.CreateIndex(indexfieldsArr, "idx_class_name", true);
		// �������������� ��������
		AddObjectClass(connection, ObjectClasses.Undefined.getValue(), "Undefined");
		AddObjectClass(connection, ObjectClasses.Folder.getValue(), "Folder");
		AddObjectClass(connection, ObjectClasses.Dictionary.getValue(), "Dictionary");
		AddObjectClass(connection, ObjectClasses.Rubricator.getValue(), "Rubricator");
	}

	// ��������� �������� ������� MetabaseObjects (�������� ��������)
	void CreateMetabaseObjects(ConnectionHandler connection) throws SQLException {
		// �������� ���� �������
		String tableName = "MetabaseObjects";
		TableHandler tableHandler = new TableHandler(tableName, connection);
		ArrayList<FieldHandler> fieldsArr = new ArrayList<FieldHandler>();
		fieldsArr.add(FieldHandler.createField("object_id", "serial", false, 1));
		fieldsArr.add(FieldHandler.createField("parent_object_id", "int", true, 0));
		fieldsArr.add(FieldHandler.createField("object_name", "text", false, 0));
		fieldsArr.add(FieldHandler.createField("ext_id", "char(100)", false, 0));
		String tableComment = "Metabase object list";
		tableHandler.CreateTable(fieldsArr, tableComment);
		// �������� constraint
		String tableTo = "ObjectClasses";
		tableHandler.CreateConstraint(tableTo, false);
		// ������� constraint "�� ����" (������������� �������) 
		tableTo = "MetabaseObjects";
		tableHandler.CreateConstraint(tableTo, false, "parent_object_id");
		// �������� ���������� ������ �� ext_id
		ArrayList<IndexHandler> indexfieldsArr = new ArrayList<IndexHandler>();
		indexfieldsArr.add(IndexHandler.createIndexField("ext_id", 0));
		tableHandler.CreateIndex(indexfieldsArr, "idx_ext_id", true);
	}
	
	// ��������� ���������� ������ � ������� MetabaseObjects
	void AddMetabaseRecord(int object_id, String object_name, String ext_id) {
		
	}

	// ��������� ������������� ��������
	public void CreateMetabase(ConnectionHandler connection, boolean recreate)
			throws ClassNotFoundException, SQLException {
		// �������� �����������
		if (recreate) {
			// ��� ������� ����� "������������" ������ ������ ������� ��������
			DeleteMetabase(connection);
		}
		// �������� ������� ObjectClasses
		CreateObjectClasses(connection);
		// �������� ������� MetabaseObjects
		CreateMetabaseObjects(connection);
		// �������� ������� ObjectTables
		CreateObjectTables(connection);
		// �������� ��� ��� sequence ��� ������������ ������, ���������� ������� ��������
		CreateTableNameSeq(connection);
		// �������� ������� � ������ �����
		CreateObjectFieldTypes(connection);
		// �������� ������� � �������� ���������
		CreateCalendarLevels(connection);
		// �������� ������� �������� ����� ��������
		CreateObjectFields(connection);
	}
	
	// ���������� ������� �������� ��������
	public enum ObjectClasses {
		Undefined(0)
		, Folder(1)
		, Dictionary(2)
		, Rubricator(3);
		
        private final int value;

        private ObjectClasses(final int newValue) {
            value = newValue;
        }

        public int getValue() { return value; }
	}
	
	// ���������� ����� ����� �������� ��������
	public enum FieldTypes {
		Regular(0)
		, RubrUnit(1);
		
        private final int value;

        private FieldTypes(final int newValue) {
            value = newValue;
        }

        public int getValue() { return value; }
	}
	
	// ���������� ������� ���������
	public enum CalendarLevels {
		Day(0)
		, Week(1)
		, Month(2)
		, Quarter(3)
		, Year(4)
		;
		
        private final int value;

        private CalendarLevels(final int newValue) {
            value = newValue;
        }

        public int getValue() { return value; }
	}
}
