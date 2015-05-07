package ru.smart_bi.gui;

public class StartGui {

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainForm.ShowForm();
			}
		});
	}

}
