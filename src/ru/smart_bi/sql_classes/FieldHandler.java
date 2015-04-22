package ru.smart_bi.sql_classes;

// ����� ���� ��� ������������ ������� �������� �������
public class FieldHandler {
	public String fieldName; // ������������ ����
	public String fieldType; // ��� ����
	public boolean isNull;   // null / not null
	public int primaryKeyIndex; // ������� ��������� � ��������� ���� (���� = 0, �� �� ������) 

	public static FieldHandler createField(String fieldName, String fieldType, boolean isNull, int primaryKeyIndex) {
		FieldHandler fieldHandler = new FieldHandler();
		fieldHandler.fieldName = fieldName;
		fieldHandler.fieldType = fieldType;
		fieldHandler.isNull = isNull;
		fieldHandler.primaryKeyIndex = primaryKeyIndex;
		return fieldHandler;
	}
}
