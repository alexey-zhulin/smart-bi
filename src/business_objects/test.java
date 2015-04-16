package business_objects;

import java.sql.SQLException;

import object_descriptors.DictionaryDescriptor;
import object_descriptors.MetabaseDescriptor;
import object_descriptors.ObjectFieldDescriptor;
import object_descriptors.RubricatorDescriptor;
import sql_classes.ConnectionHandler;
import sql_classes.FieldHandler;

public class test {

	private static String server;
	private static String port;
	private static String datebase;
	private static String user;
	private static String password;

	static void loadObjects(ConnectionHandler connection) throws SQLException {
		// ���������� "������ ������������"
		DictionaryDescriptor goods_groups = new DictionaryDescriptor(connection);
		goods_groups.object_name = "������ ������������";
		goods_groups.ext_id = "GROUP_OF_GOODS";
		goods_groups.fields.add(ObjectFieldDescriptor.createField("��� 1�",
				FieldHandler.createField("code_1c", "varchar(50)", true, 0),
				false, object_descriptors.MetabaseDescriptor.FieldTypes.Regular,
				null));
		goods_groups.CreateDictionary();
		// ���������� "������� ���������"
		DictionaryDescriptor measure_units = new DictionaryDescriptor(connection);
		measure_units.object_name = "������� ���������";
		measure_units.ext_id = "UNITS";
		measure_units.fields.add(ObjectFieldDescriptor.createField("��� 1�",
				FieldHandler.createField("code_1c", "varchar(50)", true, 0),
				false, object_descriptors.MetabaseDescriptor.FieldTypes.Regular,
				null));
		measure_units.CreateDictionary();
		// ������� ����������� "�������� ������ ������������"
		RubricatorDescriptor rubricator = new RubricatorDescriptor(connection);
		rubricator.object_name = "�������� ������ ������������";
		rubricator.ext_id = "SALE_DINAMICS";
		// ������� ������ �� ������� ������������
		rubricator.fields.add(ObjectFieldDescriptor.createField("������ ������������",
				FieldHandler.createField("group_id", "int", true, 0), false,
				object_descriptors.MetabaseDescriptor.FieldTypes.Regular, goods_groups.GetTableName()));
		// ������� ������� ���������
		rubricator.fields.add(ObjectFieldDescriptor.createField("������� ���������",
				FieldHandler.createField("unit_id", "int", true, 0), false,
				object_descriptors.MetabaseDescriptor.FieldTypes.RubrUnit, measure_units.GetTableName()));
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
			MetabaseDescriptor metabaseHandler = new MetabaseDescriptor();
			//metabaseHandler.DeleteMetabase(connection);
			boolean recreateMetabase = true;
			metabaseHandler.CreateMetabase(connection, recreateMetabase);
			loadObjects(connection);
			connection.CloseConnection();
		} catch (Exception e) {
			//System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("Operation completed...");
	}

}
