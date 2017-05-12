package org.csi.yucca.storage.datamanagementapi.importutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.csi.yucca.storage.datamanagementapi.model.metadata.ConfigData;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Field;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Info;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Tag;
import org.jsoup.Jsoup;

import au.com.bytecode.opencsv.CSVReader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class ImportFromCSV {

	public static void main(String[] args) throws IOException, TemplateException {

		if (args==null || args.length==0){
			System.out.println("Use java -jar dataman-import.jar <url>");
			System.exit(1);
		}
		
		
		Map<String,Field[]> campi=readFileds();
		Map<String, String> tags=readTagMap();
		String json = "";
		
		List<Metadata> metaToins= readDataset(tags,campi);
		int riga=1;
		for (Metadata md : metaToins) {
			if (md.getInfo().getFields()!=null && md.getInfo().getFields().length>0) {
				json = getMergeTemplate(md,"datasetCreationTemplate.ftlh");
				System.out.println("+++++++++++++++++++");
				System.out.println("ready to ins  ("+riga+")--> " + md.getInfo().getDatasetName());
				System.out.println(json);
				//System.out.println("+++++++++++++++++++");
				
				
				CloseableHttpClient httpclient = HttpClients.createDefault();
				HttpPost postMethod = new HttpPost(args[0]);
				
				//postMethod.setHeader("Content-type", "multipart/form-data;charset=UTF-8");
//				StringEntity requestEntity = new StringEntity(json);
//				  postMethod.setEntity(requestEntity);
				
				
				
//				ContentBody  body = new ByteArrayBody(json.getBytes(),ContentType.APPLICATION_JSON,"body");
				//ContentBody  body = new ByteArrayBody(new String(json.getBytes("UTF-8"), "iso-8859-1").getBytes(),ContentType.APPLICATION_JSON,"body");
				//ContentBody  body = new ByteArrayBody(new String(json.getBytes("Cp1252"), "iso-8859-1").getBytes(),ContentType.APPLICATION_JSON,"body");

				ContentBody  body =  new StringBody(new String(json.getBytes(), "iso-8859-1"), ContentType.APPLICATION_JSON);
				
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				builder.setCharset(Charset.forName("UTF-8"));
				builder.addPart("dataset", body);
				HttpEntity entity = builder.build();
				
	//qui			MultipartEntity entity = new MultipartEntity();
	//qui			FormBodyPart fbp=new FormBodyPart("dataset", body);
	//qui			entity.addPart( fbp);
				//entity.addPart("encoding", "UTF-8");
				postMethod.setEntity(entity);
//				ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
//				pairs.add(new BasicNameValuePair("requestData", json));
//				  postMethod.setEntity(new UrlEncodedFormEntity(pairs));
				
				  
				  			  
				  CloseableHttpResponse response = httpclient.execute(postMethod);	
				  httpclient.close();
				
			} else {
				System.out.println("+++++++++++++++++++");
				System.out.println("SKIPPED ("+riga+")--> " + md.getInfo().getDatasetName());
				System.out.println("+++++++++++++++++++");
				
			}
			riga++;
		}
		
		
	}


	
	
	
	private static String getMergeTemplate(Metadata metadata, String template) throws TemplateException, IOException {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
		cfg.setDirectoryForTemplateLoading(new File("toimport"));
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		cfg.setLogTemplateExceptions(false);
		
		Template temp = cfg.getTemplate(template);
		
		
		
		Writer out = new StringWriter();
		temp.process(metadata, out);
		return out.toString();
	}





	private static Map<String, String> readTagMap()
			throws FileNotFoundException, IOException {
		CSVReader reader;
		Map<String, String> tagMap = new HashMap<String, String>();

		reader = new CSVReader(new FileReader(
				"toimport/Mapping Tag dati piemonte_SDP_v01.csv"), ';', '\"', 1);
		List<String[]> myEntries = reader.readAll();
		if (myEntries == null || myEntries.size() <= 1) {
			System.out.println("No tags");
		}
		// skip first line
		for (String[] rigaTag : myEntries) {
			tagMap.put(rigaTag[0].trim(), rigaTag[2]);
		}
		System.out.println("Example of tags:");
		int i = 0;
		for (String tagKey : tagMap.keySet()) {
			i++;
			System.out.println(tagKey + "-->" + tagMap.get(tagKey));
			if (i > 5)
				break;
		}
		return tagMap;
	}
	
	
	private static Map<String,Field[]> readFileds() throws FileNotFoundException, IOException {
		Map<String,Field[]> ret = new HashMap<String, Field[]>();

		CSVReader reader = new CSVReader(new FileReader("toimport/Strutture_Dataset_da_migrare_01.csv"), ';', '\"', 1);
		List<String[]> myEntries = reader.readAll();
		if (myEntries == null || myEntries.size() <= 1) {
			System.out.println("No fields");
		}
		
		int nRiga=1;
		for (String[] rigaCampi : myEntries) {
			String idDato=rigaCampi[0];
			int nCampi=Integer.parseInt(rigaCampi[1]);
			ArrayList<Field> listaCampi=new ArrayList<Field>();
			//TODO - sembra che n campi sia sbagliato
			for (int i =0; i<(nCampi-1);i++) {
				String campoTot=rigaCampi[i+3];
				String [] elementiCampo=campoTot.split(":");

				//System.out.println("campo,elemeti ("+nRiga+","+i+") --> " + campoTot +","+elementiCampo.length);
				
				
				
				if (elementiCampo.length<2 || ("Date".equalsIgnoreCase(elementiCampo[1]) && elementiCampo.length<3)) {
					System.out.println("errore parsin campi  ("+nRiga+","+i+")--> " + campoTot);
				}
				
				
				Field curf=new Field();
				curf.setFieldName(elementiCampo[0]);
				curf.setFieldAlias(elementiCampo[0]);
				curf.setOrder(i+1);
				curf.setSourceColumn(i);
				curf.setDataType(elementiCampo[1]);
				
				
				if("Date".equalsIgnoreCase(curf.getDataType())) {
					curf.setDateTimeFormat(elementiCampo[2]);
				}
				
				listaCampi.add(curf);
			}
			Field[] arrCampi=listaCampi.toArray(new Field[0]);
			nRiga++;
			ret.put(idDato.trim(), arrCampi);
		}
		
		return ret;
	}
	
	
	private static List<Metadata> readDataset(Map<String, String> tagsDecode, Map<String,Field[]> campiDecode)  throws FileNotFoundException, IOException {
		List<Metadata> ret = new ArrayList<Metadata>();
		
		//CSVReader reader = new CSVReader(new FileReader("toimport/Mapping datasets da dati piemonte a SDP_V05_2_last.csv"), ';', '\"', 1);
		
		CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream("toimport/Mapping datasets da dati piemonte a SDP_V05_2_last.csv"), "UTF-8"), ';', '\"', 1);
		//CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream("toimport/Mapping datasets da dati piemonte a SDP_V05_2_last.csv"), "Cp1252"), ';', '\"', 1);
		
		List<String[]> myEntries = reader.readAll();
		if (myEntries == null || myEntries.size() <= 1) {
			System.out.println("No dataset");
		}
		
		int nRiga=1;
		for (String[] rigaDs : myEntries) {
			
			
			if (rigaDs.length<17) {
				System.out.println("errore parsing dataset ("+nRiga+") --> " );
			}
			String id=rigaDs[0].trim();
			
			Metadata md= new Metadata();
			Info info=new Info();
			info.setDatasetName(rigaDs[1]);
			info.setDescription(Jsoup.parse(rigaDs[3]).text());
			info.setDataDomain(rigaDs[4]);
			info.setCodSubDomain(rigaDs[5]);
			
			HashSet<Tag> tagsList=new HashSet<Tag>();
			HashSet<String> tagsSet=new HashSet<String>();
			String[] tags=rigaDs[6].split(",");
			for (int i=0;tags!=null && i<tags.length;i++) {
				Tag curTag=new Tag();
				String tagCode=tagsDecode.get(tags[i].trim());
				if (null!= tagCode && !tagsSet.contains(tagCode)) {
					curTag.setTagCode(tagCode);
					tagsList.add(curTag);
					tagsSet.add(tagCode);
					
				} else {
					//System.out.println("TAG mancante  ("+nRiga+","+i+") .... " +rigaDs[6]);
				}
			}
			if (tagsList.size()>0) {
				info.setTags(tagsList.toArray(new Tag[0]));
				
			}
			
			info.setVisibility(rigaDs[7]);
			info.setLicense(rigaDs[8]);
			
			
			Field [] campi=campiDecode.get(id);
			//if (null==campi || campi.length<=0) System.out.println("NESSUN CAMPO  ("+nRiga+") .... " +id);
			
			info.setFields(campi);
			
			
			
			info.setRequestorName("Claudia");
			info.setRequestorSurname("Secco");
			info.setRequestornEmail("claudia.secco@csi.it");
			
			md.setDcatCreatorName(rigaDs[9]);
			md.setDcatCreatorType(rigaDs[10]);
			md.setDcatCreatorId(rigaDs[11]);
			md.setDcatRightsHolderName(rigaDs[12]);
			md.setDcatRightsHolderType(rigaDs[13]);
			md.setDcatRightsHolderId(rigaDs[14]);
			md.setDcatNomeOrg(rigaDs[15]);
			md.setDcatEmailOrg(rigaDs[16]);
			
			md.setInfo(info);
			
			ConfigData cd= new ConfigData();
			cd.setTenantCode("tst_regpie");
			md.setConfigData(cd);
			ret.add(md);
			
			
			nRiga++;
		}
		
		
		
		return ret;
	}
}
