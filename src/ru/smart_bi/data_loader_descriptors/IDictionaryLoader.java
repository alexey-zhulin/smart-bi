package ru.smart_bi.data_loader_descriptors;

import java.sql.ResultSet;

public interface IDictionaryLoader {

	// Функция получения курсора данных
	public ResultSet getData() throws Exception;
	
	// Функция получения наименования поля таблицы справочника
	public String getFieldName(int position) throws Exception;
}
