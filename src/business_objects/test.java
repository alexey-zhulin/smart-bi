package business_objects;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import data_loader_descriptors.LoadParams;
import data_loader_descriptors.LoadStructure;
import data_loader_descriptors.LoadStructure.FieldTypes;
import data_loaders.DictionaryLoaderFromText;
import object_descriptors.DictionaryDescriptor;
import object_descriptors.MetabaseDescriptor;
import object_descriptors.ObjectFieldDescriptor;
import object_descriptors.RubricatorDescriptor;
import object_instances.DictionaryInstance;
import sql_classes.ConnectionHandler;
import sql_classes.FieldHandler;

public class test {

	private static String server;
	private static String port;
	private static String datebase;
	private static String user;
	private static String password;

	// ��������� �������� ������ ��������� 
	static void LoadDictionary(ConnectionHandler connection, DictionaryDescriptor dictionaryDescriptor, String fileName, boolean loadSequenceFields) throws Exception {
		// ���������� ���������
		DictionaryLoaderFromText loaderFromText = new DictionaryLoaderFromText();
		loaderFromText.delimeter = ";";
		loaderFromText.fileName = fileName;
		//loaderFromText.startRow = 2;
		//loaderFromText.endRow = 3;
		// ��������� ��������� ����� ����������
		List<LoadStructure> headers = new ArrayList<LoadStructure>();
		headers.add(LoadStructure.createLoadStructure(FieldTypes.String, dictionaryDescriptor.FieldById("code_1c"), 3));
		headers.add(LoadStructure.createLoadStructure(FieldTypes.Int, dictionaryDescriptor.FieldById("id"), 0));
		headers.add(LoadStructure.createLoadStructure(FieldTypes.String, dictionaryDescriptor.FieldById("name"), 1));
		headers.add(LoadStructure.createLoadStructure(FieldTypes.Int, dictionaryDescriptor.FieldById("parent_id"), 2));
		loaderFromText.headers = headers;
		// ��������� ��������� ��������
		LoadParams loadParams = new LoadParams();
		loadParams.loadSequenceFields = loadSequenceFields;
		loadParams.syncFieldName = "code_1c";
		// �������� instance ������� ��� ��������
		DictionaryInstance dictionaryInstance = new DictionaryInstance(connection);
		dictionaryInstance.dictionaryDescriptor = dictionaryDescriptor;
		dictionaryInstance.LoadData(loaderFromText, loadParams);
	}
	
	static void CreateObjects(ConnectionHandler connection) throws Exception {
		// ���������� "������ ������������"
		DictionaryDescriptor goods_groups = new DictionaryDescriptor(connection);
		goods_groups.object_name = "������ ������������";
		goods_groups.ext_id = "GROUP_OF_GOODS";
		ObjectFieldDescriptor code_1cField = ObjectFieldDescriptor.createField(
				"��� 1�",
				FieldHandler.createField("code_1c", "varchar(50)", true, 0),
				false,
				object_descriptors.MetabaseDescriptor.FieldTypes.Regular, null);
		goods_groups.fields.add(code_1cField);
		goods_groups.CreateDictionary();
		// ������� ������ �� ���� "code_1c"
		ArrayList<ObjectFieldDescriptor> indexFields = new ArrayList<ObjectFieldDescriptor>();
		indexFields.add(code_1cField);
		goods_groups.CreateIndexForFields(indexFields,
				"idx_group_of_goods_code_1c", true);
		// ���������� "������� ���������"
		DictionaryDescriptor measure_units = new DictionaryDescriptor(
				connection);
		measure_units.object_name = "������� ���������";
		measure_units.ext_id = "UNITS";
		code_1cField = ObjectFieldDescriptor.createField(
				"��� 1�",
				FieldHandler.createField("code_1c", "varchar(50)", true, 0),
				false,
				object_descriptors.MetabaseDescriptor.FieldTypes.Regular, null);
		measure_units.fields.add(ObjectFieldDescriptor.createField(
						"��� 1�",
						FieldHandler.createField("code_1c", "varchar(50)",
								true, 0),
						false,
						object_descriptors.MetabaseDescriptor.FieldTypes.Regular,
						null));
		measure_units.CreateDictionary();
		// ������� ������ �� ���� "code_1c"
		indexFields = new ArrayList<ObjectFieldDescriptor>();
		indexFields.add(code_1cField);
		measure_units.CreateIndexForFields(indexFields, "idx_units_code_1c",
				true);
		// �������� ������
		String fileName = "E:\\tmp\\�����1.csv";
		LoadDictionary(connection, measure_units, fileName, false);
		//String fileName = "E:\\tmp\\data_for_units.txt";
		//LoadDictionary(connection, measure_units, fileName, true);
		// ������� ����������� "�������� ������ ������������"
		RubricatorDescriptor rubricator = new RubricatorDescriptor(connection);
		rubricator.object_name = "�������� ������ ������������";
		rubricator.ext_id = "SALE_DINAMICS";
		// ������� ������ �� ������� ������������
		rubricator.fields.add(ObjectFieldDescriptor.createField(
				"������ ������������",
				FieldHandler.createField("group_id", "int", true, 0), false,
				object_descriptors.MetabaseDescriptor.FieldTypes.Regular,
				goods_groups.GetTableName()));
		// ������� ������� ���������
		rubricator.fields.add(ObjectFieldDescriptor.createField(
				"������� ���������",
				FieldHandler.createField("unit_id", "int", true, 0), false,
				object_descriptors.MetabaseDescriptor.FieldTypes.RubrUnit,
				measure_units.GetTableName()));
		rubricator.CreateRubricator(connection);
	}

	public static void main(String[] args) {
		server = "10.0.3.50";
		port = "5432";
		datebase = "testdb";
		user = "postgres";
		password = "postgres";
		Instant t1, t2;
		t1 = Instant.now();
		try {
			ConnectionHandler connection = new ConnectionHandler(server, port,
					datebase, user, password);
			MetabaseDescriptor metabaseHandler = new MetabaseDescriptor();
			// metabaseHandler.DeleteMetabase(connection);
			boolean recreateMetabase = true;
			metabaseHandler.CreateMetabase(connection, recreateMetabase);
			CreateObjects(connection);
			connection.CloseConnection();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		t2 = Instant.now();
		System.out.println("Operation completed..." + Duration.between(t1, t2).toMillis() + " ms");
	}

}
