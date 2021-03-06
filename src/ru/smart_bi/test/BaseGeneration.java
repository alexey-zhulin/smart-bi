package ru.smart_bi.test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import ru.smart_bi.data_loader_descriptors.LoadParams;
import ru.smart_bi.data_loader_descriptors.LoadStructure;
import ru.smart_bi.data_loader_descriptors.LoadStructure.FieldTypes;
import ru.smart_bi.data_loaders.DictionaryLoaderFromText;
import ru.smart_bi.object_descriptors.DictionaryDescriptor;
import ru.smart_bi.object_descriptors.FolderDescriptor;
import ru.smart_bi.object_descriptors.MetabaseDescriptor;
import ru.smart_bi.object_descriptors.ObjectFieldDescriptor;
import ru.smart_bi.object_descriptors.RubricatorDescriptor;
import ru.smart_bi.object_instances.DictionaryInstance;
import ru.smart_bi.sql_classes.FieldHandler;

public class BaseGeneration {

	// Процедура загрузки единиц измерения
	static void LoadDictionary(JdbcTemplate jdbcTemplate, DictionaryDescriptor dictionaryDescriptor, String fileName,
			boolean loadSequenceFields) throws Exception {
		// Подготовим загрузчик
		DictionaryLoaderFromText loaderFromText = new DictionaryLoaderFromText();
		loaderFromText.delimeter = ";";
		loaderFromText.fileName = fileName;
		// loaderFromText.startRow = 2;
		// loaderFromText.endRow = 3;
		// Определим структуру полей загрузчика
		List<LoadStructure> headers = new ArrayList<LoadStructure>();
		headers.add(LoadStructure.createLoadStructure(FieldTypes.String,
				dictionaryDescriptor.FieldById("code_1c"), 3));
		headers.add(LoadStructure.createLoadStructure(FieldTypes.Int,
				dictionaryDescriptor.FieldById("id"), 0));
		headers.add(LoadStructure.createLoadStructure(FieldTypes.String,
				dictionaryDescriptor.FieldById("name"), 1));
		headers.add(LoadStructure.createLoadStructure(FieldTypes.Int,
				dictionaryDescriptor.FieldById("parent_id"), 2));
		loaderFromText.headers = headers;
		// Определим параметры загрузки
		LoadParams loadParams = new LoadParams();
		loadParams.loadSequenceFields = loadSequenceFields;
		loadParams.syncFieldName = "code_1c";
		// Создадим instance словаря для загрузки
		DictionaryInstance dictionaryInstance = new DictionaryInstance(
				jdbcTemplate);
		dictionaryInstance.dictionaryDescriptor = dictionaryDescriptor;
		dictionaryInstance.LoadData(loaderFromText, loadParams);
	}

	static void CreateObjects(JdbcTemplate jdbcTemplate) throws Exception {
		Logger log = Logger.getRootLogger();
		log.info("Creating objects...");
		// Папка "Bases"
		FolderDescriptor folderBase = new FolderDescriptor(jdbcTemplate);
		folderBase.object_name = "Base";
		folderBase.ext_id = "FOLDER_BASE";
		folderBase.CreateFolder();
		int folderBaseId = (int) folderBase.object_id;
		// Справочник "Группы номенклатуры"
		DictionaryDescriptor goods_groups = new DictionaryDescriptor(jdbcTemplate);
		goods_groups.object_name = "Группы номенклатуры";
		goods_groups.ext_id = "GROUP_OF_GOODS";
		goods_groups.parent_object_id = folderBaseId;
		ObjectFieldDescriptor code_1cField = ObjectFieldDescriptor
				.createField(
						"Код 1С",
						FieldHandler.createField("code_1c", "varchar(50)",
								true, 0),
						false,
						ru.smart_bi.object_descriptors.MetabaseDescriptor.FieldTypes.Regular,
						null);
		goods_groups.fields.add(code_1cField);
		goods_groups.CreateDictionary();
		// Добавим индекс по полю "code_1c"
		ArrayList<ObjectFieldDescriptor> indexFields = new ArrayList<ObjectFieldDescriptor>();
		indexFields.add(code_1cField);
		goods_groups.CreateIndexForFields(indexFields,
				"idx_group_of_goods_code_1c", true);
		// Справочник "Единицы измерения"
		DictionaryDescriptor measure_units = new DictionaryDescriptor(
				jdbcTemplate);
		measure_units.object_name = "Единицы измерения";
		measure_units.ext_id = "UNITS";
		measure_units.parent_object_id = folderBaseId;
		code_1cField = ObjectFieldDescriptor
				.createField(
						"Код 1С",
						FieldHandler.createField("code_1c", "varchar(50)",
								true, 0),
						false,
						ru.smart_bi.object_descriptors.MetabaseDescriptor.FieldTypes.Regular,
						null);
		measure_units.fields
				.add(ObjectFieldDescriptor.createField(
						"Код 1С",
						FieldHandler.createField("code_1c", "varchar(50)",
								true, 0),
						false,
						ru.smart_bi.object_descriptors.MetabaseDescriptor.FieldTypes.Regular,
						null));
		measure_units.CreateDictionary();
		// Добавим индекс по полю "code_1c"
		indexFields = new ArrayList<ObjectFieldDescriptor>();
		indexFields.add(code_1cField);
		measure_units.CreateIndexForFields(indexFields, "idx_units_code_1c",
				true);
		// Загрузим данные
		// String fileName = "E:\\tmp\\Книга1.csv";
		// LoadDictionary(jdbcTemplate, measure_units, fileName, false);
		String fileName = "E:\\tmp\\data_for_units.txt";
		LoadDictionary(jdbcTemplate, measure_units, fileName, true);
		// Каталог показателей "Динамика продаж номенклатуры"
		RubricatorDescriptor rubricator = new RubricatorDescriptor(jdbcTemplate);
		rubricator.object_name = "Динамика продаж номенклатуры";
		rubricator.ext_id = "SALE_DINAMICS";
		rubricator.parent_object_id = folderBaseId;
		// Добавим разрез по группам номенклатуры
		rubricator.fields
				.add(ObjectFieldDescriptor.createField(
						"Группа номенклатуры",
						FieldHandler.createField("group_id", "int", true, 0),
						false,
						ru.smart_bi.object_descriptors.MetabaseDescriptor.FieldTypes.Regular,
						goods_groups.GetTableName()));
		// Добавим единицу измерения
		rubricator.fields
				.add(ObjectFieldDescriptor.createField(
						"Единица измерения",
						FieldHandler.createField("unit_id", "int", true, 0),
						false,
						ru.smart_bi.object_descriptors.MetabaseDescriptor.FieldTypes.RubrUnit,
						measure_units.GetTableName()));
		rubricator.CreateRubricator();
	}

	public static void main(String[] args) {
		Logger log = Logger.getRootLogger();
		Instant start, stop;
		start = Instant.now();
		// Создание метабазы
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext(
					"Beans.xml");
			MetabaseDescriptor metabaseDescriptor = (MetabaseDescriptor) context
					.getBean("metabaseDescriptor");
			JdbcTemplate jdbcTemplate = metabaseDescriptor.getJdbcTemplate();
			if (metabaseDescriptor.getRecreateMetabase()) {
				metabaseDescriptor.CreateMetabase();
				CreateObjects(jdbcTemplate);
			}
			((ConfigurableApplicationContext)context).close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		stop = Instant.now();
		log.info("Operation completed..."
				+ Duration.between(start, stop).toMillis() + " ms");
	}
}
