package com.springbatchnew.writer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import com.springbatchnew.model.PersonDetails;
import com.springbatchnew.model.PersonJob;
import com.springbatchnew.model.PersonName;


public class PostgresqlItemWriter<T> implements ItemPreparedStatementSetter<T> {

	@Override
	public void setValues(T targetClass, PreparedStatement ps) throws SQLException {
		
		if (targetClass.equals(PersonName.class)) {
			PersonName personName = (PersonName) targetClass;
			ps.setString(1, personName.getFirstName());
			ps.setString(2, personName.getLastName());
		} else if (targetClass.equals(PersonDetails.class)) {
			PersonDetails personDetails = (PersonDetails) targetClass;
			ps.setString(1, personDetails.getAddress());
			ps.setLong(2, personDetails.getMobileNumber());
		} else if (targetClass.equals(PersonJob.class)) {
			PersonJob personJob = (PersonJob) targetClass;
			ps.setString(1, personJob.getJobTitle());
            ps.setString(2, personJob.getCompany());
            ps.setString(3, personJob.getTeam());
		} else {
			throw new IllegalArgumentException("Invalid target class.");
		}
	}
	

}
