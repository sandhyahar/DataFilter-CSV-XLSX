package com.API.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.data.elasticsearch.annotations.Document;


@Entity
@Document(indexName = "param_mst")
public class ParamMst {
	
	@Id
	private String id;
	private String paramName;
	private String paramValue;
	private String paramValue2;
	private String description;
	
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public String getParamValue() {
		return paramValue;
	}
	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	public String getParamValue2() {
		return paramValue2;
	}
	public void setParamValue2(String paramValue2) {
		this.paramValue2 = paramValue2;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
	
}

