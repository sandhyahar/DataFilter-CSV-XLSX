package com.API.pojo;

import java.util.List;

public class FilterDataMaster {

	private String id;
	private String dataMstId;
	private String userId;
	private String filePath;
	private List<FilterCondition> filterList;
	private int Status;
	private String endDateTime;
	private String startDateTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDataMstId() {
		return dataMstId;
	}

	public void setDataMstId(String dataMstId) {
		this.dataMstId = dataMstId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
	public List<FilterCondition> getFilterList() {
		return filterList;
	}

	public void setFilterList(List<FilterCondition> filterList) {
		this.filterList = filterList;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public String getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(String endDateTime) {
		this.endDateTime = endDateTime;
	}

	public String getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(String startDateTime) {
		this.startDateTime = startDateTime;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}
