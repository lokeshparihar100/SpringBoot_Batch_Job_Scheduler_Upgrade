package com.springbatchnew.config.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.springbatchnew.batch.JobCompletionListener;
import com.springbatchnew.batch.TruncateTable;

@Configuration
public class BatchConfig {

	@Autowired
	@Qualifier(value = "postgresqlDataSource")
	private DataSource postgresqlDataSource;
	
	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private PlatformTransactionManager transactionManager;
	
	@Autowired
	private PersonNameBatchConfig personNameBatchConfig;
	
	@Autowired
	private PersonDetailsBatchConfig personDetailsBatchConfig;
	
	@Autowired
	private PersonJobBatchConfig personJobBatchConfig;
	
	@Bean
	public Step truncate() {
		
		return new StepBuilder("truncate", jobRepository)
				.tasklet(new TruncateTable(postgresqlDataSource), transactionManager)
				.build();
	}
	
	@Bean
	public JobExecutionListener jobExecutionListener() {
	      return new JobCompletionListener();
	  }
	
	@Bean(name = "batchJob")
	public Job batchJob() {
		
		Flow masterFlow = (Flow) new FlowBuilder("masterFlow").start(truncate()).build();
		  
		  
	    Flow flowJob1 = (Flow) new FlowBuilder("flow1").start(personNameBatchConfig.personNameJobStep()).build();
	    Flow flowJob2 = (Flow) new FlowBuilder("flow2").start(personDetailsBatchConfig.personDetailsJobStep()).build();
	    Flow flowJob3 = (Flow) new FlowBuilder("flow3").start(personJobBatchConfig.personJobJobStep()).build();

	    Flow slaveFlow = (Flow) new FlowBuilder("slaveFlow")
	              .split(new SimpleAsyncTaskExecutor()).add(flowJob1, flowJob2, flowJob3).build(); 
	    
	    return new JobBuilder("batchJob", jobRepository)
	    		.incrementer(new RunIdIncrementer())
	    		.listener(jobExecutionListener())
	    		.start(masterFlow)
	    		.next(slaveFlow)
	    		.build().build();
	}
}
