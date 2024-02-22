package com.API.pojo;

import java.util.List;

public class FilterOption {
	int removeDuplicate = 1;
	List<String> campaignList;

	public int getRemoveDuplicate() {
		return removeDuplicate;
	}

	public void setRemoveDuplicate(int removeDuplicate) {
		this.removeDuplicate = removeDuplicate;
	}

	public List<String> getCampaignList() {
		return campaignList;
	}

	public void setCampaignList(List<String> campaignList) {
		this.campaignList = campaignList;
	}

}
