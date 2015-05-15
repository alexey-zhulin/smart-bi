package ru.smart_bi.gui_forms;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;
import ru.smart_bi.object_instances.DictionaryInstance;
import ru.smart_bi.views.DictionaryView;

public class DictionaryForm extends TemplateFrame {
	
	private static final long serialVersionUID = 1595700307570105920L;
	
	public static void ShowForm(DictionaryInstance dictionaryInstance) {
		final DictionaryForm dictionaryForm = new DictionaryForm();

		dictionaryForm.setTitle((String) dictionaryInstance.dictionaryDescriptor.object_name);
		
		DictionaryView dictionaryView = new DictionaryView(dictionaryInstance);
		dictionaryView.setOpaque(true);
		dictionaryForm.setContentPane(dictionaryView);
		dictionaryForm.pack();
		
		dictionaryForm.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				try {
					SaveWindowParams(dictionaryForm);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e);
				}
			}
		});
		if (!LoadFormParameters(dictionaryForm))
			InitSizeValues(dictionaryForm);

		dictionaryForm.setVisible(true);
	}
}
