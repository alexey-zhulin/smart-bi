package business_objects;

public class TableHandler {
	String tableName;
	ConnectionHandler connectionHandler;
	
	// ������������� �������
	public TableHandler(String tableName, ConnectionHandler connectionHandler) {
		this.tableName = tableName;
		this.connectionHandler = connectionHandler;
	}
}
