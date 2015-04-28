package ru.smart_bi.object_descriptors;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import ru.smart_bi.sql_classes.*;

public class MetabaseDescriptor {
	private boolean recreateMetabase;
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	public void setRecreateMetabase(boolean recreateMetabase) {
		this.recreateMetabase = recreateMetabase;
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return this.jdbcTemplate;
	}

	public boolean getRecreateMetabase() {
		return this.recreateMetabase;
	}

	// Процедура закрытия подключения
	public void closeConnection() throws SQLException {
		Logger log = Logger.getRootLogger();
		log.info("Closing connection...");
		dataSource.getConnection().close();
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(this.dataSource);
	}

	// Процедура инициализации метабазы
	public void initMetabase() throws ClassNotFoundException, SQLException {
		Logger log = Logger.getRootLogger();
		log.info("Initializing metabase...");
		if (recreateMetabase)
			CreateMetabase();
	}

	// Процедура удаления структуры метабазы
	public void DeleteMetabase() throws SQLException {
		// Удалим таблицы
		TableHandler tableHandler = new TableHandler("", jdbcTemplate);
		// Таблицы экземпляров объектов метабазы
		List<String> tableList = new ArrayList<String>();
		tableHandler.SetTableName("ObjectTables");
		if (tableHandler.TableExists()) {
			String queryText = "select table_name from ObjectTables order by del_order";
			tableList = jdbcTemplate.query(queryText,
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
		}
		// Базовые таблицы
		tableList.add("ObjectTables");
		tableList.add("ObjectFields");
		tableList.add("MetabaseObjects");
		tableList.add("ObjectClasses");
		tableList.add("ObjectFieldTypes");
		tableList.add("CalendarLevels");
		// Удалим выбранные таблицы
		for (int i = 0; i < tableList.size(); i++) {
			tableHandler.SetTableName(tableList.get(i));
			tableHandler.DropTable();
		}
		// Удалим секвенции
		SequenceHandler sequenceHandler = new SequenceHandler("", jdbcTemplate);
		ArrayList<String> sequenceList = new ArrayList<String>();
		// Соберем секвенции для удаления
		sequenceList.add("TableName_Seq");
		// Удалим секвенции
		for (int i = 0; i < sequenceList.size(); i++) {
			sequenceHandler.SetSequenceName(sequenceList.get(i));
			sequenceHandler.DropSequence();
		}
	}

	// Процедура добаввляет запись в таблицу ObjectClasses
	void AddObjectClass(int class_id, String class_name) throws SQLException {
		String tableName = "ObjectClasses";
		TableContentHandler tableContent = new TableContentHandler(tableName,
				jdbcTemplate);
		ArrayList<FieldContentHandler> fieldsContArr = new ArrayList<FieldContentHandler>();
		fieldsContArr.add(FieldContentHandler.createFieldContent("class_id",
				class_id));
		fieldsContArr.add(FieldContentHandler.createFieldContent("class_name",
				class_name));
		tableContent.AddRecord(fieldsContArr);
	}

	// Процедура создания таблицы ObjectTables (таблицы, в которых хранятся
	// данные объектов метабазы)
	void CreateObjectTables() throws SQLException {
		// Создадим саму таблицу
		String tableName = "ObjectTables";
		TableHandler tableHandler = new TableHandler(tableName, jdbcTemplate);
		ArrayList<FieldHandler> fieldsArr = new ArrayList<FieldHandler>();
		fieldsArr.add(FieldHandler.createField("table_id", "serial", false, 1));
		fieldsArr.add(FieldHandler.createField("table_name", "text", false, 0));
		fieldsArr.add(FieldHandler
				.createField("del_order", "integer", false, 0)); // Порядок
																	// расположения
																	// (важно в
																	// части
																	// удаления)
		String tableComment = "Metabase object list table";
		tableHandler.CreateTable(fieldsArr, tableComment);
		// Создадим уникальный индекс по наименованию
		ArrayList<IndexHandler> indexfieldsArr = new ArrayList<IndexHandler>();
		indexfieldsArr.add(IndexHandler.createIndexField("table_name", 0));
		tableHandler.CreateIndex(indexfieldsArr, "idx_table_name", true);
		// Создадим constraint
		String tableTo = "MetabaseObjects";
		tableHandler.CreateConstraint(tableTo, false);
	}

	// Процедура добаввляет запись в таблицу ObjectClasses
	void AddObjectFieldType(int field_type_id, String field_type_name)
			throws SQLException {
		String tableName = "ObjectFieldTypes";
		TableContentHandler tableContent = new TableContentHandler(tableName,
				jdbcTemplate);
		ArrayList<FieldContentHandler> fieldsContArr = new ArrayList<FieldContentHandler>();
		fieldsContArr.add(FieldContentHandler.createFieldContent(
				"field_type_id", field_type_id));
		fieldsContArr.add(FieldContentHandler.createFieldContent(
				"field_type_name", field_type_name));
		tableContent.AddRecord(fieldsContArr);
	}

	// Процедура создания таблицы ObjectFieldTypes (таблицы, в которой храним
	// типы полей)
	void CreateObjectFieldTypes() throws SQLException {
		// Создадим саму таблицу
		String tableName = "ObjectFieldTypes";
		TableHandler tableHandler = new TableHandler(tableName, jdbcTemplate);
		ArrayList<FieldHandler> fieldsArr = new ArrayList<FieldHandler>();
		fieldsArr.add(FieldHandler
				.createField("field_type_id", "int", false, 1));
		fieldsArr.add(FieldHandler.createField("field_type_name", "text",
				false, 0));
		String tableComment = "Object field list table";
		tableHandler.CreateTable(fieldsArr, tableComment);
		// Инициализируем значения
		AddObjectFieldType(FieldTypes.Regular.getValue(), "Regular");
		AddObjectFieldType(FieldTypes.RubrUnit.getValue(), "RubrUnit");
	}

	// Процедура добавляет запись в таблицу AddObjectCalendarLevel
	void AddObjectCalendarLevel(int level_id, String level_name)
			throws SQLException {
		String tableName = "CalendarLevels";
		TableContentHandler tableContent = new TableContentHandler(tableName,
				jdbcTemplate);
		ArrayList<FieldContentHandler> fieldsContArr = new ArrayList<FieldContentHandler>();
		fieldsContArr.add(FieldContentHandler.createFieldContent("level_id",
				level_id));
		fieldsContArr.add(FieldContentHandler.createFieldContent("level_name",
				level_name));
		tableContent.AddRecord(fieldsContArr);
	}

	// Процедура создания таблицы CalendarLevels (таблица, со значениями уровня
	// календаря)
	void CreateCalendarLevels() throws SQLException {
		// Создадим саму таблицу
		String tableName = "CalendarLevels";
		TableHandler tableHandler = new TableHandler(tableName, jdbcTemplate);
		ArrayList<FieldHandler> fieldsArr = new ArrayList<FieldHandler>();
		fieldsArr.add(FieldHandler.createField("level_id", "int", false, 1));
		fieldsArr.add(FieldHandler.createField("level_name", "text", false, 0));
		String tableComment = "Calendar level dictionary";
		tableHandler.CreateTable(fieldsArr, tableComment);
		// Инициализируем значения
		AddObjectCalendarLevel(CalendarLevels.Day.getValue(), "Day");
		AddObjectCalendarLevel(CalendarLevels.Week.getValue(), "Week");
		AddObjectCalendarLevel(CalendarLevels.Month.getValue(), "Month");
		AddObjectCalendarLevel(CalendarLevels.Quarter.getValue(), "Quarter");
		AddObjectCalendarLevel(CalendarLevels.Year.getValue(), "Year");
	}

	// Процедура создания таблицы ObjectFields (таблицы, в которой храним
	// структуру полей объекта)
	void CreateObjectFields() throws SQLException {
		// Создадим саму таблицу
		String tableName = "ObjectFields";
		TableHandler tableHandler = new TableHandler(tableName, jdbcTemplate);
		ArrayList<FieldHandler> fieldsArr = new ArrayList<FieldHandler>();
		fieldsArr.add(FieldHandler.createField("field_id", "serial", false, 1));
		fieldsArr
				.add(FieldHandler.createField("field_alias", "text", false, 0));
		fieldsArr.add(FieldHandler.createField("table_name", "text", false, 0));
		fieldsArr.add(FieldHandler.createField("field_name", "text", false, 0));
		fieldsArr.add(FieldHandler.createField("linked_table_name", "text",
				true, 0));
		String tableComment = "Object field list table";
		tableHandler.CreateTable(fieldsArr, tableComment);
		// Создадим constraint
		String tableTo = "MetabaseObjects";
		tableHandler.CreateConstraint(tableTo, false);
		tableTo = "ObjectFieldTypes";
		tableHandler.CreateConstraint(tableTo, false);
	}

	// Процедура создания секвенции для наименования таблиц с данными
	void CreateTableNameSeq() throws SQLException {
		String sequenceName = "TableName_Seq";
		SequenceHandler sequenceHandler = new SequenceHandler(sequenceName,
				jdbcTemplate);
		sequenceHandler.CreateSequence();
	}

	// Процедура создания таблицы ObjectClasses (классов объектов метабазы)
	void CreateObjectClasses() throws SQLException {
		// Создадим саму таблицу
		String tableName = "ObjectClasses";
		TableHandler tableHandler = new TableHandler(tableName, jdbcTemplate);
		ArrayList<FieldHandler> fieldsArr = new ArrayList<FieldHandler>();
		fieldsArr.add(FieldHandler.createField("class_id", "int", false, 1));
		fieldsArr.add(FieldHandler.createField("class_name", "text", false, 0));
		String tableComment = "Metabase classes dictionary";
		tableHandler.CreateTable(fieldsArr, tableComment);
		// Создадим уникальный индекс по наименованию
		ArrayList<IndexHandler> indexfieldsArr = new ArrayList<IndexHandler>();
		indexfieldsArr.add(IndexHandler.createIndexField("class_name", 0));
		tableHandler.CreateIndex(indexfieldsArr, "idx_class_name", true);
		// Инициализируем значения
		AddObjectClass(ObjectClasses.Undefined.getValue(),
				"Undefined");
		AddObjectClass(ObjectClasses.Folder.getValue(), "Folder");
		AddObjectClass(ObjectClasses.Dictionary.getValue(),
				"Dictionary");
		AddObjectClass(ObjectClasses.Rubricator.getValue(),
				"Rubricator");
	}

	// Процедура создания таблицы MetabaseObjects (объектов метабазы)
	void CreateMetabaseObjects() throws SQLException {
		// Создадим саму таблицу
		String tableName = "MetabaseObjects";
		TableHandler tableHandler = new TableHandler(tableName, jdbcTemplate);
		ArrayList<FieldHandler> fieldsArr = new ArrayList<FieldHandler>();
		fieldsArr
				.add(FieldHandler.createField("object_id", "serial", false, 1));
		fieldsArr.add(FieldHandler.createField("parent_object_id", "int", true,
				0));
		fieldsArr
				.add(FieldHandler.createField("object_name", "text", false, 0));
		fieldsArr
				.add(FieldHandler.createField("ext_id", "char(100)", false, 0));
		String tableComment = "Metabase object list";
		tableHandler.CreateTable(fieldsArr, tableComment);
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

	// Процедура создания метабазы
	public void CreateMetabase() throws ClassNotFoundException, SQLException {
		// Удалим старые таблицы метабазы
		DeleteMetabase();
		// Создадим таблицу ObjectClasses
		CreateObjectClasses();
		// Создадим таблицу MetabaseObjects
		CreateMetabaseObjects();
		// Создадим таблицу ObjectTables
		CreateObjectTables();
		// Создадим для нее sequence для наименований таблиц, содержащих хданные
		// объектов
		CreateTableNameSeq();
		// Создадим таблицу с типами полей
		CreateObjectFieldTypes();
		// Создадим таблицу с уровнями календаря
		CreateCalendarLevels();
		// Создадим таблицу хранения полей объектов
		CreateObjectFields();
	}

	// Энумератор классов объектов метабазы
	public enum ObjectClasses {
		Undefined(0), Folder(1), Dictionary(2), Rubricator(3);

		private final int value;

		private ObjectClasses(final int newValue) {
			value = newValue;
		}

		public int getValue() {
			return value;
		}
	}

	// Энумератор типов полей объектов метабазы
	public enum FieldTypes {
		Regular(0), RubrUnit(1);

		private final int value;

		private FieldTypes(final int newValue) {
			value = newValue;
		}

		public int getValue() {
			return value;
		}
	}

	// Энумератор уровней календаря
	public enum CalendarLevels {
		Day(0), Week(1), Month(2), Quarter(3), Year(4);

		private final int value;

		private CalendarLevels(final int newValue) {
			value = newValue;
		}

		public int getValue() {
			return value;
		}
	}
}
