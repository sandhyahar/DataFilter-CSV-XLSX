package com.API.pojo;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FilterCondition {

	private String field;
	private String condition;
	private String value;

	public FilterCondition() {

	}

	public FilterCondition(String jsonString) {
		// Deserialize the jsonString and initialize the FilterCondition object
		
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			FilterCondition filterCondition = objectMapper.readValue(jsonString, FilterCondition.class);
			this.field = filterCondition.getField();
			this.condition = filterCondition.getCondition();
			this.value = filterCondition.getValue();
		} catch (IOException e) {
			// Handle the exception or rethrow it
		}
	}

	public String getCondition() {
		return condition;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
