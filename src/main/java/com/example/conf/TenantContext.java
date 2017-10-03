package com.example.conf;

public class TenantContext {

	private static ThreadLocal<String> currentTenant = new ThreadLocal<>();

	public static void setCurrentTenant(String tenantId) {
		currentTenant.set(tenantId);
	}

	public static String getCurrentTenant() {
		return currentTenant.get();
	}

	public static void clear() {
    	currentTenant.remove();
    }
}
