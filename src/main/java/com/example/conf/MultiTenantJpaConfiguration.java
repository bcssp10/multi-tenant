package com.example.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.example.Mars2MultitenantApplication;
import com.example.entity.Order;

@Configuration
@EnableConfigurationProperties({ JpaProperties.class })
//@ImportResource(locations = { "classpath:applicationContent.xml" })
@EnableTransactionManagement(proxyTargetClass=true)
@EnableJpaRepositories(basePackages= {"com.example.repository"},transactionManagerRef="transactionManager")
public class MultiTenantJpaConfiguration {

	@Autowired
    private DataSourceProperties properties;
	
	@Autowired
	private JpaProperties jpaProperties;
	
	@Bean
	public MultiTenantConnectionProvider multiTenantConnectionProvider() {
		return new DataSourceBasedMultiTenantConnectionProviderImpl();
	}

	@Bean
	public CurrentTenantIdentifierResolver currentTenantIdentifierResolver() {
		return new CurrentTenantIdentifierResolverImpl();
	}
	
	@Bean(name = "mars2DataSources" )
	public Map<String, DataSource> mars2DataSources() {

		File[] files = Paths.get("tenants").toFile().listFiles();
		Map<String, DataSource> datasources = new HashMap<>();
		
		 for(File propertyFile : files) {
	            Properties tenantProperties = new Properties();
	            DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(this.getClass().getClassLoader());

	            try {
	                tenantProperties.load(new FileInputStream(propertyFile));

	                String tenantId = tenantProperties.getProperty("name");

	                dataSourceBuilder.driverClassName(properties.getDriverClassName())
                    .url(tenantProperties.getProperty("datasource.url"))
                    .username(tenantProperties.getProperty("datasource.username"))
                    .password(tenantProperties.getProperty("datasource.password"));

	                if(properties.getType() != null) {
	                    dataSourceBuilder.type(properties.getType());
	                }

	                datasources.put(tenantId, dataSourceBuilder.build());
	            } catch (IOException e) {
	                e.printStackTrace();
	                return null;
	            }
		 }
		 		 
		 return datasources;
	}
	
	@PersistenceContext @Primary @Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(MultiTenantConnectionProvider multiTenantConnectionProvider,
		CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {

		Map<String, Object> hibernateProps = new LinkedHashMap<>();
		hibernateProps.putAll(this.jpaProperties.getProperties());
		hibernateProps.put(Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
		hibernateProps.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
		hibernateProps.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver);

		// No dataSource is set to resulting entityManagerFactoryBean
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setPackagesToScan(new String[] { Order.class.getPackage().getName() });
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		em.setJpaPropertyMap(hibernateProps);

		return em;
	}
	
	
	@Bean
	public EntityManagerFactory entityManagerFactory(LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
		return entityManagerFactoryBean.getObject();
	}
	

	@Bean(name="transactionManager")
	public PlatformTransactionManager txManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager jpa = new JpaTransactionManager ();
		jpa.setEntityManagerFactory(entityManagerFactory);
		return jpa;
	}
	
	
	private DataSource initialize(DataSource dataSource) {
        ClassPathResource schemaResource = new ClassPathResource("schema.sql");
        ClassPathResource dataResource = new ClassPathResource("data.sql");
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(schemaResource, dataResource);
        populator.execute(dataSource);
        return dataSource;
    }
	
}
