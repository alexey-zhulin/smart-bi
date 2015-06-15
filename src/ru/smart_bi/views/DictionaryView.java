package ru.smart_bi.views;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import ru.smart_bi.data_models.DictionaryTableModel;
import ru.smart_bi.gui_tools.JTreeTable;
import ru.smart_bi.object_instances.DictionaryInstance;

public class DictionaryView extends JPanel {

	private static final long serialVersionUID = -6524502971704948913L;
	private JTreeTable treeTable;
	
	public DictionaryView(DictionaryInstance dictionaryInstance) {
		super(new GridLayout(1, 0));
		treeTable = new JTreeTable(new DictionaryTableModel(dictionaryInstance));
		// Create the scroll pane and add the tree to it.
		JScrollPane treeView = new JScrollPane(treeTable);

		add(treeView);
	}

}
