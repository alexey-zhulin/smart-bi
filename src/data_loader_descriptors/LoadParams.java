package data_loader_descriptors;

// ����� ��������� ��������� ��� �������� ������
public class LoadParams {
	public boolean loadSequenceFields; // ��������� �������� � ����, ���� ���� ����� ��� serial
	public String syncFieldName; // ��� ���� ��� ������������� ��������� � ����������� ������
	
	public LoadParams() {
		// �������� �� ���������
		this.loadSequenceFields = false;
	}
}
