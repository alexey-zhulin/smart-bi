package ru.smart_bi.gui;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MainForm extends JFrame {

	private static final long serialVersionUID = 1L;

	// Начальная инициализация параметров формы
	void InitSizeValues() {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice();
		int displayWidth = gd.getDisplayMode().getWidth();
		int displayHeight = gd.getDisplayMode().getHeight();
		int width = displayWidth / 3 * 2;
		int height = displayHeight / 3 * 2;
		int left = (displayWidth - width) / 2;
		int top = (displayHeight - height) / 2;
		setBounds(left, top, width, height);
	}

	// Загрузка параметров формы из файла
	boolean LoadFormParameters() {
		File file = new File("MainFormParams.xml");
		if (!file.exists() || file.isDirectory())
			return false;
		try {
			XMLDecoder decoder = new XMLDecoder(new FileInputStream(file));
			decoder.readObject();
			decoder.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e);
			return true;
		}
		return true;
	}

	public MainForm() {
		if (!LoadFormParameters())
			InitSizeValues();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void ShowForm() {
		final MainForm mainForm = new MainForm();
		mainForm.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
		         try
		         {
		     		File file = new File("MainFormParams.xml");
		            XMLEncoder encoder = new XMLEncoder(new FileOutputStream(file));
		            encoder.writeObject(mainForm);
		            encoder.close();
		         }
		         catch (IOException e1)
		         {
		            JOptionPane.showMessageDialog(null, e1);
		         }
			}
		});
		mainForm.setVisible(true);
	}

}
