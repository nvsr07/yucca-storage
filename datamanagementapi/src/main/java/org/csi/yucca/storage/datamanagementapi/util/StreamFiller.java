package org.csi.yucca.storage.datamanagementapi.util;

import java.util.ArrayList;
import java.util.List;

import org.csi.yucca.storage.datamanagementapi.model.streamOutput.Components;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.ConfigData;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.Element;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.StreamInternalChildren;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.StreamOut;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.StreamTags;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.Streamchild;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.Streams;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.Tag;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Componenti;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Stream;

public class StreamFiller {

	static public StreamOut fillStream(Stream stream,Integer id) {

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
		sti.setInternalQuery(stream.getInternalQuery());
		sti.setLastMessage(stream.getLastMessage());
		sti.setLastUpdate(stream.getLastUpdate());
		sti.setLicence(stream.getLicence());
		sti.setPrivacyAcceptance(stream.getAccettazionePrivacy());
		sti.setPublishStream(stream.getPublishStream());
		sti.setRegistrationDate(stream.getRegistrationDate());
		sti.setRequesterMail(stream.getMailRichiedente());
		sti.setRequesterName(stream.getNomeRichiedente());
		sti.setRequesterSurname(stream.getCognomeRichiedente());
		sti.setSaveData(stream.getSaveData());
		
		sti.setStreamStatus(stream.getStatoStream());		
		
		
		sti.setVirtualEntityCategory(stream.getCategoriaVirtualEntity());
		sti.setVirtualEntityCode(stream.getCodiceVirtualEntity());
		sti.setVirtualEntityDescription(stream.getVirtualEntityDescription());
		sti.setVirtualEntityName(stream.getVirtualEntityName());
		sti.setVirtualEntityType(stream.getTipoVirtualEntity());
		sti.setVisibility(stream.getVisibility());
		
		StreamInternalChildren streamInternalChildren=new StreamInternalChildren();
		sti.setStreamInternalChildren(streamInternalChildren);
		
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

					elementList.add(e );

				}
				components.setElement(elementList );
				sti.setComponents(components );
			}			
		}
		
		

		streams.setStream(sti);
		strOut.setStreams(streams );


		return strOut;
	}



}
