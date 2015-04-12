package business_objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import sql_classes.*;

public class MetabaseHandler {

	// Процедура удаления структуры метабазы
	void DeleteMetabase(ConnectionHandler connection) throws SQLException {
		// Удалим таблицы
		TableHandler tableHandler = new TableHandler("", connection);
		ArrayList<String> tableList = new ArrayList<String>();
		// Базовые таблицы
		tableList.add("ObjectTables");
		tableList.add("MetabaseObjects");
		tableList.add("ObjectClasses");
		// Таблицы экземпляров объектов метабазы
		String queryText = "select table_name from ObjectTables";
		ArrayList<FieldContentHandler> paramsArr = new ArrayList<FieldContentHandler>();
		ResultSet resultSet = connection.CreateResultSet(queryText, paramsArr);
		while (resultSet.next()) {
			tableList.add(resultSet.getString("table_name"));
		}
		// Удалим выбранные таблицы
		int i;
		for (i = 0; i < tableList.size(); i++) {
			tableHandler.SetTableName(tableList.get(i));
			tableHandler.DropTable();
		}
		// Удалим секвенции
		SequenceHandler sequenceHandler = new SequenceHandler("", connection);
		ArrayList<String> sequenceList = new ArrayList<String>();
		// Соберем секвенции для удаления
		sequenceList.add("TableName_Seq");
		// Удалим секвенции
		for (i = 0; i < sequenceList.size(); i++) {
			sequenceHandler.SetSequenceName(sequenceList.get(i));
			sequenceHandler.DropSequence();
		}
	}

	// Процедура добаввляет запись в таблицу ObjectClasses
	void AddObjectClass(ConnectionHandler connection, int class_id, String class_name) throws SQLException {
		String tableName = "ObjectClasses";
		TableContentHandler tableContent = new TableContentHandler(tableName, connection);
		ArrayList<FieldContentHandler> fieldsContArr = new ArrayList<FieldContentHandler>();
		fieldsContArr.add(FieldContentHandler.createFieldContent("class_id", class_id));
		fieldsContArr.add(FieldContentHandler.createFieldContent("class_name", class_name));
		tableContent.AddRecord(fieldsContArr);
	}
	
	// Процедура создания таблицы ObjectTables (таблицы, в которых хранятся данные объектов метабазы)
	void CreateObjectTables(ConnectionHandler connection) throws SQLException {
		// Создадим саму таблицу
		String tableName = "ObjectTables";
		TableHandler tableHandler = new TableHandler(tableName, connection);
		ArrayList<FieldHandler> fieldsArr = new ArrayList<FieldHandler>();
		fieldsArr.add(FieldHandler.createField("table_id", "serial", false, 1));
		fieldsArr.add(FieldHandler.createField("table_name", "text", false, 0));
		tableHandler.CreateTable(fieldsArr);
		// Создадим уникальный индекс по наименованию
		ArrayList<IndexHandler> indexfieldsArr = new ArrayList<IndexHandler>();
		indexfieldsArr.add(IndexHandler.createIndexField("table_name", 0));
		tableHandler.CreateIndex(indexfieldsArr, "idx_table_name", true);
		// Создадим constraint
		String tableTo = "MetabaseObjects";
		tableHandler.CreateConstraint(tableTo, false);
	}
	
	// Процедура создания секвенции для наименования таблиц с данными
	void CreateTableNameSeq(ConnectionHandler connection) throws SQLException {
		String sequenceName = "TableName_Seq";
		SequenceHandler sequenceHandler = new SequenceHandler(sequenceName, connection);
		sequenceHandler.CreateSequence();
	}
	
	// Процедура создания таблицы ObjectClasses (классов объектов метабазы)
	void CreateObjectClasses(ConnectionHandler connection) throws SQLException {
		// Создадим саму таблицу
		String tableName = "ObjectClasses";
		TableHandler tableHandler = new TableHandler(tableName, connection);
		ArrayList<FieldHandler> fieldsArr = new ArrayList<FieldHandler>();
		fieldsArr.add(FieldHandler.createField("class_id", "int", false, 1));
		fieldsArr.add(FieldHandler.createField("class_name", "text", false, 0));
		tableHandler.CreateTable(fieldsArr);
		// Создадим уникальный индекс по наименованию
		ArrayList<IndexHandler> indexfieldsArr = new ArrayList<IndexHandler>();
		indexfieldsArr.add(IndexHandler.createIndexField("class_name", 0));
		tableHandler.CreateIndex(indexfieldsArr, "idx_class_name", true);
		// Инициализируем значения
		AddObjectClass(connection, ObjectClasses.Dictionary.getValue(), "Dictionary");
		AddObjectClass(connection, ObjectClasses.Rubricator.getValue(), "Rubricator");
	}

	// Процедура создания таблицы MetabaseObjects (объектов метабазы)
	void CreateMetabaseObjects(ConnectionHandler connection) throws SQLException {
		// Создадим саму таблицу
		String tableName = "MetabaseObjects";
		TableHandler tableHandler = new TableHandler(tableName, connection);
		ArrayList<FieldHandler> fieldsArr = new ArrayList<FieldHandler>();
		fieldsArr.add(FieldHandler.createField("object_id", "serial", false, 1));
		fieldsArr.add(FieldHandler.createField("parent_object_id", "int", true, 0));
		fieldsArr.add(FieldHandler.createField("object_name", "text", false, 0));
		fieldsArr.add(FieldHandler.createField("ext_id", "char(100)", false, 0));
		tableHandler.CreateTable(fieldsArr);
		// Создадим constraint
		String tableTo = "ObjectClasses";
		tableHandler.CreateConstraint(tableTo, false);
		// Сделаем constraint "на себя" (иерархическую таблицу) 
		tableTo = "MetabaseObjects";
		tableHandler.CreateConstraint(tableTo, false, "parent_object_id");
		// Создадим уникальный индекс по ext_id
		ArrayList<IndexHandler> indexfieldsArr = new ArrayList<IndexHandler>();
		indexfieldsArr.add(IndexHandler.createIndexField("ext_id", 0));
		tableHandler.CreateIndex(indexfieldsArr, "idx_ext_id", true);
	}
	
	// Процедура добавления записи в таблицу MetabaseObjects
	void AddMetabaseRecord(int object_id, String object_name, String ext_id) {
		
	}

	// Процедура инициализации метабазы
	public void CreateMetabase(ConnectionHandler connection, boolean recreate)
			throws ClassNotFoundException, SQLException {
		// Создадим подключение
		if (recreate) {
			// При наличии опции "пересоздания" удалим старые таблицы метабазы
			DeleteMetabase(connection);
		}
		// Создадим таблицу ObjectClasses
		CreateObjectClasses(connection);
		// Создадим таблицу MetabaseObjects
		CreateMetabaseObjects(connection);
		// Создадим таблицу ObjectTables
		CreateObjectTables(connection);
		// Создадим для нее sequence для наименований таблиц, содержащих хданные объектов
		CreateTableNameSeq(connection);
	}
	
	// Энумератор классов объектов метабазы
	public enum ObjectClasses {
		Dictionary(1)
		, Rubricator(2);
		
        private final int value;

        private ObjectClasses(final int newValue) {
            value = newValue;
        }

        public int getValue() { return value; }
	}
}
