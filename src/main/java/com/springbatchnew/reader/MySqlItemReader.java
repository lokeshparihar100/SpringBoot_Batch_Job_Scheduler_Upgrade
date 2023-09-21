package com.springbatchnew.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.springbatchnew.model.PersonDetails;
import com.springbatchnew.model.PersonJob;
import com.springbatchnew.model.PersonName;

public class MySqlItemReader<T>  implements RowMapper<T>{

	private Class<T> targetClass;
	
	public void MySQLItemReader(Class<T> targetClass) {
		this.targetClass = targetClass;
	}
	
	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		T mappedObject;
		try {
			mappedObject = targetClass.getDeclaredConstructor().newInstance();
			if(targetClass.equals(PersonName.class)) {
				PersonName personName = (PersonName) mappedObject;
				personName.setFirstName(rs.getString("first_name"));
				personName.setLastName(rs.getString("last_name"));
			} 
			else if(targetClass.equals(PersonDetails.class)) {
				PersonDetails personDetails = (PersonDetails) mappedObject;
				personDetails.setAddress(rs.getString("address"));
				personDetails.setMobileNumber(rs.getLong("mobile_number"));
			} 
			else if(targetClass.equals(PersonJob.class)) {
				PersonJob personJob = (PersonJob) mappedObject;
				personJob.setJobTitle(rs.getString("job_title"));
				personJob.setCompany(rs.getString("company"));
				personJob.setTeam(rs.getString("team"));
			} else {
				throw new IllegalArgumentException("Invalid target class.");
			}
			
		} catch (Exception e) {
			throw new RuntimeException("Error mapping object.", e);
		}
		return null;
	}
	
	

}
