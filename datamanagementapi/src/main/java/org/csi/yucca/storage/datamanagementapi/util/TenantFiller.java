package org.csi.yucca.storage.datamanagementapi.util;

import org.csi.yucca.storage.datamanagementapi.model.tenantin.Tenant;
import org.csi.yucca.storage.datamanagementapi.model.tenantout.TenantOut;

public class TenantFiller {


	public static TenantOut fillTenant(Tenant tenant) {
		System.out.println("FILL Tenant OBJECT");
		TenantOut tout = new TenantOut();
		
		tout.setIdTenant(tenant.getIdTenant());
		tout.setTenantName(tenant.getNomeTenant());
		tout.setTenantDescription(tenant.getTenantDescription());
		tout.setTenantCode(tenant.getCodiceTenant());
		
		
		//dati di  default o aggregati
		
		
		String DBTenant = "DB_"+tenant.getCodiceTenant();
		
		tout.setDataCollectionName("data");
		tout.setDataCollectionDb(DBTenant);
		tout.setMeasuresCollectionName("measures");
		tout.setMeasuresCollectionDb(DBTenant);
		tout.setSocialCollectionName("social");
		tout.setSocialCollectionDb(DBTenant);
		tout.setMediaCollectionName("media");
		tout.setMediaCollectionDb(DBTenant);
		tout.setArchiveDataCollectionDb(DBTenant);
		tout.setArchiveMeasuresCollectionDb(DBTenant);
		tout.setArchiveDataCollectionName("archivedata");
		tout.setArchiveMeasuresCollectionName("archivemeasures");
		
		return tout;
	}
	
}
