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

import com.springbatchnew.model.PersonName;
import com.springbatchnew.reader.MySqlItemReader;
import com.springbatchnew.writer.PostgresqlItemWriter;

@Configuration
@PropertySource(value = "classpath:sqlqueries.properties")
public class PersonNameBatchConfig {

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
	
	@Value(value = "${read.name}")
	private String mySqlReadQuery;
	
	@Value(value = "${write.name}")
	private String postgresqlWriteQuery;
	
	@Bean
	public JdbcCursorItemReader<PersonName> personNameItemReader() {
		
		JdbcCursorItemReader<PersonName> reader = new JdbcCursorItemReader<PersonName>();
		reader.setDataSource(mysqlDataSource);
		reader.setSql(mySqlReadQuery);
		reader.setRowMapper(new MySqlItemReader<PersonName>());
		return reader;
	}
	
	@Bean
	public JdbcBatchItemWriter<PersonName> personNameItemWriter() {
		
		JdbcBatchItemWriter<PersonName> writer = new JdbcBatchItemWriter<PersonName>();
		writer.setDataSource(postgresqlDataSource);
		writer.setSql(postgresqlWriteQuery);
		writer.setItemPreparedStatementSetter(new PostgresqlItemWriter<PersonName>());
		return writer;
	}
	
	@Bean
	public Step personNameStep(JdbcCursorItemReader<PersonName> personNameReader, JdbcBatchItemWriter<PersonName> personNameWriter) {
		
		return new StepBuilder("personNameStep", jobRepository)
				.<PersonName, PersonName>chunk(10, transactionManager)
				.reader(personNameReader)
				.writer(personNameWriter)
				.build();
	}
	
	@Bean
	public Job personNameJob() {
		
		return new JobBuilder("personNameJob", jobRepository)
				.start(personNameStep(personNameItemReader(), personNameItemWriter()))
				.build();
	}
	
	@Bean
	public Step personNameJobStep() {
		
		return new StepBuilder("personNameJobStep", jobRepository)
				.job(personNameJob())
				.build();
	}
}
