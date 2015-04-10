package business_objects;

import java.sql.SQLException;

import sql_classes.*;

public class MetabaseHandler {

	// Процедура удаления структуры метабазы
	void DeleteMetabase(ConnectionHandler connection) throws SQLException {
		String tableName = "MetabaseObjects";
		TableHandler tableHandler = new TableHandler(tableName, connection);
		tableHandler.DropTable();
		//
		tableName = "ObjectClasses";
		tableHandler.SetTableName(tableName);
	}

	// Процедура создания таблицы ObjectClasses (классов объектов метабазы)
	void CreateObjectClasses(ConnectionHandler connection) throws SQLException {
		// Создадим саму таблицу
		String tableName = "ObjectClasses";
		TableHandler tableHandler = new TableHandler(tableName, connection);
		FieldHandler[] fieldsArr = new FieldHandler[2];
		fieldsArr[0] = FieldHandler.createField("id", "int", false, 1);
		fieldsArr[1] = FieldHandler.createField("class_name", "text", false, 0);
		tableHandler.CreateTable(fieldsArr);
		// Создадим уникальный индекс по наименованию
		IndexHandler[] indexfieldsArr = new IndexHandler[1];
		indexfieldsArr[0] = IndexHandler.createIndexField("class_name", 0);
		tableHandler.CreateIndex(indexfieldsArr, "idx_class_name", true);
		// Инициализируем значения
	}

	// Процедура создания таблицы MetabaseObjects (объектов метабазы)
	void CreateMetabaseObjects(ConnectionHandler connection) throws SQLException {
		// Создадим саму таблицу
		String tableName = "MetabaseObjects";
		TableHandler tableHandler = new TableHandler(tableName, connection);
		FieldHandler[] fieldsArr = new FieldHandler[2];
		fieldsArr[0] = FieldHandler.createField("id", "int", false, 1);
		fieldsArr[1] = FieldHandler.createField("object_name", "text", false, 0);
		tableHandler.CreateTable(fieldsArr);
	}

	// Процедура инициализации метабазы
	public void CreateMetabase(boolean recreate, String server, String port,
			String datebase, String user, String password)
			throws ClassNotFoundException, SQLException {
		// Создадим подключение
		ConnectionHandler connection = new ConnectionHandler(server, port,
				datebase, user, password);
		if (recreate) {
			// При наличии опции "пересоздания" удалим старые таблицы метабазы
		}
		// Создадим таблицу ObjectClasses
		CreateObjectClasses(connection);
		// Создадим таблицу MetabaseObjects
		CreateMetabaseObjects(connection);
		// Закроем подключение
		connection.CloseConnection();
	}
}
