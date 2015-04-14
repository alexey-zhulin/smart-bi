package business_objects;

import business_objects.MetabaseHandler.FieldTypes;
import sql_classes.FieldHandler;

public class ObjectField {
	String fieldAlias; // ������������ ���� (������-��������)
	FieldHandler fieldHandler; // ��������� ���� � ����� ������ ��
	boolean predefined; // ���������������� ���� (�� ����������������)
	FieldTypes fieldType; // ��� ����
	String linked_table_name; // ������� � ������� ���� (� ������, ���� ���� �������� ������� �� ������ �������)

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
