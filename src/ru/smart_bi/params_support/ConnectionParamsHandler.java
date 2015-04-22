package ru.smart_bi.params_support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class ConnectionParamsHandler {
	public String server = null;
	public String port = null;
	public String database = null;
	public String user = null;
	public String password = null;
	
	public void LoadParamsFromXml(String paramFileName) throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		File paramFile = new File(paramFileName);
		if (!paramFile.exists() || paramFile.isDirectory()) throw new Error("File [" + paramFileName + "] does not exist");
		Properties properties = new Properties();
		properties.loadFromXML(new FileInputStream(paramFileName));
		server = properties.getProperty("server"); // "10.0.3.50";
		port = properties.getProperty("port"); // "5432";
		database = properties.getProperty("database"); // "testdb";
		user = properties.getProperty("user"); // "postgres";
		password = properties.getProperty("password"); // "postgres";
	}
	
	// Функция определяет из массива параметров значение, которое относится к param_type
	public static String GetConnectionParamValue(String[] args, String param_type) {
		String pathToParams = null;
		for (int i = 0; i < args.length; i++) {
			if (args[i].substring(0, param_type.length()).equals(param_type)) {
				pathToParams = args[i].substring(param_type.length() + 1); // param_type + "="
			}
		}
		return pathToParams;
	}
	
	// Процедура записи параметров в xml файл
	public void SaveParamsToXml(String paramFileName) throws IOException {
		Properties properties = new Properties();
		properties.setProperty("server", server);
		properties.setProperty("port", port);
		properties.setProperty("database", database);
		properties.setProperty("user", user);
		properties.setProperty("password", password);
        FileOutputStream outputStream = new FileOutputStream(paramFileName);
		properties.storeToXML(outputStream, "Connection parameters", "UTF-8");
	}
}
