package ru.smart_bi.object_instances;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import ru.smart_bi.object_descriptors.MetabaseDescriptor;
import ru.smart_bi.object_descriptors.ObjectDescriptor;

public class MetabaseInstanse {

	public MetabaseDescriptor metabaseDescriptor;

	public MetabaseInstanse(MetabaseDescriptor metabaseDescriptor) {
		this.metabaseDescriptor = metabaseDescriptor;
	}

	
List<ObjectDescriptor> FillObjectList(ResultSet rs) throws SQLException {
		List<ObjectDescriptor> list = new ArrayList<ObjectDescriptor>();
		while (rs.next()) {
			ObjectDescriptor objectDescriptor = new ObjectDescriptor(
					metabaseDescriptor.getJdbcTemplate());
			objectDescriptor.object_id = rs.getInt("object_id");
			objectDescriptor.parent_object_id = rs.getInt("parent_object_id");
			objectDescriptor.object_name = rs.getString("object_name");
			objectDescriptor.ext_id = rs.getString("ext_id");
			objectDescriptor.f_class_id = rs.getInt("f_class_id");
			objectDescriptor.class_name = rs.getString("class_name");
			list.add(objectDescriptor);
		}
		return list;
	}

	public List<ObjectDescriptor> GetChildrenObjects(
			ObjectDescriptor parentObjectDescriptor) {
		if (parentObjectDescriptor == null) {
			String queryText = "select mo.*, oc.class_name from metabaseobjects mo, objectclasses oc where mo.parent_object_id is null and mo.f_class_id = oc.class_id";
			List<ObjectDescriptor> objectList = metabaseDescriptor
					.getJdbcTemplate().query(queryText,
							new ResultSetExtractor<List<ObjectDescriptor>>() {
								@Override
								public List<ObjectDescriptor> extractData(
										ResultSet rs) throws SQLException,
										DataAccessException {
									List<ObjectDescriptor> list = FillObjectList(rs);
									return list;
								}
							});
			return objectList;
		} else {
			String queryText = "select mo.*, oc.class_name from metabaseobjects mo, objectclasses oc where mo.parent_object_id = ? and mo.f_class_id = oc.class_id";
			List<ObjectDescriptor> objectList = metabaseDescriptor
					.getJdbcTemplate().query(queryText,
							new Object[] { parentObjectDescriptor.object_id },
							new ResultSetExtractor<List<ObjectDescriptor>>() {
								@Override
								public List<ObjectDescriptor> extractData(
										ResultSet rs) throws SQLException,
										DataAccessException {
									List<ObjectDescriptor> list = FillObjectList(rs);
									return list;
								}
							});
			return objectList;
		}
	}

}