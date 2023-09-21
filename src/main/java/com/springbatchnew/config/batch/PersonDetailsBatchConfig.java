package com.springbatchnew.config.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.PlatformTransactionManager;

import com.springbatchnew.model.PersonDetails;
import com.springbatchnew.reader.MySqlItemReader;
import com.springbatchnew.writer.PostgresqlItemWriter;

@Configuration
@PropertySource(value = "classpath:sqlqueries.properties")
public class PersonDetailsBatchConfig {

	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private PlatformTransactionManager transactionManager;
	
	@Autowired
	@Qualifier(value = "mySqlDataSource")
	private DataSource mysqlDataSource;
	
	@Autowired
	@Qualifier(value = "postgresqlDataSource")
	private DataSource postgresqlDataSource;
	
	@Value(value = "${read.detail}")
	private String mySqlReadQuery;
	
	@Value(value = "${write.detail}")
	private String postgresqlWriteQuery;
	
	@Bean
	public JdbcCursorItemReader<PersonDetails> personDetailsItemReader() {
		
		JdbcCursorItemReader<PersonDetails> reader = new JdbcCursorItemReader<PersonDetails>();
		reader.setDataSource(mysqlDataSource);
		reader.setSql(mySqlReadQuery);
		reader.setRowMapper(new MySqlItemReader<PersonDetails>());
		return reader;
	}
	
	@Bean
	public JdbcBatchItemWriter<PersonDetails> personDetailsItemWriter() {
		
		JdbcBatchItemWriter<PersonDetails> writer = new JdbcBatchItemWriter<PersonDetails>();
		writer.setDataSource(postgresqlDataSource);
		writer.setSql(postgresqlWriteQuery);
		writer.setItemPreparedStatementSetter(new PostgresqlItemWriter<PersonDetails>());
		return writer;
	}
	
	@Bean
	public Step personDetailsStep(JdbcCursorItemReader<PersonDetails> personDetailsReader, JdbcBatchItemWriter<PersonDetails> personDetailsWriter) {
		
		return new StepBuilder("personDetailsStep", jobRepository)
				.<PersonDetails, PersonDetails>chunk(10, transactionManager)
				.reader(personDetailsReader)
				.writer(personDetailsWriter)
				.build();
	}
	
	@Bean
	public Job personDetailsJob() {
		
		return new JobBuilder("personDetailsJob", jobRepository)
				.start(personDetailsStep(personDetailsItemReader(), personDetailsItemWriter()))
				.build();
	}
	
	@Bean
	public Step personDetailsJobStep() {
		
		return new StepBuilder("personDetailsJobStep", jobRepository)
				.job(personDetailsJob())
				.build();
	}
}
