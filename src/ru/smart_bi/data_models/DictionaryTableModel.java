package ru.smart_bi.data_models;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import ru.smart_bi.gui_tools.AbstractTreeTableModel;
import ru.smart_bi.gui_tools.TreeTableModel;
import ru.smart_bi.object_descriptors.DictionaryDescriptor;
import ru.smart_bi.object_instances.DictionaryInstance;
import ru.smart_bi.object_instances.DictionaryItem;

public class DictionaryTableModel extends AbstractTreeTableModel implements
		TreeTableModel {

	private String[] columnNames;
	private DictionaryInstance dictionaryInstance;

	//
	// The TreeModel interface
	//

	public int getChildCount(Object parent_object_id) {
		DictionaryItem[] children;
		int childrenCount = 0;
		try {
			children = (DictionaryItem[]) dictionaryInstance.GetDictionaryData(parent_object_id)
					.toArray();
			childrenCount =  (children == null) ? 0 : children.length;
		} catch (SQLException e) {
			Logger log = Logger.getRootLogger();
			log.error(e.getStackTrace());
		}
		return childrenCount;
	}

	public DictionaryItem getChild(Object parent_object_id, int i) {
		DictionaryItem dictionaryItem = null;
		try {
			dictionaryItem = dictionaryInstance.GetDictionaryData(parent_object_id).get(i);
		} catch (SQLException e) {
			Logger log = Logger.getRootLogger();
			log.error(e.getStackTrace());
		}
		return dictionaryItem;
	}

	public DictionaryTableModel(DictionaryInstance dictionaryInstance) {
		this.root = null;
		this.dictionaryInstance = dictionaryInstance;
		DictionaryDescriptor dictionaryDescriptor = dictionaryInstance.dictionaryDescriptor;
		// Define column names base on dictionaryDescriptor
		columnNames = new String[dictionaryDescriptor.fields.size()];
		for (int i = 0; i < dictionaryDescriptor.fields.size(); i++) {
			columnNames[i] = dictionaryDescriptor.fields.get(i).fieldAlias;
		}
	}


	//
	// The TreeTableNode interface
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public Object getValueAt(Object node, int column) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getIndexOfChild(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isLeaf(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
