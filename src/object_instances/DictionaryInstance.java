package object_instances;

import java.sql.*;
import java.util.ArrayList;

import data_loader_descriptors.IDictionaryLoader;
import data_loader_descriptors.LoadParams;
import object_descriptors.DictionaryDescriptor;
import object_descriptors.ObjectFieldDescriptor;
import sql_classes.ConnectionHandler;
import sql_classes.FieldContentHandler;
import sql_classes.TableContentHandler;

public class DictionaryInstance extends ObjectInstance {
	public DictionaryDescriptor dictionaryDescriptor;

	public DictionaryInstance(ConnectionHandler connection) {
		super(connection);
	}

	// Функция возвращает курсор с данными справочника
	public ResultSet GetDictionaryData() throws SQLException {
		String dictTableName = dictionaryDescriptor.GetTableName();
		ArrayList<ObjectFieldDescriptor> fieldsArr = dictionaryDescriptor.fields;
		// Сформируем скрипт, для получения данных объекта
		String queryText = "select ";
		int i;
		for (i = 0; i < fieldsArr.size(); i++) {
			if (i == (fieldsArr.size() - 1)) {
				queryText = queryText + fieldsArr.get(i).fieldHandler + ", ";
			} else {
				queryText = queryText + fieldsArr.get(i).fieldHandler + "\n";
			}
		}
		queryText = queryText + "from " + dictTableName + "\n";
		ArrayList<FieldContentHandler> paramsArr = new ArrayList<FieldContentHandler>();
		ResultSet resultSet = connection.CreateResultSet(queryText, paramsArr);
		return resultSet;
	}

	// Процедура загружает данные в справочник
	public void LoadData(IDictionaryLoader dataLoader, LoadParams loadParams) throws Exception {
		ResultSet resultSet = dataLoader.getData();
		// Перед загрузкой проверим соотвествие структуры даных полученного
		// курсора и полей словаря
		ArrayList<ObjectFieldDescriptor> fieldsArr = dictionaryDescriptor.fields;
		ResultSetMetaData resultMetaData = resultSet.getMetaData();
		if (resultMetaData.getColumnCount() != fieldsArr.size()) {
			throw new Exception(
					"The field count in data source ["+ resultMetaData.getColumnCount() +"] is not equal to field count in dictionary [" + fieldsArr.size() + "]");
		}
		int i;
		for (i = 1; i <= resultMetaData.getColumnCount(); i++) {
			if (resultMetaData.getColumnName(i) != fieldsArr.get(i-1).fieldHandler.fieldName) {
				throw new Exception("The field number [" + i
						+ "] in data source have to have the name ["
						+ fieldsArr.get(i-1).fieldHandler.fieldName + "] instead of [" + resultMetaData.getColumnName(i) + "]");
			}
		}
		// Загрузим данные в словарь
		String tableName = dictionaryDescriptor.GetTableName();
		TableContentHandler tableContent = new TableContentHandler(tableName, connection);
		while (resultSet.next()) {
			ArrayList<FieldContentHandler> fieldsContArr = new ArrayList<FieldContentHandler>();
			ArrayList<FieldContentHandler> keyFieldsContArr = new ArrayList<FieldContentHandler>();
			boolean needToUpdate = false;
			for (i = 1; i <= resultMetaData.getColumnCount(); i++) {
				// При необходимости исключим загрузку автоинкрементного поля (serial)
				if (!loadParams.loadSequenceFields) {
					if (resultMetaData.isAutoIncrement(i)) continue;
				}
				fieldsContArr.add(FieldContentHandler.createFieldContent(fieldsArr.get(i-1).fieldHandler.fieldName, resultSet.getObject(i)));
				// Опираясь на поле синхронизации, проверим добавлять новую запись или обновлять существующую
				if (fieldsArr.get(i-1).fieldHandler.fieldName == loadParams.syncFieldName) {
					keyFieldsContArr.add(FieldContentHandler.createFieldContent(fieldsArr.get(i-1).fieldHandler.fieldName, resultSet.getObject(i)));
				}
			}
			if (needToUpdate) tableContent.UpdateRecord(fieldsContArr, keyFieldsContArr);
			else tableContent.AddRecord(fieldsContArr);
		}
	}
}
