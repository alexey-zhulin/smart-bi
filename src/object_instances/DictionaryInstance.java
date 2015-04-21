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

	// ������� ���������� ������ � ������� �����������
	public ResultSet GetDictionaryData() throws SQLException {
		String dictTableName = dictionaryDescriptor.GetTableName();
		ArrayList<ObjectFieldDescriptor> fieldsArr = dictionaryDescriptor.fields;
		// ���������� ������, ��� ��������� ������ �������
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

	// ��������� ��������� ������ � ����������
	public void LoadData(IDictionaryLoader dataLoader, LoadParams loadParams) throws Exception {
		ResultSet resultSet = dataLoader.getData();
		ResultSetMetaData resultMetaData = resultSet.getMetaData();
		// �������� ������ � �������
		String tableName = dictionaryDescriptor.GetTableName();
		TableContentHandler tableContent = new TableContentHandler(tableName, connection);
		while (resultSet.next()) {
			ArrayList<FieldContentHandler> fieldsContArr = new ArrayList<FieldContentHandler>();
			ArrayList<FieldContentHandler> keyFieldsContArr = new ArrayList<FieldContentHandler>();
			boolean needToUpdate = false;
			int i;
			for (i = 1; i <= resultMetaData.getColumnCount(); i++) {
				// ��� ������������� �������� �������� ����������������� ���� (serial)
				if (!loadParams.loadSequenceFields) {
					if (resultMetaData.isAutoIncrement(i)) continue;
				}
				fieldsContArr.add(FieldContentHandler.createFieldContent(dataLoader.getFieldName(i-1), resultSet.getObject(i)));
				// �������� �� ���� �������������, �������� ��������� ����� ������ ��� ��������� ������������
				if (dataLoader.getFieldName(i-1) == loadParams.syncFieldName) {
					keyFieldsContArr.add(FieldContentHandler.createFieldContent(dataLoader.getFieldName(i-1), resultSet.getObject(i)));
					needToUpdate = tableContent.ValueExists(keyFieldsContArr);
				}
			}
			if (needToUpdate) tableContent.UpdateRecord(fieldsContArr, keyFieldsContArr);
			else tableContent.AddRecord(fieldsContArr);
		}
	}
}
