package org.csi.yucca.storage.datamanagementapi.util;

import org.csi.yucca.storage.datamanagementapi.model.tenantin.Tenant;
import org.csi.yucca.storage.datamanagementapi.model.tenantout.TenantOut;

import com.google.gson.annotations.Expose;

public class TenantFiller {


	public static TenantOut fillTenant(Tenant tenant) {
		System.out.println("FILL Tenant OBJECT");
		TenantOut tout = new TenantOut();
		
		tout.setIdTenant(tenant.getIdTenant());
		tout.setTenantName(tenant.getNomeTenant());
		tout.setTenantDescription(tenant.getTenantDescription());
		tout.setTenantCode(tenant.getCodiceTenant());
		tout.setMaxDatasetNum(tenant.getMaxDatasetNum());
		tout.setMaxStreamsNum(tenant.getMaxStreamsNum());
		
		tout.setOrganizationCode(tenant.getOrganizationCode());
		tout.setTenantType(tenant.getTenantType());
		tout.setCodDeploymentStatus(tenant.getCodDeploymentStatus());
		tout.setDataAttivazione(tenant.getDataAttivazione());
		tout.setDataDisattivazione(tenant.getDataDisattivazione());
		tout.setNumGiorniAttivo(tenant.getNumGiorniAttivo());
		tout.setIdEcosystem(tenant.getIdEcosystem());
	    tout.setUserName(tenant.getUserName());
	    tout.setUserFirstName(tenant.getUserFirstName());
	    tout.setUserLastName(tenant.getUserLastName());
	    tout.setUserEmail(tenant.getUserEmail());
	    tout.setUserTypeAuth(tenant.getUserTypeAuth());
	    
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
		
		
		//SOLR
		tout.setDataSolrCollectionName(tenant.getDataSolrCollectionName());
		tout.setMeasuresSolrCollectionName(tenant.getMeasuresSolrCollectionName());
		tout.setSocialSolrCollectionName(tenant.getSocialSolrCollectionName());
		tout.setMediaSolrCollectionName(tenant.getMediaSolrCollectionName());

		//PHOENIX
		tout.setMediaPhoenixTableName(tenant.getMediaPhoenixTableName());
		tout.setMediaPhoenixSchemaName(tenant.getMediaPhoenixSchemaName());
		
		tout.setSocialPhoenixTableName(tenant.getSocialPhoenixTableName());
		tout.setSocialPhoenixSchemaName(tenant.getSocialPhoenixSchemaName());
		
		tout.setDataPhoenixTableName(tenant.getDataPhoenixTableName());
		tout.setDataPhoenixSchemaName(tenant.getDataPhoenixSchemaName());
		
		tout.setMeasuresPhoenixTableName(tenant.getMeasuresPhoenixTableName());
		tout.setMeasuresPhoenixSchemaName(tenant.getMeasuresPhoenixSchemaName());
		
		
		
		tout.setMaxOdataResultPerPage(tenant.getMaxOdataResultPerPage());
		tout.setShareInformationType(tenant.getShareInformationType());
 		
		return tout;
	}
	
}
