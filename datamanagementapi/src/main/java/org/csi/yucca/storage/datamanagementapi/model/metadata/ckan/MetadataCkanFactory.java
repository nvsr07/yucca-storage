package org.csi.yucca.storage.datamanagementapi.model.metadata.ckan;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;

import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Tag;
import org.csi.yucca.storage.datamanagementapi.singleton.Config;
import org.csi.yucca.storage.datamanagementapi.util.Constants;
import org.csi.yucca.storage.datamanagementapi.util.Util;

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
		String downloadCsvUrl = baseExposedApiUrl + metadata.getDatasetCode() + "/?$format=csv";
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

			messagesMap.put(lang, ResourceBundle.getBundle("/i18n/MessagesBundle", locale));

		}
		return messagesMap.get(lang);
	}

}
