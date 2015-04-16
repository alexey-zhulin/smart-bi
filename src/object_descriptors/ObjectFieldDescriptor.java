package object_descriptors;

import object_descriptors.MetabaseDescriptor.FieldTypes;
import sql_classes.FieldHandler;

public class ObjectFieldDescriptor {
	public String fieldAlias; // ������������ ���� (������-��������)
	public FieldHandler fieldHandler; // ��������� ���� � ����� ������ ��
	public boolean predefined; // ���������������� ���� (�� ����������������)
	public FieldTypes fieldType; // ��� ����
	public String linked_table_name; // ������� � ������� ���� (� ������, ���� ���� �������� ������� �� ������ �������)

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
