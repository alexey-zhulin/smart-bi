package sql_classes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SequenceHandler {
	String sequenceName;
	ConnectionHandler connectionHandler;

	// Инициализация объекта
	public SequenceHandler(String sequenceName, ConnectionHandler connectionHandler) {
		this.sequenceName = sequenceName;
		this.connectionHandler = connectionHandler;
	}
	
	// Создание секвенции
	public void CreateSequence() throws SQLException {
		String queryText = "CREATE SEQUENCE " + sequenceName;
		connectionHandler.ExecuteCommand(queryText);
	}
	
	// Удаление секвенции
	public void DropSequence() throws SQLException {
		String queryText = "DROP SEQUENCE IF EXISTS " + sequenceName;
		connectionHandler.ExecuteCommand(queryText);
	}

	// Изменение имени секвенции для переключения на другую
	public void SetSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}
	
	// Получение следующего значения из секвенции
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
