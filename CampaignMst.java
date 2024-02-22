package com.API.pojo;

import java.util.Date;
import java.time.LocalDateTime;
import java.util.List;

public class CampaignMst {

	private String id, campaignName, userId, filePath, processedFilePath, selectedCountry;
	private int status, isVisible,downloadFlag;
	private String endDateTime;
	private String startDateTime;
	private List<String> filterCampaignList;
	private long totalRecords;
	// FilterOption filterOption;

	public String getProcessedFilePath() {
		return processedFilePath;
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

	public int getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(int isVisible) {
		this.isVisible = isVisible;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setProcessedFilePath(String processedFilePath) {
		this.processedFilePath = processedFilePath;
	}

	public List<String> getFilterCampaignList() {
		return filterCampaignList;
	}

	public void setFilterCampaignList(List<String> filterCampaignList) {
		this.filterCampaignList = filterCampaignList;
	}

	public List<String> getCampaignList() {
		return filterCampaignList;
	}

	public void setCampaignList(List<String> filterCampaignList) {
		this.filterCampaignList = filterCampaignList;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getSelectedCountry() {
		return selectedCountry;
	}

	public void setSelectedCountry(String selectedCountry) {
		this.selectedCountry = selectedCountry;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getDownloadFlag() {
		return downloadFlag;
	}

	public void setDownloadFlag(int downloadFlag) {
		this.downloadFlag = downloadFlag;
	}

	public long getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}

	
	

}
