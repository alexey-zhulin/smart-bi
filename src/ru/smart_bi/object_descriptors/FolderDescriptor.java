package ru.smart_bi.object_descriptors;

import java.sql.SQLException;

import ru.smart_bi.object_descriptors.MetabaseDescriptor.ObjectClasses;
import ru.smart_bi.sql_classes.ConnectionHandler;

public class FolderDescriptor extends ObjectDescriptor {
	
	public FolderDescriptor(ConnectionHandler connection) {
		super(connection);
		f_class_id = ObjectClasses.Dictionary.getValue();
	}
	
	public void CreateFolder() throws SQLException {
		// Запишем базовые параметры объекта в таблицу MetabaseObjects
		CreateObject();
	}
	
}
