package com.API.pojo;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Header {
    private String name;
    private boolean uniqueKey;
    private boolean notNull;
    private String validationType;

    public Header() {
        // Default constructor
    }

    public Header(String jsonString) {
		// Deserialize the jsonString and initialize the FilterCondition object
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			Header filterCondition = objectMapper.readValue(jsonString, Header.class);
			this.name = filterCondition.getName();
			this.uniqueKey = filterCondition.getUniqueKey();
			this.notNull = filterCondition.getNotNull();
			this.validationType = filterCondition.getValidationType();
		} catch (IOException e) {
			// Handle the exception or rethrow it
		}
	}



	// Getters and setters for the properties

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(boolean uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public boolean getNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

	public String getValidationType() {
		return validationType;
	}

	public void setValidationType(String validationType) {
		this.validationType = validationType;
	}
    
}
