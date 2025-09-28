package be.ucll.application.config;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.h2.tools.Server;
import org.hibernate.dialect.H2Dialect;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.SharedCacheMode;

@Configuration
@EnableAspectJAutoProxy
@EnableTransactionManagement
public class JpaConfig {

	private final String[] entityPackages = { "be.ucll" };

	@Bean(initMethod = "start", destroyMethod = "stop")
	public Server h2Server() throws SQLException {
		return Server.createTcpServer(
				"-tcp",
				"-tcpAllowOthers",
				"-tcpShutdownForce",
				"-ifNotExists"
		);
	}

	@Bean(initMethod = "setIsolationLevelReadUncommited")
	public H2IsolationLevelInitializerBean h2IsolationLevelInitializerBean(DataSource dataSource) {
		return new H2IsolationLevelInitializerBean(dataSource);
	}

	@Bean
	@DependsOn("h2Server")
	public DataSource dataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUrl("jdbc:h2:tcp://localhost/mem:db;LOCK_MODE=0");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		return dataSource;
	}

	@Bean(name = "jpa.provider.properties")
	public Map<String, ?> jpaProperties() {
		Map<String, Object> properties = new HashMap<>();
		properties.put("hibernate.dialect", H2Dialect.class.getName());
		properties.put("hibernate.query.substitutions", "true 1,false 0");
		properties.put("hibernate.cache.use_second_level_cache", "false");
		properties.put("hibernate.cache.use_query_cache", "false");
		properties.put("hibernate.hbm2ddl.auto", "create");

		properties.put("hibernate.max_fetch_depth", "3");
		properties.put("hibernate.jdbc.batch_size", 200);
		properties.put("hibernate.jdbc.fetch_size", 200);
		properties.put("hibernate.order_inserts", true);
		properties.put("hibernate.order_updates", true);
		properties.put("hibernate.default_batch_fetch_size", 200);
		return properties;
	}

	@Bean
	public FactoryBean<EntityManagerFactory> entityManagerFactory(@Qualifier("jpa.provider.properties") Map<String, ?> jpaProperties, DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setPersistenceUnitName("jpa");
		factory.setJpaPropertyMap(jpaProperties);
		factory.setSharedCacheMode(SharedCacheMode.ENABLE_SELECTIVE);
		factory.setPackagesToScan(entityPackages);
		factory.setDataSource(dataSource);
		factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		return factory;
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory, DataSource dataSource) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		transactionManager.setDataSource(dataSource);
		return transactionManager;
	}
}
