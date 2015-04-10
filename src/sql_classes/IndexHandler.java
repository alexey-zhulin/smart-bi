package sql_classes;


//����� ���� ��� ������������ ������� �������� �������
public class IndexHandler {
	public String fieldName; // ������������ ����, ��������� � ������
	public int indexPosition; // ������� ��������� � ������ 

	public static IndexHandler createIndexField(String fieldName, int indexPosition) {
		IndexHandler indexHandler = new IndexHandler();
		indexHandler.fieldName = fieldName;
		indexHandler.indexPosition = indexPosition;
		return indexHandler;
	}
}
