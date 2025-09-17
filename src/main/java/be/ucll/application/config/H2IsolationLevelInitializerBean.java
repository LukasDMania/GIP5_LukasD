package be.ucll.application.config;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class H2IsolationLevelInitializerBean {

	private JdbcTemplate jdbcTemplate;

	public H2IsolationLevelInitializerBean(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public void setIsolationLevelReadUncommited() {
		jdbcTemplate.execute("SET LOCK_MODE 0");
	}
}
