package ru.smart_bi.object_descriptors;

import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;

import ru.smart_bi.object_descriptors.MetabaseDescriptor.ObjectClasses;

public class FolderDescriptor extends ObjectDescriptor {
	
	public FolderDescriptor(JdbcTemplate jdbcTemplate) {
		super(jdbcTemplate);
		f_class_id = ObjectClasses.Dictionary.getValue();
	}
	
	public void CreateFolder() throws SQLException {
		// Запишем базовые параметры объекта в таблицу MetabaseObjects
		CreateObject();
	}
	
}
