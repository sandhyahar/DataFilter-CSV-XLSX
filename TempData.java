package com.API.pojo;

import java.util.List;
import java.util.Map;

public class TempData {
	
	private String id;
    private List<Map<String, String>> headerMaping; // Change the type to List<Map<String, String>>
	private String filePath;
	private List<Header> headerList;
	private String dataName;
	private String dataMstId;
	private int Status;
	private String userId;
	private String uniqueHeaderName;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getStatus() {
		return Status;
	}
	public void setStatus(int status) {
		Status = status;
	}
	public List<Header> getHeaderList() {
		return headerList;
	}
	public void setHeaderList(List<Header> headerList) {
		this.headerList = headerList;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getDataName() {
		return dataName;
	}
	public void setDataName(String dataName) {
		this.dataName = dataName;
	}
	public String getDataMstId() {
		return dataMstId;
	}
	public void setDataMstId(String dataMstId) {
		this.dataMstId = dataMstId;
	}
	public List<Map<String, String>> getHeaderMaping() {
		return headerMaping;
	}
	public void setHeaderMaping(List<Map<String, String>> headerMaping) {
		this.headerMaping = headerMaping;
	}
	public String getUniqueHeaderName() {
		return uniqueHeaderName;
	}
	public void setUniqueHeaderName(String uniqueHeaderName) {
		this.uniqueHeaderName = uniqueHeaderName;
	}
	
}
