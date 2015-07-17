package org.csi.yucca.storage.datamanagementapi.model.streamOutput;

public class Position {
	private Double lon;
	private Double lat;
	private Double elevation;
	private Integer floor;
	private String building;
	private String room;

	public Position() {
		super();
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getElevation() {
		return elevation;
	}

	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}

	public Integer getFloor() {
		return floor;
	}

	public void setFloor(Integer floor) {
		this.floor = floor;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

}
