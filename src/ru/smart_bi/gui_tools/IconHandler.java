package ru.smart_bi.gui_tools;

import java.net.URL;

import javax.swing.ImageIcon;

public class IconHandler {
	public ImageIcon createImageIcon(String path, String description) {
		URL imgURL = getClass().getResource(path);
		return new ImageIcon(imgURL, description);
	}
}
