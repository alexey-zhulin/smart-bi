package ru.smart_bi.sql_classes;


//Класс поля для формирования скрипта создания таблицы
public class IndexHandler {
	public String fieldName; // Наименование поля, входящего в индекс
	public int indexPosition; // порядок вхождения в индекс 

	public static IndexHandler createIndexField(String fieldName, int indexPosition) {
		IndexHandler indexHandler = new IndexHandler();
		indexHandler.fieldName = fieldName;
		indexHandler.indexPosition = indexPosition;
		return indexHandler;
	}
}
