package ru.smart_bi.object_instances;

import ru.smart_bi.sql_classes.ConnectionHandler;

public class ObjectInstance {
	ConnectionHandler connection;
	
	public ObjectInstance(ConnectionHandler connection) {
		this.connection = connection;
	}

}
