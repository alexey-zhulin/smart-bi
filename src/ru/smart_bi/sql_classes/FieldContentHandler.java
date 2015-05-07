package ru.smart_bi.sql_classes;

public class FieldContentHandler {
	public String fieldName;
	public Object fieldValue;

	public static FieldContentHandler createFieldContent(String fieldName, Object fieldValue) {
		FieldContentHandler fieldContentHandler = new FieldContentHandler();
		fieldContentHandler.fieldName = fieldName;
		fieldContentHandler.fieldValue = fieldValue;
		return fieldContentHandler;
	}
}
