package ru.smart_bi.data_loader_descriptors;

import ru.smart_bi.object_descriptors.ObjectFieldDescriptor;

public class LoadStructure {
	public FieldTypes field_type; // Типизация загружаемых данных
	public ObjectFieldDescriptor linkedField; // Поле объекта, с которым
												// связываем поле загрузчика
	public int positionInSource; // Позиция в источнике

	public static LoadStructure createLoadStructure(FieldTypes field_type,
			ObjectFieldDescriptor linkedField, int positionInSource)
			throws Exception {
		LoadStructure loadStructure = new LoadStructure();
		loadStructure.field_type = field_type;
		loadStructure.linkedField = linkedField;
		loadStructure.positionInSource = positionInSource;
		return loadStructure;
	}

	// Энумератор типов полей структуры загрузки
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
