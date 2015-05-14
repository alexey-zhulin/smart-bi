package ru.smart_bi.gui_forms;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MainMenu extends JMenuBar {

	private static final long serialVersionUID = -2342798007788985149L;

	public MainMenu() {
		super();
		// Create main menu
		// FILE
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		this.add(menu);
		JMenuItem menuItem;
		//Open repository
		menuItem = new JMenuItem("Open repository");
		menuItem.setMnemonic(KeyEvent.VK_O);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// TODO: Create Open repository method
			}

		});
		menu.add(menuItem);
		//Exit
		menuItem = new JMenuItem("Exit");
		menuItem.setMnemonic(KeyEvent.VK_X);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}

		});
		menu.add(menuItem);
		// HELP
		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		this.add(menu);
		// About
		menuItem = new JMenuItem("About");
		menuItem.setMnemonic(KeyEvent.VK_A);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// TODO: Show about window
			}

		});
		menu.add(menuItem);
	}

}
