package com.springbatchnew.config.job;


import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.TriggerBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.springbatchnew.job.QuartzJob;


@Configuration
public class QuartzConfig {
	
	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	private JobLocator jobLocator;

	@Value("${cron.schedule.one}")
	private String cronScheduleOne;
	  
	@Value("${cron.schedule.two}")
	private String cronScheduleTwo;
	
	@Bean
	public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
	    JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
	    jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
	    return jobRegistryBeanPostProcessor;
	}
	  
	@Bean
	public JobDetail batchJobDetail() {
		
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("jobName", "batchJob");
		jobDataMap.put("jobLauncher", jobLauncher);
		jobDataMap.put("jobLocator", jobLocator);
		
		return JobBuilder.newJob(QuartzJob.class)
				  .withIdentity("batchJob")
				  .withDescription("My Batch Job")
				  .setJobData(jobDataMap)
				  .storeDurably(true)
				  .build();
	}
	
	@Bean
	  public CronTrigger jobOneTrigger() {		// BatchJob
		  
		  CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronScheduleOne);
		  
		  return TriggerBuilder
				  .newTrigger()
				  .forJob(batchJobDetail())
				  .withIdentity("jobOneTrigger")
				  .withSchedule(scheduleBuilder)
				  .build();
	  }
	  
	  @Bean
	  public CronTrigger jobTwoTrigger() {	// BatchJob
		  
		  CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronScheduleTwo);
		  
		  return TriggerBuilder
				  .newTrigger()
				  .forJob(batchJobDetail())
				  .withIdentity("jobTwoTrigger")
				  .withSchedule(scheduleBuilder)
				  .build();
	  }
}
