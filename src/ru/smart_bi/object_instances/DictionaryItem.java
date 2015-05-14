package ru.smart_bi.object_instances;

import java.util.ArrayList;

import ru.smart_bi.object_descriptors.ObjectDescriptor;
import ru.smart_bi.object_descriptors.ObjectFieldDescriptor;
import ru.smart_bi.sql_classes.FieldContentHandler;

public class DictionaryItem {
	public ArrayList<FieldContentHandler> fields;
	
	public DictionaryItem(ObjectDescriptor objectDescriptor) {
		fields = new ArrayList<FieldContentHandler>();
		for (ObjectFieldDescriptor fieldHandler: objectDescriptor.fields) {
			fields.add(FieldContentHandler.createFieldContent(fieldHandler.fieldAlias, null));
		}
	}
}
