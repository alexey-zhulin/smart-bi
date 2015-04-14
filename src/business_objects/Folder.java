package business_objects;

import java.sql.SQLException;

import business_objects.MetabaseHandler.ObjectClasses;
import sql_classes.ConnectionHandler;

public class Folder extends MetabaseObject {
	
	public Folder(ConnectionHandler connection) {
		super(connection);
		f_class_id = ObjectClasses.Dictionary.getValue();
	}
	
	public void CreateFolder() throws SQLException {
		// Запишем базовые параметры объекта в таблицу MetabaseObjects
		CreateObject();
	}
	
}
