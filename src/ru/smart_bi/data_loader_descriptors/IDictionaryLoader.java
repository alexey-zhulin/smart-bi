package ru.smart_bi.data_loader_descriptors;

import java.sql.ResultSet;

public interface IDictionaryLoader {

	// ������� ��������� ������� ������
	public ResultSet getData() throws Exception;
	
	// ������� ��������� ������������ ���� ������� �����������
	public String getFieldName(int position) throws Exception;
}
