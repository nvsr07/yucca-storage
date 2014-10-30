package org.csi.yucca.storage.datamanagementapi.model;

public class TrashInfo extends AbstractEntity{
	public String trashRevision;
	private String trashDate;

	public String getTrashRevision() {
		return trashRevision;
	}

	public void setTrashRevision(String trashRevision) {
		this.trashRevision = trashRevision;
	}

	public String getTrashDate() {
		return trashDate;
	}

	public void setTrashDate(String trashDate) {
		this.trashDate = trashDate;
	}

}
