package ru.smart_bi.data_models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import ru.smart_bi.object_descriptors.MetabaseDescriptor;
import ru.smart_bi.object_descriptors.ObjectDescriptor;
import ru.smart_bi.object_instances.MetabaseInstanse;

public class MetabaseTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -8023072375870988867L;
	private String[] columnNames = { "Name", "ID", "Object type" };
	private Object[][] data;

	public MetabaseTableModel(MetabaseDescriptor metabaseDescriptor,
			ObjectDescriptor parentObjectDescriptor) {
		MetabaseInstanse metabaseInstanse = new MetabaseInstanse(
				metabaseDescriptor);
		List<ObjectDescriptor> objectDescriptorList = metabaseInstanse
				.GetChildrenObjects(parentObjectDescriptor);
		ArrayList<Object[]> objectArr = new ArrayList<Object[]>();
		for (ObjectDescriptor currentObjectDescriptor : objectDescriptorList) {
			Object[] row = new Object[columnNames.length];
			row[0] = currentObjectDescriptor.object_name;
			row[1] = currentObjectDescriptor.ext_id;
			row[2] = currentObjectDescriptor.class_name;
			objectArr.add(row);
		}
		data = new Object[objectArr.size()][columnNames.length];
		for (int i = 0; i < objectArr.size(); i++) {
			data[i] = (Object[]) objectArr.get(i);
		}
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}

    public String getColumnName(int col) {
        return columnNames[col];
    }

}
