package com.example.conf;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

import com.example.conf.TenantContext;

public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

	static String DEFAULT_TENANT = "DEFAULT";
	
	@Override
	public String resolveCurrentTenantIdentifier() {
		String currentTenant = TenantContext.getCurrentTenant();
		return currentTenant != null ? currentTenant : DEFAULT_TENANT;
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return true;
	}

}
