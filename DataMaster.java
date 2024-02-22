package com.API.pojo;

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class DataMaster {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;
	private String UserId;
	private String dataName;
    private List<Header> HeaderList;
    private int Status;
    private String filePath;
    private String insert_time;
    private String Data;
    private long total;
    
    
	public String getData() {
		return Data;
	}
	public void setData(String data) {
		Data = data;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getUserId() {
		return UserId;
	}
	public void setUserId(String userId) {
		UserId = userId;
	}
	public String getDataName() {
		return dataName;
	}
	public void setDataName(String dataName) {
		this.dataName = dataName;
	}
	
	public List<Header> getHeaderList() {
		return HeaderList;
	}
	public void setHeaderList(List<Header> headerList) {
		HeaderList = headerList;
	}
	public int getStatus() {
		return Status;
	}
	public void setStatus(int status) {
		Status = status;
	}
	public String getInsert_time() {
		return insert_time;
	}
	public void setInsert_time(String formattedDateTime) {
		this.insert_time = formattedDateTime;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
    
	
	

}
