package org.openmrs.module.customrestservices.api.util;

import org.openmrs.module.openhmis.commons.api.entity.security.IObjectAuthorizationPrivileges;
import org.openmrs.util.PrivilegeConstants;

public class BasicObjectAuthorizationPrivileges implements IObjectAuthorizationPrivileges {
	@Override
	public String getSavePrivilege() {
		return PrivilegeConstants.MANAGE_CONCEPTS;
	}

	@Override
	public String getPurgePrivilege() {
		return PrivilegeConstants.PURGE_CONCEPTS;
	}

	@Override
	public String getGetPrivilege() {
		return PrivilegeConstants.GET_CONCEPTS;
	}
}
