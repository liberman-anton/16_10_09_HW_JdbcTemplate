import java.lang.reflect.Field;
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
			} 
		}
		return resMap;
	}

	public String getInsertStatement(T obj) throws Exception{
		StringBuilder res = new StringBuilder("(");
		LinkedHashMap<String,Object> map = (LinkedHashMap<String,Object>) getMap(obj);
		Iterator<String> it = map.keySet().iterator();
		if(it.hasNext()){
			res.append(it.next());
		}
		while(it.hasNext()){	
			res.append(',').append(it.next());
		}
		res.append(") VALUES(");
		
		Iterator<Object> itValues = map.values().iterator();
		if(itValues.hasNext()){
			res.append(getValue(itValues.next()));
		}
		while(itValues.hasNext()){
			res.append(',').append(getValue(itValues.next()));
		}
		res.append(")");
		return res.toString();
	}
	
	private String getValue(Object value) {
		if(value.getClass() == String.class)
			return "'" + value + "'";
		else
			return value.toString();
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getMap(T obj) throws Exception {
		String json = mapper.writeValueAsString(obj);
		Map<String, Object> res = mapper.readValue(json, Map.class);
		res.put(TYPE, obj.getClass().getName());
		return res;
	}
	
	public Map<String, Object> getMapFromObj(Object obj) throws Exception{
		Map<String, Object> resMap = new LinkedHashMap<>(); 
		
		Field[] fields = obj.getClass().getDeclaredFields();
		for(Field field : fields){
			String key = field.getName();
			Object value = obj.getClass().getDeclaredMethod(getGetMethodName(key), parameterTypes);
			resMap.put(key, value);
		}
		
		return resMap;
	}
}
