package business_objects;

import java.sql.SQLException;

import sql_classes.ConnectionHandler;

public class test {
	
	private static String server;
	private static String port;
	private static String datebase;
	private static String user;
	private static String password;

	static void loadObjects(ConnectionHandler connection) throws SQLException {
		MetabaseObject metabaseObject = new MetabaseObject(connection);
		metabaseObject.object_name = "Dictionary1";
		metabaseObject.ext_id = "OBJ1";
		metabaseObject.f_class_id = 1;
		metabaseObject.CreateObject();
		//
		metabaseObject.object_name = "Dictionary2";
		metabaseObject.ext_id = "OBJ2";
		metabaseObject.f_class_id = 1;
		metabaseObject.CreateObject();
		//
	}
	
	public static void main(String[] args) {
		server = "localhost";
		port = "5432";
		datebase = "testdb";
		user = "postgres";
		password = "123";
		try {
			ConnectionHandler connection = new ConnectionHandler(server, port, datebase, user, password);
			MetabaseHandler metabaseHandler = new MetabaseHandler();
			boolean recreateMetabase = true;
			metabaseHandler.CreateMetabase(connection, recreateMetabase);
			loadObjects(connection);
			connection.CloseConnection();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Operation completed...");
	}

}
