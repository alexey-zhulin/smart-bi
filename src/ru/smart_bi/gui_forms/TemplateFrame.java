package ru.smart_bi.gui_forms;

import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class TemplateFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	// Начальная инициализация параметров формы
	static void InitSizeValues(TemplateFrame mainForm) {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice();
		int displayWidth = gd.getDisplayMode().getWidth();
		int displayHeight = gd.getDisplayMode().getHeight();
		int width = displayWidth / 3 * 2;
		int height = displayHeight / 3 * 2;
		int left = (displayWidth - width) / 2;
		int top = (displayHeight - height) / 2;
		mainForm.setBounds(left, top, width, height);
	}

	// Загрузка параметров формы из файла
	static boolean LoadFormParameters(TemplateFrame mainForm) {
		try {
			File file = new File(mainForm.getClass().getSimpleName() + ".prop");
			if (!file.exists() || file.isDirectory())
				return false;
			Properties properties = new Properties();
			BufferedReader burrerReader = new BufferedReader(new FileReader(
					file));
			properties.load(burrerReader);
			if (properties.getProperty("xPos") == null
					|| properties.getProperty("yPos") == null
					|| properties.getProperty("width") == null
					|| properties.getProperty("height") == null)
				return false;

			int xPos = Integer.parseInt(properties.getProperty("xPos"));
			int yPos = Integer.parseInt(properties.getProperty("yPos"));
			int width = Integer.parseInt(properties.getProperty("width"));
			int height = Integer.parseInt(properties.getProperty("height"));

			Rectangle rectangle = new Rectangle(xPos, yPos, width, height);
			mainForm.setBounds(rectangle);
			if (properties.getProperty("state") != null) {
				int state = Integer.parseInt(properties.getProperty("state"));
				mainForm.setExtendedState(state);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e);
			return true;
		}
		return true;
	}

	static void SaveWindowParams(TemplateFrame mainForm) throws Exception {
		File file = new File(mainForm.getClass().getSimpleName() + ".prop");
		Properties properties = new Properties();
		properties.setProperty("state", String.valueOf(mainForm.getExtendedState()));
		mainForm.setExtendedState(Frame.NORMAL);
		Rectangle rectangle = mainForm.getBounds();
		Integer xPos = (int) rectangle.getX();
		Integer yPos = (int) rectangle.getY();
		Integer width = (int) rectangle.getWidth();
		Integer height = (int) rectangle.getHeight();

		properties.setProperty("xPos", xPos.toString());
		properties.setProperty("yPos", yPos.toString());
		properties.setProperty("width", width.toString());
		properties.setProperty("height", height.toString());

		BufferedWriter bufferWriter = new BufferedWriter(new FileWriter(file));
		properties.store(bufferWriter, "Saved values for \"" + mainForm.getClass().getName() + "\"");
	}

}
