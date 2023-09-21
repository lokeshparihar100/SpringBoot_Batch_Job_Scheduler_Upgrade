package com.springbatchnew.config.database;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DatabaseConfig {
	
	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		
		return dataSource;
	}
	
	@Bean(name = "mySqlDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.mysql")
	public DataSource mySqlDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		
		return dataSource;
	}
	
	@Bean(name = "postgresqlDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.pg")
	public DataSource postgresqlDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		
		return dataSource;
	}

}
