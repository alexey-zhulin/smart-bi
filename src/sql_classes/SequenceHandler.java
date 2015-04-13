package sql_classes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SequenceHandler {
	String sequenceName;
	ConnectionHandler connectionHandler;

	// ������������� �������
	public SequenceHandler(String sequenceName, ConnectionHandler connectionHandler) {
		this.sequenceName = sequenceName;
		this.connectionHandler = connectionHandler;
	}
	
	// �������� ���������
	public void CreateSequence() throws SQLException {
		String queryText = "CREATE SEQUENCE " + sequenceName;
		connectionHandler.ExecuteCommand(queryText);
	}
	
	// �������� ���������
	public void DropSequence() throws SQLException {
		String queryText = "DROP SEQUENCE IF EXISTS " + sequenceName;
		connectionHandler.ExecuteCommand(queryText);
	}

	// ��������� ����� ��������� ��� ������������ �� ������
	public void SetSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}
	
	// ��������� ���������� �������� �� ���������
	public int GetNextVal() throws SQLException {
		int nextVal = 0;
		String queryText = "SELECT nextval('" + sequenceName + "') as nextval;";
		ArrayList<FieldContentHandler> paramsArr = new ArrayList<FieldContentHandler>();
		ResultSet resultSet = connectionHandler.CreateResultSet(queryText, paramsArr);
		while (resultSet.next()) {
			nextVal = resultSet.getInt("nextval");
		}
		return nextVal;
	}

}
