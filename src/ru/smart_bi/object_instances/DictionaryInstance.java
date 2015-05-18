package ru.smart_bi.object_instances;

import java.sql.*;
import java.util.ArrayList;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import ru.smart_bi.data_loader_descriptors.IDictionaryLoader;
import ru.smart_bi.data_loader_descriptors.LoadParams;
import ru.smart_bi.object_descriptors.DictionaryDescriptor;
import ru.smart_bi.object_descriptors.ObjectDescriptor;
import ru.smart_bi.object_descriptors.ObjectFieldDescriptor;
import ru.smart_bi.sql_classes.FieldContentHandler;
import ru.smart_bi.sql_classes.TableContentHandler;

public class DictionaryInstance extends ObjectInstance {
	public DictionaryDescriptor dictionaryDescriptor;

	public DictionaryInstance(JdbcTemplate jdbcTemplate) {
		super(jdbcTemplate);
	}

	// Функция возвращает курсор с данными справочника
	public ArrayList<DictionaryItem> GetDictionaryData(Object parent_object_id) throws SQLException {
		//ArrayList<DictionaryItem> dictionaryItems = new ArrayList<DictionaryItem>();
		//return dictionaryItems;
		String dictTableName = dictionaryDescriptor.GetTableName();
		ArrayList<ObjectFieldDescriptor> fieldsArr = dictionaryDescriptor.fields;
		// Сформируем скрипт, для получения данных объекта
		String queryText = "select ";
		for (int i = 0; i < fieldsArr.size(); i++) {
			if (i == (fieldsArr.size() - 1)) {
				queryText = queryText + fieldsArr.get(i).fieldHandler.fieldName + "\n";
			} else {
				queryText = queryText + fieldsArr.get(i).fieldHandler.fieldName + ", ";
			}
		}
		queryText = queryText + "from " + dictTableName + "\n";
		if (parent_object_id instanceof ObjectDescriptor)
			queryText = queryText + "where parent_id = " + ((ObjectDescriptor) parent_object_id).object_id;
		else
			queryText = queryText + "where parent_id is null";
			
		ArrayList<DictionaryItem> dictionaryItems = jdbcTemplate.query(queryText,
				new ResultSetExtractor<ArrayList<DictionaryItem>>() {
					@Override
					public ArrayList<DictionaryItem> extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						ArrayList<DictionaryItem> dictionaryItems = new ArrayList<DictionaryItem>();
						// Fill dictionary items from result set
						while (rs.next()) {
							DictionaryItem dictionaryItem = new DictionaryItem(dictionaryDescriptor);
							// fill current dictionary item
							for (int i = 0; i < dictionaryItem.fields.size(); i++) {
								FieldContentHandler currentField = dictionaryItem.fields.get(i);
								currentField.fieldValue = rs.getObject(i+1);
								dictionaryItem.fields.set(i, currentField);
							}
							// add item to result
							dictionaryItems.add(dictionaryItem);
						}
						rs.close();
						return dictionaryItems;
					}
				});
		return dictionaryItems;
	}

	// Процедура загружает данные в справочник
	public void LoadData(IDictionaryLoader dataLoader, LoadParams loadParams)
			throws Exception {
		ResultSet resultSet = dataLoader.getData();
		ResultSetMetaData resultMetaData = resultSet.getMetaData();
		// Загрузим данные в словарь
		String tableName = dictionaryDescriptor.GetTableName();
		TableContentHandler tableContent = new TableContentHandler(tableName,
				jdbcTemplate);
		while (resultSet.next()) {
			ArrayList<FieldContentHandler> fieldsContArr = new ArrayList<FieldContentHandler>();
			ArrayList<FieldContentHandler> keyFieldsContArr = new ArrayList<FieldContentHandler>();
			boolean needToUpdate = false;
			for (int i = 1; i <= resultMetaData.getColumnCount(); i++) {
				// При необходимости исключим загрузку автоинкрементного поля
				// (serial)
				if (!loadParams.loadSequenceFields) {
					if (resultMetaData.isAutoIncrement(i))
						continue;
				}
				fieldsContArr
						.add(FieldContentHandler.createFieldContent(
								dataLoader.getFieldName(i - 1),
								resultSet.getObject(i)));
				// Опираясь на поле синхронизации, проверим добавлять новую
				// запись или обновлять существующую
				if (dataLoader.getFieldName(i - 1) == loadParams.syncFieldName) {
					keyFieldsContArr.add(FieldContentHandler
							.createFieldContent(dataLoader.getFieldName(i - 1),
									resultSet.getObject(i)));
					needToUpdate = tableContent.ValueExists(keyFieldsContArr);
				}
			}
			if (needToUpdate)
				tableContent.UpdateRecord(fieldsContArr, keyFieldsContArr);
			else
				tableContent.AddRecord(fieldsContArr);
		}
	}
}
