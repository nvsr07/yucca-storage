package org.csi.yucca.storage.datamanagementapi.importutil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Info;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Tag;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import au.com.bytecode.opencsv.CSVReader;

public class ImportFromCSV {

	public static void main(String[] args) throws IOException, TemplateException {

		// Read list of TAG
		Map<String, String> tagMap = null;
		try {
			tagMap = readTagMap();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// template free
		Metadata metadata = new Metadata();
		Info info =new Info();
		info.setDatasetName("provba");
		info.setDescription("desc");
		info.setLicense("ss");
		info.setRequestorName("c");
		info.setRequestorSurname("c");
		info.setRequestornEmail("c");
		info.setDataDomain("dd");
		info.setCodSubDomain("dd");
		Tag tag1 = new Tag();
		tag1.setTagCode("TAG1");
		Tag tag2 = new Tag();
		tag2.setTagCode("TAG2");
		Tag tag3 = new Tag();
		tag3.setTagCode("TAG3");
		
		
		info.setTags(new Tag[]{tag1,tag2,tag3});
		
		metadata.setInfo(info);
		
		
		String json = getMergeTemplate(metadata,"datasetCreationTemplate.ftlh");
		
		System.out.println(json);
		
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
			tagMap.put(rigaTag[0], rigaTag[2]);
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
}
