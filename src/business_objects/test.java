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
		// Справочник "Группы номенклатуры"
		Dictionary goods_groups = new Dictionary(connection);
		goods_groups.object_name = "Группы номенклатуры";
		goods_groups.ext_id = "GROUP_OF_GOODS";
		goods_groups.fields.add(ObjectField.createField("Код 1С",
				FieldHandler.createField("code_1c", "varchar(50)", true, 0),
				false, business_objects.MetabaseHandler.FieldTypes.Regular,
				null));
		goods_groups.CreateDictionary();
		// Справочник "Единицы измерения"
		Dictionary measure_units = new Dictionary(connection);
		measure_units.object_name = "Единицы измерения";
		measure_units.ext_id = "UNITS";
		measure_units.fields.add(ObjectField.createField("Код 1С",
				FieldHandler.createField("code_1c", "varchar(50)", true, 0),
				false, business_objects.MetabaseHandler.FieldTypes.Regular,
				null));
		measure_units.CreateDictionary();
		// Каталог показателей "Динамика продаж номенклатуры"
		Rubricator rubricator = new Rubricator(connection);
		rubricator.object_name = "Динамика продаж номенклатуры";
		rubricator.ext_id = "SALE_DINAMICS";
		// Добавим разрез по группам номенклатуры
		rubricator.fields.add(ObjectField.createField("Группа номенклатуры",
				FieldHandler.createField("group_id", "int", true, 0), false,
				business_objects.MetabaseHandler.FieldTypes.Regular, goods_groups.GetTableName()));
		// Добавим единицу измерения
		rubricator.fields.add(ObjectField.createField("Единица измерения",
				FieldHandler.createField("unit_id", "int", true, 0), false,
				business_objects.MetabaseHandler.FieldTypes.RubrUnit, measure_units.GetTableName()));
		rubricator.CreateRubricator(connection);
	}

	public static void main(String[] args) {
		server = "10.0.3.50";
		port = "5432";
		datebase = "testdb";
		user = "postgres";
		password = "postgres";
		try {
			ConnectionHandler connection = new ConnectionHandler(server, port,
					datebase, user, password);
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
