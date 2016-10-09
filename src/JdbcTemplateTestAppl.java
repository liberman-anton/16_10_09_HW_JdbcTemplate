import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

import tel_ran.databases.jdbs.DatabaseConnection;
import tel_ran.persons.entities.Child;
import tel_ran.persons.entities.Person;

public class JdbcTemplateTestAppl {

	public static void main(String[] args) throws Exception {
		JdbcTemplate<Person> template = new JdbcTemplate<>();
		System.out.println(template.getInsertStatement(new Child(123, 2016, "name1","sun")));
		
		LinkedHashMap<String,Object> map = (LinkedHashMap<String, Object>) template.getMapFromObj((Person)(new Child(123, 2016, "name1","sun")));
		for(Map.Entry<String, Object> entry : map.entrySet()){
				System.out.println(entry);
				System.out.println();
		}
		
//		DatabaseConnection connection = DatabaseConnection.getDatebaseConnection("root", "12345", null, null);
//		Statement statement = connection.getConnection().createStatement();
//		String sql = String.format("SELECT * FROM %s WHERE id=%d", "persons", 7885407);;
//		ResultSet rs = statement.executeQuery(sql);
//		System.out.println(template.getObject(rs));
	}

}
