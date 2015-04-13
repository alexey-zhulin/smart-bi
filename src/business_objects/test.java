package business_objects;

import java.sql.SQLException;

import sql_classes.ConnectionHandler;
import sql_classes.FieldHandler;

public class test {
	
	private static String server;
	private static String port;
	private static String datebase;
	private static String user;
	private static String password;

	static void loadObjects(ConnectionHandler connection) throws SQLException {
		Dictionary dictionary = new Dictionary(connection);
		dictionary.object_name = "Группы номенклатуры";
		dictionary.ext_id = "GROUP_OF_GOODS";
		dictionary.fields.add(ObjectField.createField("Код 1С", FieldHandler.createField("code_1c", "varchar(50)", true, 0)));
		dictionary.CreateDictionary();
	}
	
	public static void main(String[] args) {
		server = "10.0.3.50";
		port = "5432";
		datebase = "testdb";
		user = "postgres";
		password = "postgres";
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
