package com.docops.workflow.domain.model;

public record TenantContext(
	    String tenantId,
	    String orgId
	) {}