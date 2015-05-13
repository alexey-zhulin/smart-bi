package ru.smart_bi.gui_forms;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MainMenu extends JMenuBar implements ActionListener {

	private static final long serialVersionUID = -2342798007788985149L;
	private JMenuItem exitMenuItem;

	public MainMenu() {
		super();
		// Create main menu
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		this.add(fileMenu);
		exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(this);
		fileMenu.add(exitMenuItem);
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		System.out.println(actionEvent.getSource());

	}

}
