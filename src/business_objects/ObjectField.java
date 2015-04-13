package business_objects;

import sql_classes.FieldHandler;

public class ObjectField {
	String fieldAlias;
	FieldHandler fieldHandler; 
	boolean predefined;

	static ObjectField createField(String fieldAlias, FieldHandler fieldHandler, boolean predefined) {
		ObjectField objectField = new ObjectField();
		objectField.fieldAlias = fieldAlias;
		objectField.fieldHandler = fieldHandler;
		objectField.predefined = predefined;
		return objectField;
	}

	public static ObjectField createField(String fieldAlias, FieldHandler fieldHandler) {
		return createField(fieldAlias, fieldHandler, false);
	}
}
