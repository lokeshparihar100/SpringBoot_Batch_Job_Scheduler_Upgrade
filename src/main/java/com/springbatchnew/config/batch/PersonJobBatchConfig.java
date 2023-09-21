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

import com.springbatchnew.model.PersonJob;
import com.springbatchnew.reader.MySqlItemReader;
import com.springbatchnew.writer.PostgresqlItemWriter;

@Configuration
@PropertySource(value = "classpath:sqlqueries.properties")
public class PersonJobBatchConfig {

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
	
	@Value(value = "${read.job}")
	private String mySqlReadQuery;
	
	@Value(value = "${write.job}")
	private String postgresqlWriteQuery;
	
	@Bean
	public JdbcCursorItemReader<PersonJob> personJobItemReader() {
		
		JdbcCursorItemReader<PersonJob> reader = new JdbcCursorItemReader<PersonJob>();
		reader.setDataSource(mysqlDataSource);
		reader.setSql(mySqlReadQuery);
		reader.setRowMapper(new MySqlItemReader<PersonJob>());
		return reader;
	}
	
	@Bean
	public JdbcBatchItemWriter<PersonJob> personJobItemWriter() {
		
		JdbcBatchItemWriter<PersonJob> writer = new JdbcBatchItemWriter<PersonJob>();
		writer.setDataSource(postgresqlDataSource);
		writer.setSql(postgresqlWriteQuery);
		writer.setItemPreparedStatementSetter(new PostgresqlItemWriter<PersonJob>());
		return writer;
	}
	
	@Bean
	public Step personJobStep(JdbcCursorItemReader<PersonJob> personJobReader, JdbcBatchItemWriter<PersonJob> personJobWriter) {
		
		return new StepBuilder("personJobStep", jobRepository)
				.<PersonJob, PersonJob>chunk(10, transactionManager)
				.reader(personJobReader)
				.writer(personJobWriter)
				.build();
	}
	
	@Bean
	public Job personJobJob() {
		
		return new JobBuilder("personJobJob", jobRepository)
				.start(personJobStep(personJobItemReader(), personJobItemWriter()))
				.build();
	}
	
	@Bean
	public Step personJobJobStep() {
		
		return new StepBuilder("personJobJobStep", jobRepository)
				.job(personJobJob())
				.build();
	}
}
