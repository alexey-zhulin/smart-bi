package ru.smart_bi.sql_classes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

public class SequenceHandler {
	String sequenceName;
	JdbcTemplate jdbcTemplate;

	// Инициализация объекта
	public SequenceHandler(String sequenceName, JdbcTemplate jdbcTemplate) {
		this.sequenceName = sequenceName;
		this.jdbcTemplate = jdbcTemplate;
	}
	
	// Создание секвенции
	public void CreateSequence() throws SQLException {
		String queryText = "CREATE SEQUENCE " + sequenceName;
		jdbcTemplate.execute(queryText);
	}
	
	// Удаление секвенции
	public void DropSequence() throws SQLException {
		String queryText = "DROP SEQUENCE IF EXISTS " + sequenceName;
		jdbcTemplate.execute(queryText);
	}

	// Изменение имени секвенции для переключения на другую
	public void SetSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}
	
	// Получение следующего значения из секвенции
	public int GetNextVal() throws SQLException {
		int nextVal = 0;
		String queryText = "SELECT nextval('" + sequenceName + "') as nextval;";
		List<Integer> resultSet = jdbcTemplate.query(queryText,
				new ResultSetExtractor<List<Integer>>() {
					@Override
					public List<Integer> extractData(ResultSet rs)
							throws SQLException, DataAccessException {

						List<Integer> list = new ArrayList<Integer>();
						while (rs.next()) {
							list.add(rs.getInt("nextval"));
						}
						return list;
					}
				});
		for (Integer curVal: resultSet) {
			nextVal = curVal;
		}
		return nextVal;
	}

}
