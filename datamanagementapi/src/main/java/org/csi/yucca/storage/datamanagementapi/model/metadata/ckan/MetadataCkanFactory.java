package org.csi.yucca.storage.datamanagementapi.model.metadata.ckan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.TimeZone;

import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Tag;
import org.csi.yucca.storage.datamanagementapi.singleton.Config;
import org.csi.yucca.storage.datamanagementapi.util.Constants;
import org.csi.yucca.storage.datamanagementapi.util.Util;

import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;

public class MetadataCkanFactory {

	public static final String PACKAGE_ID_SEPARATOR = "_";

	public MetadataCkanFactory() {
	}

	public static String createPackageId(Long idDataset, Integer datasetVersion) {
		return "smartdatanet.it" + PACKAGE_ID_SEPARATOR + idDataset + PACKAGE_ID_SEPARATOR + datasetVersion;
	}

	public static String createPackageId(Metadata metadata) {
		return createPackageId(metadata.getIdDataset(), metadata.getDatasetVersion());
	}

	public static Long getDatasetDatasetIdFromPackageId(String packageId) {
		String[] packageIdSplitted = packageId.split(PACKAGE_ID_SEPARATOR);
		return new Long(packageIdSplitted[1]);
	}

	public static Integer getDatasetVersionFromPackageId(String packageId) {
		String[] packageIdSplitted = packageId.split(PACKAGE_ID_SEPARATOR);
		return new Integer(packageIdSplitted[2]);
	}

	public static Dataset createDataset(Metadata metadata) {
		Dataset ckanDataset = new Dataset();
		ckanDataset.setId(createPackageId(metadata));
		ckanDataset.setName(createPackageId(metadata));
		ckanDataset.setTitle(metadata.getInfo().getDatasetName());
		ckanDataset.setNotes(metadata.getInfo().getDescription());
		ckanDataset.setVersion(Util.nvl(metadata.getDatasetVersion()));

		String storeApiAddress = Config.getInstance().getStoreApiAddress();
		String metadataUrl = storeApiAddress + "name=" + metadata.getDatasetCode() + "_odata&version=1.0&provider=admin  ";
		ckanDataset.setUrl(metadataUrl);

		Resource resourceApiOdata = new Resource();
		resourceApiOdata.setDescription("Api Odata");
		resourceApiOdata.setFormat("ODATA");
		String baseExposedApiUrl = Config.getInstance().getBaseExposedApiUrl();
		String apiOdataUrl = baseExposedApiUrl + metadata.getDatasetCode();
		resourceApiOdata.setUrl(apiOdataUrl);
		ckanDataset.addResource(resourceApiOdata);

		Resource resourceDownload = new Resource();
		resourceDownload.setDescription("Csv download url");
		resourceDownload.setFormat("CSV");
		//String downloadCsvUrl = baseExposedApiUrl + metadata.getDatasetCode() + "/?$format=csv"; BASE_EXPOSED_API_URL=https://int-api.smartdatanet.it:443/api/
		String downloadCsvUrl  =  Config.getInstance().getBaseExposedApiUrl() + metadata.getDatasetCode() + "/download/"+metadata.getIdDataset()+"/";
		if(Metadata.CONFIG_DATA_TYPE_DATASET.equals(metadata.getConfigData().getType()) && Metadata.CONFIG_DATA_SUBTYPE_BULK_DATASET.equals(metadata.getConfigData().getSubtype())) {
			downloadCsvUrl += "all";
		}
		else{
			downloadCsvUrl += "current";
		}
		
		resourceDownload.setUrl(downloadCsvUrl);
		ckanDataset.addResource(resourceDownload);

		ckanDataset.setAuthor(metadata.getOpendata().getAuthor());
		ckanDataset.setLicense(metadata.getInfo().getLicense());
		ckanDataset.setIsopen(true);
		if (metadata.getOpendata().getMetadaCreateDate() != null)
			ckanDataset.setMetadata_created(formatDate(metadata.getOpendata().getMetadaCreateDate()));
		if (metadata.getOpendata().getMetadaUpdateDate() != null)
			ckanDataset.setMetadata_created(formatDate(metadata.getOpendata().getMetadaUpdateDate()));

		String lang = metadata.getOpendata().getLanguage() == null ? Constants.LANGUAGES_SUPPORTED[0] : metadata.getOpendata().getLanguage();

		ResourceBundle messages = getMessages(lang);

		if (metadata.getInfo().getTags() != null) {
			for (Tag tag : metadata.getInfo().getTags()) {
				// ckanDataset.addTag(tag.getTagCode(),
				// messages.getString(tag.getTagCode()));
				ckanDataset.addTag(messages.getString(tag.getTagCode()));
			}
		}

		ExtraV2 extras = new ExtraV2();
		if (metadata.getInfo().getDataDomain() != null) {
			String domainTranslated = messages.getString(metadata.getInfo().getDataDomain());
			extras.setTopic(domainTranslated);
			extras.setHidden_field(domainTranslated);
		}

		if (metadata.getOpendata().getMetadaCreateDate() != null)
			extras.setMetadata_created(formatDate(metadata.getOpendata().getMetadaCreateDate()));
		if (metadata.getOpendata().getMetadaUpdateDate() != null)
			extras.setMetadata_modified(formatDate(metadata.getOpendata().getMetadaUpdateDate()));
		if (metadata.getInfo().getRegistrationDate() != null)
			extras.setPackage_created(formatDate(metadata.getInfo().getRegistrationDate()));
		if (metadata.getOpendata().getDataUpdateDate() != null)
			extras.setPackage_modified(formatDate(new Date(metadata.getOpendata().getDataUpdateDate())));

		if (ckanDataset.getResources() != null) {
			List<String> resourcesList = new LinkedList<String>();
			for (Resource resource : ckanDataset.getResources()) {
				resourcesList.add(resource.createResourceV2());

			}
			Map<String, List<String>> extrasList = new HashMap<String, List<String>>();
			extrasList.put("resource", resourcesList);
			ckanDataset.setExtrasList(extrasList);
		}

		if (metadata.getInfo().getImportFileType() != null)
			extras.setPackage_type(metadata.getInfo().getImportFileType());

		return ckanDataset;
	}

