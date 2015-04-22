package ru.smart_bi.data_loader_descriptors;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DictionaryLoader implements IDictionaryLoader {
	public List<LoadStructure> headers;
	
	// Процедура сортировки полей
	protected void HeadersSorter() {
		Map<Integer, LoadStructure> headersMap = new TreeMap<Integer, LoadStructure>();
		int i;
		for (i = 0; i < headers.size(); i++) {
			headersMap.put(headers.get(i).positionInSource, headers.get(i));
		}
		headers.clear();
		for (i = 0; i < headersMap.size(); i++) {
			headers.add((LoadStructure) headersMap.values().toArray()[i]);
		}
	}

	@Override
	public ResultSet getData() throws Exception {
		return null;
	}

	@Override
	public String getFieldName(int position) throws Exception {
		if (headers.size() > position) return headers.get(position).linkedField.fieldHandler.fieldName;
		return null;
	}

}
