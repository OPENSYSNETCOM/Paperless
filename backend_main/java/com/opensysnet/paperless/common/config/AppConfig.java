package com.opensysnet.paperless.common.config;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.opensysnet.paperless.session.BaseSqlSessionTemplate;
import oracle.jdbc.xa.client.OracleXADataSource;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.web.client.RestTemplate;

import javax.transaction.SystemException;
import java.sql.SQLException;

@Configuration
@EnableTransactionManagement
@PropertySource(value = "classpath:application.properties")
public class AppConfig {

	//RestTemplate
	@Value(value = "${com.opensysnet.common.rest.timeout}")
	private String restTimeOut;

	//oracle default setting
	@Value(value = "${com.opensysnet.common.db.oracle.pool.size}")
	private Integer poolSize;

	@Value(value = "${com.opensysnet.common.db.oracle.borrow.connection.timeout}")
	private Integer borrowConnectionTimeout;

	@Value(value = "${com.opensysnet.common.db.oracle.max.active}")
	private Integer mysqlMaxActive;

	@Value(value = "${com.opensysnet.common.db.oracle.test.query}")
	private String testQuery;

	@Value(value = "${com.opensysnet.common.db.oracle.maintenance.interval}")
	private Integer maintenanceInterval;

	//ORACLE DB
	@Value(value = "${com.opensysnet.common.db.oracle.url}")
	private String urlDB;

	@Value(value = "${com.opensysnet.common.db.oracle.username}")
	private String userNameDB;

	@Value(value = "${com.opensysnet.common.db.oracle.password}")
	private String passwordDB;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean(initMethod = "init", destroyMethod = "close")
	public UserTransactionManager userTransactionManager() {
		UserTransactionManager userTransactionManager = new UserTransactionManager();
		userTransactionManager.setStartupTransactionService(true);
		userTransactionManager.setForceShutdown(false);
		return userTransactionManager;
	}

	@Bean
	public UserTransactionImp userTransactionImp() throws SystemException {
		UserTransactionImp userTransactionImp = new UserTransactionImp();
		userTransactionImp.setTransactionTimeout(300);
		return userTransactionImp;
	}

	/**
	 * JTA Transaction 설정
	 * @return
	 * @throws SystemException
	 */
	@Bean
	public JtaTransactionManager jtaTransactionManager() throws SystemException {
		JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
		jtaTransactionManager.setUserTransaction(userTransactionImp());
		jtaTransactionManager.setTransactionManager(userTransactionManager());
		return jtaTransactionManager;
	}

	//========================= DB 설정 시작 =========================
	@Bean(initMethod = "init", destroyMethod = "close")
	public AtomikosDataSourceBean dataSource() throws SQLException {
		OracleXADataSource oracleXADataSource = new OracleXADataSource();
		oracleXADataSource.setURL(urlDB);
		oracleXADataSource.setUser(userNameDB);
		oracleXADataSource.setPassword(passwordDB);
		// oracleXADataSource.setPinGlobalTxToPhysicalConnection(true);

		AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
		atomikosDataSourceBean.setUniqueResourceName(userNameDB);
		atomikosDataSourceBean.setXaDataSource(oracleXADataSource);
		atomikosDataSourceBean.setPoolSize(poolSize);
		atomikosDataSourceBean.setBorrowConnectionTimeout(borrowConnectionTimeout);
		atomikosDataSourceBean.setTestQuery(testQuery);
		atomikosDataSourceBean.setMaintenanceInterval(maintenanceInterval);

		return atomikosDataSourceBean;
	}

	@Bean
	public SqlSessionFactory sessionFactory() throws SQLException {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource());
		// SQL 인터셉터 설정 - 필요시 주석제거
		// sqlSessionFactoryBean.setPlugins(new Interceptor[]{new SqlInterceptor()});

		PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
		Resource[] resource;
		SqlSessionFactory sqlSessionFactory = null;
		try {
			resource = pathMatchingResourcePatternResolver.getResources("classpath*:**/sqlmapper/datasource/*.xml");
			sqlSessionFactoryBean.setMapperLocations(resource);
			sqlSessionFactory = sqlSessionFactoryBean.getObject();
			sqlSessionFactory.getConfiguration().setJdbcTypeForNull(JdbcType.NULL);
		} catch(Exception e) {
			e.printStackTrace();
		}

		return sqlSessionFactory;
	}

	@Bean(name = "sessionTemplate", destroyMethod = "clearCache")
	public SqlSessionTemplate sessionTemplate() throws SQLException {
		return new BaseSqlSessionTemplate(sessionFactory(), ExecutorType.SIMPLE);
	}
	//========================= DB 설정 종료 =========================

	@Bean
	public HttpClient httpClient() {
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		// 수용 가능한 pool 최대 사이즈
		connectionManager.setMaxTotal(100);
		// host 당 connection pool 생성 가능 connection 수
		connectionManager.setDefaultMaxPerRoute(100);
		return HttpClientBuilder.create().setConnectionManager(connectionManager).build();
	}

	@Bean(name = "restTemplate")
	public RestTemplate restTemplate() {
		// connection pooling 설정
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient());
		// 타임아웃 (ms)
		factory.setReadTimeout(Integer.parseInt(restTimeOut));

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(factory));

		return restTemplate;
	}

}
