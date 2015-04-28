package ru.smart_bi.object_instances;

import org.springframework.jdbc.core.JdbcTemplate;

public class ObjectInstance {
	JdbcTemplate jdbcTemplate;
	
	public ObjectInstance(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
