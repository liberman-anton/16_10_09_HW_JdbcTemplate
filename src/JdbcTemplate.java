import java.sql.*;
import java.util.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import tel_ran.persons.entities.Person;

public class JdbcTemplate<T> {
	private final String TYPE = "_class";
	private ObjectMapper mapper = new ObjectMapper();
	
	public JdbcTemplate(){
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public T getObject(ResultSet rs) throws Exception{
		LinkedHashMap<String,Object> map = getMapFromRs(rs);
		return getT(map);
//		return map.toString();
	}
	
	@SuppressWarnings("unchecked")
	private T getT(Map<String, Object> mapJson) throws Exception {
		String json = mapper.writeValueAsString(mapJson);
		return (T) mapper.readValue(json, Person.class);
//				Class.forName((String) mapJson.get(TYPE)));
	}
	
	private LinkedHashMap<String, Object> getMapFromRs(ResultSet rs) throws Exception {
		LinkedHashMap<String,Object> resMap = new LinkedHashMap<>();
		while (rs.next()) {
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				String key = rs.getMetaData().getColumnName(i);
				Object value = rs.getObject(i);
				resMap.put(key, value);
//				System.out.println(key + value);
			} 
		}
		return resMap;
	}

	public String getInsertStatement(T obj) throws Exception{
		String res = "(";
		LinkedHashMap<String,Object> map = (LinkedHashMap<String,Object>) getMap(obj);
		for(String key : map.keySet()){
			res += key + ',';
		}
		res += TYPE + ") VALUES(";
		for(Object value : map.values()){
			res += value.toString() + ',';
		}
		res += obj.getClass().getName() + ")";
		return res;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> getMap(Object obj) throws Exception {
		String json = mapper.writeValueAsString(obj);
		Map<String, Object> res = mapper.readValue(json, Map.class);
		//res.put(TYPE, obj.getClass().getName());
		return res;
	}
}
