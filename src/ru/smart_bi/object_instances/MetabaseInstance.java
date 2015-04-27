package ru.smart_bi.object_instances;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import ru.smart_bi.object_descriptors.MetabaseDescriptor;
import ru.smart_bi.sql_classes.ConnectionHandler;

public class MetabaseInstance {
	private String server;
	private String port;
	private String database;
	private String user;
	private String password;
	private boolean recreateMetabase;
	private ConnectionHandler connection;
	
	public void setServer(String server) {
		this.server = server;
	}
	
	public void setPort(String port) {
		this.port = port;
	}
	
	public void setDatabase(String database) {
		this.database = database;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setRecreateMetabase(boolean recreateMetabase) {
		this.recreateMetabase = recreateMetabase;
	}
	public boolean getRecreateMetabase() {
		return this.recreateMetabase;
	}
	
	public ConnectionHandler getConnection() {
		return connection;
	}
	
	public void closeConnection() throws SQLException {
		Logger log = Logger.getRootLogger();
		log.info("Closing connection...");
		connection.CloseConnection();
	}
	
	public void initMetabase() throws ClassNotFoundException, SQLException {
		Logger log = Logger.getRootLogger();
		log.info("Initializing metabase...");
		connection = new ConnectionHandler(server, port,
				database, user, password);
		MetabaseDescriptor metabaseHandler = new MetabaseDescriptor();
		if (recreateMetabase) metabaseHandler.CreateMetabase(connection, recreateMetabase);
	}
}
