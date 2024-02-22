package com.API.pojo;

import java.util.List;
import java.util.Map;

public class FileUpload {

	    private List<String> headers;
        private List<Map<String, String>> sampleRecords;
	    private long total;
	    private String filepath;

	    public List<String> getHeaders() {
	        return headers;
	    }

	    public void setHeaders(List<String> headers) {
	        this.headers = headers;
	    }

	    public List<Map<String, String>> getSampleRecords() {
            return sampleRecords;
        }

        public void setSampleRecords(List<Map<String, String>> sampleRecords) {
            this.sampleRecords = sampleRecords;
        }
	    public long getTotal() {
	        return total;
	    }

	    public void setTotal(long total) {
	        this.total = total;
	    }

	    public String getFilepath() {
	        return filepath;
	    }

	    public void setFilepath(String filepath) {
	        this.filepath = filepath;
	    }
	}

		


