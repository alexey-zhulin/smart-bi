package ru.smart_bi.gui_forms;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ru.smart_bi.object_descriptors.MetabaseDescriptor;
import ru.smart_bi.views.MetabaseView;

public class MainForm extends TemplateFrame {

	private static final String APPLICATION_NAME = "Smart-BI";
	private static final long serialVersionUID = -910607985724295837L;
	private MetabaseDescriptor metabaseDescriptor;

	public MainForm() {
		// Metabase initialization
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext(
					"Beans.xml");
			metabaseDescriptor = (MetabaseDescriptor) context
					.getBean("metabaseDescriptor");
			((ConfigurableApplicationContext) context).close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static void ShowForm() {
		final MainForm mainForm = new MainForm();

		mainForm.setTitle(APPLICATION_NAME + " ("
				+ mainForm.metabaseDescriptor.getRepositoryName() + ")");
		// Create and set up the content pane.
		MetabaseView metabaseView = new MetabaseView(mainForm.metabaseDescriptor);
		metabaseView.setOpaque(true);
		mainForm.setContentPane(metabaseView);
		mainForm.pack();

		mainForm.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				try {
					SaveWindowParams(mainForm);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e);
				}
			}
		});
		if (!LoadFormParameters(mainForm))
			InitSizeValues(mainForm);
		mainForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainForm.setJMenuBar(new MainMenu());

		mainForm.setVisible(true);
	}

}
