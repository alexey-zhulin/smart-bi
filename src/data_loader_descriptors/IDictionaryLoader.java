package data_loader_descriptors;

import java.sql.ResultSet;

public interface IDictionaryLoader {

	// ������� ��������� ������� ������
	public ResultSet getData() throws Exception;
}
