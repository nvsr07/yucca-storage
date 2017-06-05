package org.csi.yucca.storage.datamanagementapi.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.csi.yucca.storage.datamanagementapi.model.metadata.ConfigData;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Field;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Info;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Opendata;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Tag;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Tenantsharing;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Tenantssharing;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Componenti;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Element;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Stream;

public class MetadataFiller {

	static public Metadata fillMetadata(Stream stream) {

		System.out.println("FILL METADATA OBJECT");

		Metadata myMeta = new Metadata();

		// String datasetCode = "ds_"+stream.getCodiceStream()+"_"; calculation
		// on insert
		// myMeta.setDatasetCode(datasetCode);
		myMeta.setDatasetVersion(stream.getDeploymentVersion());
		myMeta.setIdDataset(stream.getIdDataset());
		ConfigData cf = new ConfigData();
		cf.setCurrent(1);
		cf.setType("dataset");
		if (stream.getIdTipoVe() == Constants.VIRTUAL_ENTITY_TWITTER_TYPE_ID) {
			cf.setSubtype(Metadata.CONFIG_DATA_SUBTYPE_SOCIAL_DATASET);
			cf.setCollection(Metadata.CONFIG_DATA_DEFAULT_COLLECTION_SOCIAL);
		} else
			cf.setSubtype(Metadata.CONFIG_DATA_SUBTYPE_STREAM_DATASET);
		// cf.setEntityNameSpace("it.csi.smartdata.odata."+stream.getCodiceTenant()+".");
		cf.setDatasetStatus(stream.getDeploymentStatusCode());
		cf.setIdTenant(stream.getIdTenant());
		cf.setTenantCode(stream.getCodiceTenant());
		
		cf.setOrganizationCode(stream.getOrganizationCode());
		cf.setOrganizationDescription(stream.getOrganizationDescription());
		cf.setTenantName(stream.getTenantName());
		cf.setTenantDescription(stream.getTenantDescription());

		myMeta.setConfigData(cf);

		Info info = new Info();
		info.setCopyright(stream.getCopyright());
		info.setDataDomain(stream.getDomainStream());
		info.setCodSubDomain(stream.getCodSubDomain());
		info.setDatasetName(stream.getCodiceStream());
		info.setDescription("Dataset " + stream.getNomeStream());
		info.setDisclaimer(stream.getDisclaimer());
		info.setIcon(stream.getStreamIcon());

		info.setFps(stream.getFps());
		info.setLicense(stream.getLicence());

		Date date = null;
		if (stream.getRegistrationDate() != null) {
			try {
				date = new SimpleDateFormat(Constants.DATE_FORMAT).parse(stream.getRegistrationDate());
			} catch (ParseException e) {
				date = new Date();
			}
		} else {
			date = new Date();
		}
		info.setRegistrationDate(date);
		info.setRequestornEmail(stream.getMailRichiedente());
		info.setRequestorName(stream.getNomeRichiedente());
		info.setRequestorSurname(stream.getCognomeRichiedente());
		info.setVisibility(stream.getVisibility());
		info.setDomainTranslated(stream.getDomainTranslated());
		info.setSubDomainTranslated(stream.getSubDomainTranslated());
		info.setTagsTranslated(stream.getTagsTranslated());

		Tenantssharing tenantsShare = new Tenantssharing();
		List<Tenantsharing> listaTenant = new ArrayList<Tenantsharing>();
		if (stream.getTenantssharing() != null) {
			List<org.csi.yucca.storage.datamanagementapi.model.streaminput.Tenantsharing> lista = stream.getTenantssharing().getTenantsharing();
			if (lista != null) {
				Set<String> tenantSet = new TreeSet<String>();
				
				// Insert owner
				Tenantsharing newTen = new Tenantsharing();
				newTen.setIdTenant(stream.getIdTenant());
				newTen.setIsOwner(1);
				newTen.setTenantCode(stream.getCodiceTenant());
				newTen.setTenantName(stream.getNomeTenant());
				listaTenant.add(newTen);
				
				for (org.csi.yucca.storage.datamanagementapi.model.streaminput.Tenantsharing tenant : lista) {

					if (!tenant.getTenantCode().equals(stream.getCodiceTenant()) && !tenantSet.contains(tenant.getTenantCode()) && tenant.getIsOwner() != 1) {

						newTen = new Tenantsharing();
						newTen.setIdTenant(tenant.getIdTenant().longValue());
						newTen.setIsOwner(tenant.getIsOwner());
						newTen.setTenantCode(tenant.getTenantCode());
						newTen.setTenantDescription(tenant.getTenantDescription());
						newTen.setTenantName(tenant.getTenantName());
						tenantSet.add(tenant.getTenantCode());
						listaTenant.add(newTen);
					}
				}
			}
		}

		Tenantsharing arrayTenant[] = new Tenantsharing[listaTenant.size()];
		arrayTenant = listaTenant.toArray(arrayTenant);
		tenantsShare.setTenantsharing(arrayTenant);

		info.setTenantssharing(tenantsShare);

		Componenti comp = stream.getComponenti();
		if (comp != null) {
			List<Element> elem = comp.getElement();
			if (elem != null) {
				List<Field> fields = new ArrayList<Field>();

				for (Element el : elem) {

					Field f = new Field();
					f.setFieldAlias(el.getPhenomenon());
					f.setFieldName(el.getNome());
					f.setIsKey(0);
					f.setDataType(el.getDataType());
					f.setMeasureUnit(el.getMeasureUnit());
					
					f.setOrder(el.getOrder());
					
					fields.add(f);
				}
				info.setFields(fields.toArray(new Field[0]));
			}
		}

		List<Tag> tags = new ArrayList<Tag>();
		if (stream.getStreamTags() != null && stream.getStreamTags().getTag() != null) {
			for (org.csi.yucca.storage.datamanagementapi.model.streaminput.Tag t : stream.getStreamTags().getTag()) {
				Tag tag = new Tag();
				tag.setTagCode(t.getTagCode());
				tags.add(tag);
			}
		}
		info.setTags(tags.toArray(new Tag[0]));

		
		/* YUCCA-505 */
		info.setExternalReference(stream.getExternalReference());
		if (stream.getOpendata()!=null) {
			Opendata odataOut=new Opendata();
			odataOut.setAuthor(stream.getOpendata().getAuthor());
			odataOut.setDataUpdateDate(stream.getOpendata().getDataUpdateDate());
			odataOut.setOpendata( (stream.getOpendata().getIsOpendata()==null || stream.getOpendata().getIsOpendata().intValue()==0) ? false : true);
			odataOut.setLanguage(stream.getOpendata().getLanguage());
			myMeta.setOpendata(odataOut);
		}
		
		myMeta.setDcatReady(1);
		myMeta.setDcatNomeOrg(stream.getDcatNomeOrg());
		myMeta.setDcatEmailOrg(stream.getDcatEmailOrg());
		myMeta.setDcatCreatorName(stream.getDcatCreatorName());
		myMeta.setDcatCreatorType(stream.getDcatCreatorType());
		myMeta.setDcatCreatorId(stream.getDcatCreatorId());
		myMeta.setDcatRightsHolderName(stream.getDcatRightsHolderName());
		myMeta.setDcatRightsHolderType(stream.getDcatRightsHolderType());
		myMeta.setDcatRightsHolderId(stream.getDcatRightsHolderId());
		myMeta.setDcatDataUpdate(stream.getDcatDataUpdate());
				
		myMeta.setInfo(info);
		return myMeta;
	}
}
