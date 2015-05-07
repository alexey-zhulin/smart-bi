package ru.smart_bi.object_instances;

import java.sql.*;
import java.util.ArrayList;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import ru.smart_bi.data_loader_descriptors.IDictionaryLoader;
import ru.smart_bi.data_loader_descriptors.LoadParams;
import ru.smart_bi.object_descriptors.DictionaryDescriptor;
import ru.smart_bi.object_descriptors.ObjectFieldDescriptor;
import ru.smart_bi.sql_classes.FieldContentHandler;
import ru.smart_bi.sql_classes.TableContentHandler;

public class DictionaryInstance extends ObjectInstance {
	public DictionaryDescriptor dictionaryDescriptor;

	public DictionaryInstance(JdbcTemplate jdbcTemplate) {
		super(jdbcTemplate);
	}

	// Функция возвращает курсор с данными справочника
	public ResultSet GetDictionaryData() throws SQLException {
		String dictTableName = dictionaryDescriptor.GetTableName();
		ArrayList<ObjectFieldDescriptor> fieldsArr = dictionaryDescriptor.fields;
		// Сформируем скрипт, для получения данных объекта
		String queryText = "select ";
		for (int i = 0; i < fieldsArr.size(); i++) {
			if (i == (fieldsArr.size() - 1)) {
				queryText = queryText + fieldsArr.get(i).fieldHandler + ", ";
			} else {
				queryText = queryText + fieldsArr.get(i).fieldHandler + "\n";
			}
		}
		queryText = queryText + "from " + dictTableName + "\n";
		ResultSet resultSet = jdbcTemplate.query(queryText,
				new ResultSetExtractor<ResultSet>() {
					@Override
					public ResultSet extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						return rs;
					}
				});
		return resultSet;
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
