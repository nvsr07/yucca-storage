package org.csi.yucca.storage.datamanagementapi.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.csi.yucca.storage.datamanagementapi.model.streamOutput.Components;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.ConfigData;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.Element;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.StreamInternalChildren;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.StreamOut;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.StreamTags;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.Streamchild;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.Streams;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.Tag;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.Tenantsharing;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.Tenantssharing;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Componenti;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Position;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Stream;
import org.omg.CORBA.SetOverrideType;

public class StreamFiller {

	static public StreamOut fillStream(Stream stream,Long id) {

		System.out.println("FILL STREAM OBJECT");
		StreamOut strOut = new StreamOut();
		strOut.setIdStream(stream.getIdStream());
		strOut.setStreamCode(stream.getCodiceStream());
		strOut.setStreamName(stream.getNomeStream());


		// Setting ConfigData in Stream Object
		ConfigData configData = new ConfigData();

		configData.setDatasetVersion(stream.getDeploymentVersion());
		configData.setIdDataset(id);
		configData.setIdTenant(stream.getIdTenant());
		configData.setTenantCode(stream.getCodiceTenant());

		strOut.setConfigData(configData);


		// Setting 

		Streams streams = new Streams();

		org.csi.yucca.storage.datamanagementapi.model.streamOutput.Stream sti = new org.csi.yucca.storage.datamanagementapi.model.streamOutput.Stream();

		sti.setCopyright(stream.getCopyright());
		sti.setDeploymentStatusCode(stream.getDeploymentStatusCode());
		sti.setDeploymentStatusDesc(stream.getDeploymentStatusDesc());
		sti.setDeploymentVersion(stream.getDeploymentVersion());
		sti.setDisclaimer(stream.getDisclaimer());
		sti.setDomainStream(stream.getDomainStream());
		sti.setFabricControllerOutcome(stream.getEsitoFabricController());
		sti.setFps(stream.getFps());
		sti.setIdCategoriaVe(stream.getIdCategoriaVe());
		sti.setIdTipoVe(stream.getIdTipoVe());
		sti.setIdVirtualEntity(stream.getIdVirtualEntity());
		sti.setStreamIcon(stream.getStreamIcon());
		sti.setInternalQuery(stream.getInternalQuery());
		sti.setLastMessage(stream.getLastMessage());
		sti.setLastUpdate(stream.getLastUpdate());
		sti.setLicence(stream.getLicence());
		sti.setPrivacyAcceptance(stream.getAccettazionePrivacy());
		sti.setPublishStream(stream.getPublishStream()==1?true:false);
		sti.setRegistrationDate(stream.getRegistrationDate());
		sti.setRequesterMail(stream.getMailRichiedente());
		sti.setRequesterName(stream.getNomeRichiedente());
		sti.setRequesterSurname(stream.getCognomeRichiedente());
		sti.setSaveData(stream.getSaveData());

		sti.setStreamStatus(stream.getStatoStream());	
		
		sti.setTwtCount(stream.getTwtCount());
		sti.setTwtGeolocLat(stream.getTwtGeolocLat());
		sti.setTwtGeolocLon(stream.getTwtGeolocLon());
		sti.setTwtGeolocRadius(stream.getTwtGeolocRadius());
		sti.setTwtGeolocUnit(stream.getTwtGeolocUnit());
		sti.setTwtLang(stream.getTwtLang());
		sti.setTwtLocale(stream.getTwtLocale());
		sti.setTwtQuery(stream.getTwtQuery());
		sti.setTwtMaxStreamsOfVE(stream.getTwtMaxStreamsOfVE());
		sti.setTwtRatePercentage(stream.getTwtRatePercentage());
		sti.setTwtResultType(stream.getTwtResultType());
		sti.setTwtUntil(stream.getTwtUntil());


		sti.setVirtualEntityCategory(stream.getCategoriaVirtualEntity());
		sti.setVirtualEntityCode(stream.getCodiceVirtualEntity());
		sti.setVirtualEntityDescription(stream.getVirtualEntityDescription());
		sti.setVirtualEntityName(stream.getVirtualEntityName());
		sti.setVirtualEntityType(stream.getTipoVirtualEntity());
		sti.setVisibility(stream.getVisibility());

		StreamInternalChildren streamInternalChildren=new StreamInternalChildren();
		sti.setStreamInternalChildren(streamInternalChildren);


		//FIXME make an array when the model changes for now just make it work :P .
		org.csi.yucca.storage.datamanagementapi.model.streamOutput.VirtualEntityPositions VEPO = new org.csi.yucca.storage.datamanagementapi.model.streamOutput.VirtualEntityPositions();
		if(stream.getVirtualEntityPositions()!=null){

			VEPO.setPosition(new ArrayList<org.csi.yucca.storage.datamanagementapi.model.streamOutput.Position>());

			org.csi.yucca.storage.datamanagementapi.model.streamOutput.Position position =new org.csi.yucca.storage.datamanagementapi.model.streamOutput.Position();
			for(Position p : stream.getVirtualEntityPositions().getPosition()){
				position.setRoom(p.getRoom());
				position.setBuilding(p.getBuilding());
				position.setElevation(p.getElevation());
				position.setFloor(p.getFloor());
				position.setLat(p.getLat());
				position.setLon(p.getLon());

				VEPO.getPosition().add(position);
			}
			sti.setVirtualEntityPositions(VEPO);
		}

		if(stream.getStreamInternalChildren()!=null && stream.getStreamInternalChildren().getStreamChildren()!=null){
			List<Streamchild> scl = new ArrayList<Streamchild>();
			List<org.csi.yucca.storage.datamanagementapi.model.streaminput.Streamchild> lista = stream.getStreamInternalChildren().getStreamChildren();
			for(org.csi.yucca.storage.datamanagementapi.model.streaminput.Streamchild child : lista){
				Streamchild sc = new Streamchild();
				sc.setAliasChildStream(child.getAliasChildStream());
				sc.setIdChildStream(child.getIdChildStream());
				sc.setIdStream(child.getIdStream());
				//				sc.setIdTenant(child.getCodiceTenant());
				sc.setInternalQuery(child.getInternalQuery());
				sc.setStreamCode(child.getCodiceStream());
				sc.setStreamName(child.getNomeStream());
				sc.setTenantCode(child.getCodiceTenant());
				sc.setTenantName(child.getNomeTenant());
				sc.setVirtualEntityCode(child.getCodiceVirtualEntity());
				sc.setVirtualEntityDescription(child.getVirtualEntityDescription());
				sc.setVirtualEntityName(child.getVirtualEntityName());

				scl.add(sc);
			}
			streamInternalChildren.setStreamChildren(scl );

		}

		StreamTags streamTags = new StreamTags();
		List<Tag> tags = new ArrayList<Tag>();

		if(stream.getStreamTags()!=null && stream.getStreamTags().getTag()!=null){
			for(  org.csi.yucca.storage.datamanagementapi.model.streaminput.Tag t : stream.getStreamTags().getTag()){
				Tag tag = new Tag();
				tag.setTagCode(t.getTagCode());
				tags.add(tag);
			}
		}

		streamTags.setTags(tags );
		sti.setStreamTags(streamTags );

		Componenti comp = stream.getComponenti();
		if(comp!=null){
			List<org.csi.yucca.storage.datamanagementapi.model.streaminput.Element> elem = comp.getElement();
			if(elem!=null){
				Components components = new Components();
				List<Element> elementList = new ArrayList<Element>();
				for(org.csi.yucca.storage.datamanagementapi.model.streaminput.Element el : elem){

					Element e = new Element();				
					e.setComponentAlias(el.getNome());
					e.setComponentName(el.getNome());
					e.setDataType(el.getDataType());
					e.setIdComponent(el.getIdComponente());
					e.setIdDataType(el.getIdDataType());
					e.setIdMeasureUnit(el.getIdMeasureUnit());
					e.setIdPhenomenon(el.getIdPhenomenon());
					e.setMeasureUnit(el.getMeasureUnit());
					e.setMeasureUnitCategory(el.getMeasureUnitCategory());
					e.setPhenomenon(el.getPhenomenon());
					e.setPhenomenonCategory(el.getPhenomenonCategory());
					e.setTolerance(el.getTolerance());
					e.setSinceVersion(el.getSinceVersion());

					elementList.add(e );

				}
				components.setElement(elementList );
				sti.setComponents(components );
			}			
		}

		Tenantssharing tenantsShare = new Tenantssharing();
		List<Tenantsharing> listaTenant = new ArrayList<Tenantsharing>();
		if(stream.getTenantssharing()!=null){
			List<org.csi.yucca.storage.datamanagementapi.model.streaminput.Tenantsharing> lista = stream.getTenantssharing().getTenantsharing();
			if(lista!=null){
				Set<String> tenantSet = new TreeSet<String>();
				for(org.csi.yucca.storage.datamanagementapi.model.streaminput.Tenantsharing tenant : lista){
					if(tenant.getTenantCode().equals(stream.getCodiceTenant()) && !tenantSet.contains(tenant.getTenantCode())  && tenant.getIsOwner() !=1){

						Tenantsharing newTen = new Tenantsharing();
						newTen.setIdTenant(tenant.getIdTenant());
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
		
		//Insert owner
		Tenantsharing newTen = new Tenantsharing();
		newTen.setIdTenant(stream.getIdTenant().intValue());
		newTen.setIsOwner(1);
		newTen.setTenantCode(stream.getCodiceTenant());
		newTen.setTenantName(stream.getNomeTenant());					
		listaTenant.add(newTen);	
		
		tenantsShare.setTenantsharing(listaTenant);
	
		sti.setTenantssharing(tenantsShare);

		streams.setStream(sti);
		strOut.setStreams(streams );


		return strOut;
	}



}
