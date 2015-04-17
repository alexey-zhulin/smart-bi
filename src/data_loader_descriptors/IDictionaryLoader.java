package data_loader_descriptors;

import java.sql.ResultSet;

public interface IDictionaryLoader {

	// Функция получения курсора данных
	public ResultSet getData() throws Exception;
}
