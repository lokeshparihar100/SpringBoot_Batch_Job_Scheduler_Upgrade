package com.springbatchnew.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;

public class TruncateTable implements Tasklet {
	
	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public TruncateTable(DataSource postgresqlDataSource) {
		this.dataSource = postgresqlDataSource;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
		JdbcTemplate stmt = new JdbcTemplate(getDataSource());
		stmt.execute("truncate Name");
	    stmt.execute("truncate Details");
	    stmt.execute("truncate Job");
	
	    return RepeatStatus.FINISHED;
	}

}