	private static String formatDate(Date date) {
		String formattedDate = null;
		if (date != null) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			df.setTimeZone(TimeZone.getTimeZone("UTC"));
			formattedDate = df.format(date);
		}
		return formattedDate;
	}

	private static Map<String, ResourceBundle> messagesMap = new HashMap<String, ResourceBundle>();

	private static ResourceBundle getMessages(String lang) {

		if (messagesMap.get(lang) == null) {
			String effectiveLang = Constants.LANGUAGES_SUPPORTED[0];
			for (String supportedLang : Constants.LANGUAGES_SUPPORTED) {
				if (supportedLang.equals(lang))
					effectiveLang = lang;
			}

			Locale locale = new Locale(effectiveLang);

			//messagesMap.put(lang, ResourceBundle.getBundle("/i18n/MessagesBundle", locale));
			String tagResource = "";
			String domainResource = "";

			tagResource = formatMessages(locale, "tags");
			domainResource = formatMessages(locale, "domains");
			try {
				messagesMap.put(lang, new PropertyResourceBundle(new StringReader(tagResource + "\n" + domainResource)));
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}

		}
		return messagesMap.get(lang);
	}
	
	private static JSONObject loadMessages(Locale currentLocale, String element) {
		InputStream is = null;
		JSONObject json = null;
		try {
			String tagsDomainsURL = Config.getInstance().getApiAdminServiceUrl();
			is = new URL(tagsDomainsURL + "/misc/stream"+element+"/").openStream();

			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = null;
			jsonText = readAll(rd);
			json = new JSONObject(jsonText);

			is.close();
		} catch (JSONException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return json;
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	protected static String formatMessages(Locale locale, String element) {
		
		JSONObject messages = loadMessages(locale, element);
		
		StringBuffer sb = new StringBuffer("");
		String loc = locale.getLanguage().substring(0, 1).toUpperCase() + locale.getLanguage().substring(1);
		
		String label1 = (element.equals("tags") ? "streamTags" : "streamDomains");
		String label2 = (element.equals("tags") ? "tagCode" : "codDomain");
		
		try {
			JSONObject streamTags = messages.getJSONObject(label1);
			JSONArray elements = streamTags.getJSONArray("element");
			for(int i = 0 ; i < elements.length() ; i++){
				String tagCode = elements.getJSONObject(i).getString(label2);
				String langEl = elements.getJSONObject(i).getString("lang"+loc);
			    sb.append(tagCode + " = " + langEl + "\n");
			}

		} catch (JSONException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

		return sb.toString();
	}

}
