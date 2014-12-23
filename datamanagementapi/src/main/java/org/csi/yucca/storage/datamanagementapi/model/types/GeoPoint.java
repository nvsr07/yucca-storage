package org.csi.yucca.storage.datamanagementapi.model.types;

import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import com.google.gson.Gson;

public class GeoPoint {

	private Double[] idxLocation;

	public String toJson() {
		Gson gson = JSonHelper.getInstance();
		return gson.toJson(this);
	}

	public GeoPoint() {
		super();
	}

	public GeoPoint(Double longitude, Double latitude) {
		super();
		setIdxLocation(new Double[] { longitude, latitude });
	}

	public void setLatitude(Double latitude) {
		if (idxLocation == null)
			idxLocation = new Double[2];
		idxLocation[0] = latitude;
	}

	public void setLongitude(Double longitude) {
		if (idxLocation == null)
			idxLocation = new Double[2];
		idxLocation[1] = longitude;
	}

	public Double[] getIdxLocation() {
		return idxLocation;
	}

	public void setIdxLocation(Double[] idxLocation) {
		this.idxLocation = idxLocation;
	}

	public boolean isValid() {
		return idxLocation != null && idxLocation.length == 2 && idxLocation[0] != null && idxLocation[1] != null;
	}

	public static void main(String[] args) {
		GeoPoint geoPoint = new GeoPoint();
		// System.out.println(geoPoint.getLatitude());
		// System.out.println(geoPoint.getIdxLocation()[0]);
		System.out.println(geoPoint.toJson());
		System.out.println(geoPoint.isValid());
		geoPoint.setLatitude(3.);
		System.out.println(geoPoint.toJson());
		System.out.println(geoPoint.isValid());
		geoPoint.setLongitude(33.);
		System.out.println(geoPoint.toJson());
		System.out.println(geoPoint.isValid());
	}

}
