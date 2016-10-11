package org.csi.yucca.storage.datamanagementapi.model.metadata;

import java.util.Date;

import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import com.google.gson.Gson;

public class Dcat {

	private boolean dcatReady;
	private String agentName;
	private String agentType;
	private String identificativo;
	private String descrCat;
	private String editore;
	private String titoloCat;
	private String homepage;
	private String puntoContatto;
	private String spatial;
	private String vcard;
	private String nomeOrg;
	private String emailOrg;
	private String telOrg;
	private String urlOrg;

	public static Dcat fromJson(String json) {
		Gson gson = JSonHelper.getInstance();
		return gson.fromJson(json, Dcat.class);
	}

	public String toJson() {
		Gson gson = JSonHelper.getInstance();
		return gson.toJson(this);
	}

	public Dcat() {
	}

	public boolean isDcatReady() {
		return dcatReady;
	}

	public void setDcatReady(boolean dcatReady) {
		this.dcatReady = dcatReady;
	}

	public String getAgentName() {
		String rtn =  (agentName == null) ? "" : agentName;
		return rtn;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getAgentType() {
		String rtn =  (agentType == null) ? "" : agentType;
		return rtn;
	}

	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}

	public String getIdentificativo() {
		String rtn =  (identificativo == null) ? "" : identificativo;
		return rtn;
	}

	public void setIdentificativo(String identificativo) {
		this.identificativo = identificativo;
	}

	public String getDescrCat() {
		String rtn =  (descrCat == null) ? "" : descrCat;
		return rtn;
	}

	public void setDescrCat(String descrCat) {
		this.descrCat = descrCat;
	}

	public String getEditore() {
		String rtn =  (editore == null) ? "" : editore;
		return rtn;
	}

	public void setEditore(String editore) {
		this.editore = editore;
	}

	public String getTitoloCat() {
		String rtn =  (titoloCat == null) ? "" : titoloCat;
		return rtn;
	}

	public void setTitoloCat(String titoloCat) {
		this.titoloCat = titoloCat;
	}

	public String getHomepage() {
		String rtn =  (homepage == null) ? "" : homepage;
		return rtn;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public String getPuntoContatto() {
		String rtn =  (puntoContatto == null) ? "" : puntoContatto;
		return rtn;
	}

	public void setPuntoContatto(String puntoContatto) {
		this.puntoContatto = puntoContatto;
	}

	public String getSpatial() {
		String rtn =  (spatial == null) ? "" : spatial;
		return rtn;
	}

	public void setSpatial(String spatial) {
		this.spatial = spatial;
	}

	public String getVcard() {
		String rtn =  (vcard == null) ? "" : vcard;
		return rtn;
	}

	public void setVcard(String vcard) {
		this.vcard = vcard;
	}

	public String getNomeOrg() {
		String rtn =  (nomeOrg == null) ? "" : nomeOrg;
		return rtn;
	}

	public void setNomeOrg(String nomeOrg) {
		this.nomeOrg = nomeOrg;
	}

	public String getEmailOrg() {
		String rtn =  (emailOrg == null) ? "" : emailOrg;
		return rtn;
	}

	public void setEmailOrg(String emailOrg) {
		this.emailOrg = emailOrg;
	}

	public String getTelOrg() {
		String rtn =  (telOrg == null) ? "" : telOrg;
		return rtn;
	}

	public void setTelOrg(String telOrg) {
		this.telOrg = telOrg;
	}

	public String getUrlOrg() {
		String rtn =  (urlOrg == null) ? "" : urlOrg;
		return rtn;
	}

	public void setUrlOrg(String urlOrg) {
		this.urlOrg = urlOrg;
	}
	
	
}