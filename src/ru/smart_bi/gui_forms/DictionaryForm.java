package ru.smart_bi.gui_forms;

import javax.swing.JScrollPane;

import ru.smart_bi.data_models.DictionaryTableModel;
import ru.smart_bi.gui_tools.JTreeTable;
import ru.smart_bi.object_instances.DictionaryInstance;

public class DictionaryForm extends TemplateFrame {
	
	private static final long serialVersionUID = 1595700307570105920L;
	private JTreeTable treeTable;
	private DictionaryInstance dictionaryInstance;

	public DictionaryForm(DictionaryInstance dictionaryInstance) {
		this.treeTable = new JTreeTable(new DictionaryTableModel(dictionaryInstance));
		this.dictionaryInstance = dictionaryInstance;
	}
	
	public void ShowForm() {
		setTitle((String) dictionaryInstance.dictionaryDescriptor.object_name);
		getContentPane().add(new JScrollPane(treeTable));
		pack();
		setVisible(true);
	}
}
