package org.csi.yucca.storage.datamanagementapi.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.csi.yucca.storage.datamanagementapi.model.metadata.ConfigData;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Field;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Info;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Tag;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Componenti;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Element;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Stream;

public class MetadataFiller {


	static public Metadata fillMetadata(Stream stream) {

		System.out.println("FILL METADATA OBJECT");

		Metadata myMeta = new Metadata();

		//		String datasetCode = "ds_"+stream.getCodiceStream()+"_"; calculation on insert
		//		myMeta.setDatasetCode(datasetCode);
		myMeta.setDatasetVersion(stream.getDeploymentVersion());
		ConfigData cf = new ConfigData();
		cf.setCurrent(1);
		cf.setType("dataset");
		cf.setSubtype("streamDataset");
		//		cf.setEntityNameSpace("it.csi.smartdata.odata."+stream.getCodiceTenant()+".");
		cf.setDatasetStatus(stream.getDeploymentStatusCode());
		cf.setIdTenant(stream.getIdTenant());
		cf.setTenantCode(stream.getCodiceTenant());

		myMeta.setConfigData(cf );

		Info info= new Info();
		info.setCopyright(stream.getCopyright());
		info.setDataDomain(stream.getDomainStream());
		info.setDatasetName(stream.getCodiceStream());
		info.setDescription("Dataset " +stream.getNomeStream());
		info.setDisclaimer(stream.getDisclaimer());

		info.setFps(stream.getFps());
		info.setLicense(stream.getLicence());

		Date date=null;
		if(stream.getRegistrationDate()!=null){		
			try {
				date = new SimpleDateFormat(Constants.DATE_FORMAT).parse(stream.getRegistrationDate());
			} catch (ParseException e) {
				date = new Date();
			}
		}else{
			date = new Date();
		}
		info.setRegistrationDate(date);
		info.setRequestornEmail(stream.getMailRichiedente());
		info.setRequestorName(stream.getNomeRichiedente());
		info.setRequestorSurname(stream.getCognomeRichiedente());
		info.setVisibility(stream.getVisibility());



		Componenti comp = stream.getComponenti();
		if(comp!=null){
			List<Element> elem = comp.getElement();
			if(elem!=null){
				List<Field> fields = new ArrayList<Field>();

				Field f = new Field();	
				f.setFieldAlias("-");
				f.setFieldName("streamCode");
				f.setIsKey(0);
				f.setDataType("string");
				f.setMeasureUnit("-");
				fields.add(f );
				
				f = new Field();	
				f.setFieldAlias("-");
				f.setFieldName("sensor");
				f.setIsKey(0);
				f.setDataType("string");
				f.setMeasureUnit("-");
				fields.add(f );

				
				f = new Field();	
				f.setFieldAlias("-");
				f.setFieldName("time");
				f.setIsKey(0);
				f.setDataType("dateTime");
				f.setMeasureUnit("-");
				fields.add(f );
				
				for(Element el : elem){

					f = new Field();	
					f.setFieldAlias(el.getPhenomenon());
					f.setFieldName(el.getNome());
					f.setIsKey(0);
					f.setDataType(el.getDataType());
					f.setMeasureUnit(el.getMeasureUnit());
					fields.add(f );
				}
				info.setFields(fields.toArray(new Field[0]));
			}			
		}

		List<Tag> tags = new ArrayList<Tag>();
		if(stream.getStreamTags()!=null && stream.getStreamTags().getTag()!=null){
			for(  org.csi.yucca.storage.datamanagementapi.model.streaminput.Tag t : stream.getStreamTags().getTag()){
				Tag tag = new Tag();
				tag.setTagCode(t.getTagCode());
				tags.add(tag);
			}
		}

		info.setTags(tags.toArray(new Tag[0]));


		myMeta.setInfo(info);

		return myMeta;
	}

}
