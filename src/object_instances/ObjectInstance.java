package object_instances;

import sql_classes.ConnectionHandler;

public class ObjectInstance {
	ConnectionHandler connection;
	
	public ObjectInstance(ConnectionHandler connection) {
		this.connection = connection;
	}

}
