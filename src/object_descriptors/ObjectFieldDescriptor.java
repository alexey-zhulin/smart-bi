package object_descriptors;

import object_descriptors.MetabaseDescriptor.FieldTypes;
import sql_classes.FieldHandler;

public class ObjectFieldDescriptor {
	public String fieldAlias; // Наименование поля (бизнес-название)
	public FieldHandler fieldHandler; // Типизация поля с точки зрения БД
	public boolean predefined; // Предопределенное поле (не пользовательское)
	public FieldTypes fieldType; // Тип поля
	public String linked_table_name; // Таблица с данными поля (в случае, если поле является ссылкой на данные таблицы)

	public static ObjectFieldDescriptor createField(String fieldAlias, FieldHandler fieldHandler, boolean predefined, FieldTypes fieldType, String linked_table_name) {
		ObjectFieldDescriptor objectField = new ObjectFieldDescriptor();
		objectField.fieldAlias = fieldAlias;
		objectField.fieldHandler = fieldHandler;
		objectField.predefined = predefined;
		objectField.fieldType = fieldType;
		objectField.linked_table_name = linked_table_name;
		return objectField;
	}
}
