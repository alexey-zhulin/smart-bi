package business_objects;

import business_objects.MetabaseHandler.FieldTypes;
import sql_classes.FieldHandler;

public class ObjectField {
	String fieldAlias; // Наименование поля (бизнес-название)
	FieldHandler fieldHandler; // Типизация поля с точки зрения БД
	boolean predefined; // Предопределенное поле (не пользовательское)
	FieldTypes fieldType; // Тип поля
	String linked_table_name; // Таблица с данными поля (в случае, если поле является ссылкой на данные таблицы)

	static ObjectField createField(String fieldAlias, FieldHandler fieldHandler, boolean predefined, FieldTypes fieldType, String linked_table_name) {
		ObjectField objectField = new ObjectField();
		objectField.fieldAlias = fieldAlias;
		objectField.fieldHandler = fieldHandler;
		objectField.predefined = predefined;
		objectField.fieldType = fieldType;
		objectField.linked_table_name = linked_table_name;
		return objectField;
	}
}
