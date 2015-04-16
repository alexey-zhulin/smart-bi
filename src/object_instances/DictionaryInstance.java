package object_instances;

import java.sql.*;
import java.util.ArrayList;

import object_descriptors.DictionaryDescriptor;
import object_descriptors.ObjectFieldDescriptor;
import sql_classes.ConnectionHandler;
import sql_classes.FieldContentHandler;

public class DictionaryInstance extends ObjectInstance {
	public DictionaryDescriptor dictionaryDescriptor;
	
	public DictionaryInstance(ConnectionHandler connection) {
		super(connection);
	}

	public ResultSet GetDictionaryData() throws SQLException {
		String dictTableName = dictionaryDescriptor.GetTableName();
		ArrayList<ObjectFieldDescriptor> fieldsArr = dictionaryDescriptor.fields;
		// Сформируем скрипт, для получения данных объекта
		String queryText = "select ";
		int i;
		for (i=0; i < fieldsArr.size(); i++) {
			if (i == (fieldsArr.size() - 1)) {
				queryText = queryText + fieldsArr.get(i).fieldHandler + ", ";
			}
			else {
				queryText = queryText + fieldsArr.get(i).fieldHandler + "\n";
			}
		}
		queryText = queryText + "from " + dictTableName + "\n";
		ArrayList<FieldContentHandler> paramsArr = new ArrayList<FieldContentHandler>();
		ResultSet resultSet = connection.CreateResultSet(queryText, paramsArr);
		return resultSet;
	}
}
