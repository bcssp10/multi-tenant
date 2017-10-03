package com.example.conf;

import static com.example.conf.CurrentTenantIdentifierResolverImpl.DEFAULT_TENANT;

import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;

public class DataSourceBasedMultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

	private static final long serialVersionUID = 1L;

	@Autowired
	private Map<String,DataSource> mars2DataSources;
	
	@Override
	protected DataSource selectAnyDataSource() {
		return this.mars2DataSources.values().iterator().next();
	}

	@Override
	protected DataSource selectDataSource(String tenantId) {
		return this.mars2DataSources.get(tenantId);
	}

}
