package ru.smart_bi.sql_classes;

// Класс поля для формирования скрипта создания таблицы
public class FieldHandler {
	public String fieldName; // Наименование поля
	public String fieldType; // Тип поля
	public boolean isNull;   // null / not null
	public int primaryKeyIndex; // порядок вхождения в первичный ключ (если = 0, то не входит) 

	public static FieldHandler createField(String fieldName, String fieldType, boolean isNull, int primaryKeyIndex) {
		FieldHandler fieldHandler = new FieldHandler();
		fieldHandler.fieldName = fieldName;
		fieldHandler.fieldType = fieldType;
		fieldHandler.isNull = isNull;
		fieldHandler.primaryKeyIndex = primaryKeyIndex;
		return fieldHandler;
	}
}
