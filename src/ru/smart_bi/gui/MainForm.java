package ru.smart_bi.gui;

import java.util.prefs.Preferences;

import javax.swing.JFrame;

public class MainForm extends JFrame {

	private static final int DEFAULT_WIDTH = 300;
	private static final int DEFAULT_HEIGHT = 200;
	private static final long serialVersionUID = 1L;

	public MainForm() {
		Preferences root = Preferences.userRoot();
		final Preferences node = root.node("/ru/smart-bi/mainForm");
		int left = node.getInt("left", 0);
		int top = node.getInt("top", 0);
		int width = node.getInt("width", DEFAULT_WIDTH);
		int height = node.getInt("height", DEFAULT_HEIGHT);
		setBounds(left, top, width, height);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
