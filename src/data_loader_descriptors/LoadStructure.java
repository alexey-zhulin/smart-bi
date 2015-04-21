package data_loader_descriptors;

import object_descriptors.ObjectFieldDescriptor;

public class LoadStructure {
	public String header_name;
	public FieldTypes field_type;
	public ObjectFieldDescriptor linkedField; // ���� �������, � �������
												// ��������� ���� ����������
	public int positionInSource; // ������� � ���������

	public static LoadStructure createLoadStructure(String header_name,
			FieldTypes field_type, ObjectFieldDescriptor linkedField,
			int positionInSource) throws Exception {
		if (linkedField == null) {
			throw new Exception(
					"The field ["+ header_name +"] has null value in linkedField");
		}
		LoadStructure loadStructure = new LoadStructure();
		loadStructure.header_name = header_name;
		loadStructure.field_type = field_type;
		loadStructure.linkedField = linkedField;
		loadStructure.positionInSource = positionInSource;
		return loadStructure;
	}

	// ���������� ����� ����� ��������� ��������
	public enum FieldTypes {
		Int(0), Double(1), String(2), Date(3), Boolean(4);

		private final int value;

		private FieldTypes(final int newValue) {
			value = newValue;
		}

		public int getValue() {
			return value;
		}
	}
}
