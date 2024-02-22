package com.API.Events;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.json.JSONObject;
import org.springframework.util.ObjectUtils;

import com.API.Common.QueryBuilderUtils;
import com.API.Common.common;
import com.API.pojo.Header;
import com.API.pojo.TempData;
import com.API.pojo.ValidationMaster;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.monitorjbl.xlsx.StreamingReader;
import com.opencsv.CSVReader;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;

public class ProcessTempDataThread implements Runnable {

	TempData tm;

	RestHighLevelClient resthighLevelClient;

	ElasticsearchClient elasticsearchClient;
	


	public ProcessTempDataThread(TempData tempMaster, ElasticsearchClient elasticsearchClient,
			RestHighLevelClient resthighLevelClient) {
		this.elasticsearchClient = elasticsearchClient;
		this.resthighLevelClient = resthighLevelClient;
		this.tm = tempMaster;
	}

	@Override
	public void run() {
		if (tm != null) {
			try {
				readFile(tm.getFilePath());
			} catch (ElasticsearchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tm.setStatus(3);
			try {
				updateFilterDataMaster(tm, tm.getId());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	boolean checkFileExist(String file) {
		boolean flag = false;
		File f = new File(file);
		if (f.exists() && !f.isDirectory()) {
			flag = true;
		}
		return flag;
	}

	public void readFile(String fileName) {
		HashMap<String, JSONObject> inputHash = new HashMap<>();
		BulkRequest bulkRequest = new BulkRequest();
		boolean rowFlag = true;
		try {
			if (fileName.endsWith(".xlsx")) {
				inputHash = readXlsxFile(fileName);
			} else if (fileName.endsWith(".csv")) {
				inputHash = readCsvFile(fileName);

			} else {
				System.out.println("Unsupported file format");
			}
			if (inputHash != null && inputHash.size() > 0) {
				int startIndex = 0;
				int cnt = 1;
				while (cnt > 0) {
					cnt = getUniqueSearchData(tm.getDataMstId(), startIndex, tm.getUniqueHeaderName(), inputHash,
							tm.getUserId());
					startIndex += 500;
				}

				if (inputHash != null && inputHash.size() > 0) {
					for (Map.Entry<String, JSONObject> entry : inputHash.entrySet()) {
						String uniqueHeaderValue = entry.getKey(); // Get the mobile number as the uniqueHeaderValue
						JSONObject jsonObject = entry.getValue(); // Get the JSONObject for this mobile number
						bulkRequest.add(new IndexRequest("detail_data_mst").source(jsonObject.toMap()));
						cnt++;
						if (cnt > 500) {
							insertRecords(bulkRequest);
							bulkRequest = new BulkRequest();
							cnt = 0;
						}
					}
					if (cnt > 0) {
						insertRecords(bulkRequest);
						bulkRequest = new BulkRequest();
						cnt = 0;
					}
				}
				
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}

	public HashMap<String, JSONObject> readCsvFile(String fileName) {
		HashMap<String, JSONObject> inputHash = new HashMap<>();
		HashMap<String, Set<String>> uniqueValuesMap = new HashMap<>();
		Set<String> uniqueValues = null;

		try (CSVReader csvr = new CSVReader(new FileReader(fileName))) {
			String[] header = csvr.readNext(); // Read the header line

			if (header == null) {
				System.out.println("Empty or missing header in the CSV file.");
				return inputHash;
			}

			HashMap<Integer, Header> map = new HashMap<>();
			String uniqueHeader = "";
			uniqueHeader = readHeader(header, map); // Process the header line and store the header mapping

			if (map.isEmpty()) {
				System.out.println("Header mapping is empty. Please check the Header list in");
				return inputHash;
			}

			int count = 0; 

			String[] line;
			while ((line = csvr.readNext()) != null) {
				JSONObject json = new JSONObject();
				json.put("userId", tm.getUserId());
				json.put("dataMstId", tm.getId());

				boolean rowFlag = true;
				String uniqueHeaderValue = "";
				for (int i = 0; i < line.length; i++) {
					Header key = map.get(i);
					if (key != null) {
						String cellValue = line[i];
						if (key.getNotNull() && (cellValue == null || cellValue.trim().isEmpty())) {
							rowFlag = false;
							// break;
						}

						if (key.getUniqueKey()) {
							String headerName = key.getName();
							uniqueValues = uniqueValuesMap.get(headerName);

							if (uniqueValues == null) {
								uniqueValues = new HashSet<>();
								uniqueValuesMap.put(headerName, uniqueValues);
							}

							if (!uniqueValues.contains(cellValue)) {
								uniqueValues.add(cellValue);
								uniqueHeaderValue = cellValue;
							}
						}

						String validationType = key.getValidationType();
						cellValue = validationData(validationType, cellValue);

						System.out.println(cellValue);

						json.put(key.getName(), cellValue);

					}

				}

				if (rowFlag) {
					inputHash.put(uniqueHeaderValue, json);
					count++;

				}	
			}
			
		} catch (IOException e) {
			System.out.println("Exception: " + e);
		}

		return inputHash;
	}

	public String readHeader(String[] header, HashMap<Integer, Header> headerMap) {
		String uniqueHeader = "";
		List<Header> headerList = tm.getHeaderList();
		List<Map<String, String>> headerMaping = tm.getHeaderMaping();
		if (header != null) {
			for (int i = 0; i < header.length; i++) {
				String trimmedHeader = header[i].trim();
				String headers = common.removeSpacesAndSpecialCharacters(trimmedHeader);
				for (Map<String, String> map : headerMaping) {
					if (map.containsKey(headers)) {
						String actualHeader = map.get(headers);
						for (Header headerObj : headerList) {
							if (headerObj.getName().equals(actualHeader)) {
								if (headerObj.getUniqueKey()) {
									uniqueHeader = headerObj.getName();
								}
								headerMap.put(i, headerObj);
							}
						}
					}
				}
			}
		}
		return uniqueHeader;
	}

	public HashMap<String, JSONObject> readXlsxFile(String filePath) {
		HashMap<String, JSONObject> inputHash = new HashMap<>();
		boolean rowFlag = true;
		Set<String> uniqueValues = null;		
		long totalcnt = 0;
		String uniqueHeaderValue = "";
		HashMap<String, Set<String>> uniqueValuesMap = new HashMap<>();

		  try (InputStream fileInputStream = new FileInputStream(new File(filePath));
			         Workbook workbook = StreamingReader.builder().rowCacheSize(100).bufferSize(1024).open(fileInputStream)) {

			Sheet sheet = workbook.getSheetAt(0); // Assuming you want to read the first sheet

			// Get the header row and create a mapping of column index to header name
			Row headerRow = sheet.iterator().next();
			HashMap<Integer, Header> headerMap = new HashMap<>();
			String uniqueHeader = "";

			uniqueHeader = readHeader(headerRow, headerMap);

			// Read each row
			Iterator<Row> rowIterator = sheet.iterator();
			rowIterator.next(); // Skip the header row
		 	int rowCount = 0;
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if (row != null) {
					rowFlag = true;
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("userId", tm.getUserId());
					jsonObject.put("dataMstId", tm.getDataMstId());

					for (Map.Entry<Integer, Header> entry : headerMap.entrySet()) {
						int index = entry.getKey();
						Header header = entry.getValue();
						Cell cell = row.getCell(index);

						String cellValue = getCellValueAsString(cell);

						if (header.getNotNull() && (cellValue == null || cellValue.trim().equalsIgnoreCase(""))) {
							rowFlag = false;
							break;
						}

						if (header.getUniqueKey()) {
							String headerName = header.getName();
							uniqueValues = uniqueValuesMap.get(headerName);

							if (uniqueValues == null) {
								uniqueValues = new HashSet<>();
								uniqueValuesMap.put(headerName, uniqueValues);
							}

							if (!uniqueValues.contains(cellValue)) {
								uniqueValues.add(cellValue);
								uniqueHeaderValue = cellValue;
							}
						}

						String validationType = header.getValidationType();
						cellValue = validationData(validationType, cellValue);

						jsonObject.put(header.getName(), cellValue);

					}
					if (rowFlag) {
						inputHash.put(uniqueHeaderValue, jsonObject);
						rowCount++;
						totalcnt++;

					}
				}

			}
			System.out.println("Finish reading Total row count xlsx TempData: " + totalcnt);

			workbook.close();
			fileInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return inputHash;
	}

	public String readHeader(Row headerRow, HashMap<Integer, Header> headerMap) {
		String uniqueHeader = "";
		List<Header> headerList = tm.getHeaderList();
		List<Map<String, String>> headerMaping = tm.getHeaderMaping();
		if (headerRow != null) {
			int cellCount = headerRow.getLastCellNum();
			for (int i = 0; i < cellCount; i++) {
				Cell cell = headerRow.getCell(i);
				if (cell != null) {
					String header = getCellValueAsString(cell);
					header = common.removeSpacesAndSpecialCharacters(header.trim());
					for (Map<String, String> map : headerMaping) {
						if (map.containsKey(header)) {
							String actualHeader = map.get(header);
							for (Header headerObj : headerList) {
								if (headerObj.getName().equals(actualHeader)) {
									if (headerObj.getUniqueKey()) {
										uniqueHeader = headerObj.getName();
									}
									headerMap.put(i, headerObj);
								}
							}
						}
					}

				}
			}
		}
		return uniqueHeader;
	}

	public String validationData(String name, String cellValue) {
		ValidationMaster vdm = new ValidationMaster();
		vdm.setName(name);
		String value = cellValue;
		if ("mobile".equalsIgnoreCase(vdm.getName())) {
			value = common.mobileValidation(value);
			if (value == null || value.equalsIgnoreCase("")) {
				value = "-";
			}
		} else if ("email".equalsIgnoreCase(vdm.getName())) {
			value = common.emailValidation(value);
			if (value == null || value.equalsIgnoreCase("")) {
				value = "-";
			}
		} else if ("pincode".equalsIgnoreCase(vdm.getName())) {
			value = common.pincodeValidation(value);
			if (value == null || value.equalsIgnoreCase("")) {
				value = "-";
			}
		}

		return value;
	}

	private void insertRecords(BulkRequest bulkRequest) {
		try {
			// Execute the bulk request
			BulkResponse bulkResponse = resthighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

			// Process the response
			if (bulkResponse.hasFailures()) {
				// Handle failures
				System.err.println("Bulk insert operation encountered failures:");
			} else {
				// Handle successful insertion
				System.out.println("Bulk insert operation completed successfully.");
			}
			
		} catch (IOException e) {
			// Handle exception
			e.printStackTrace();
		}
	}

	public String getCellValueAsString(Cell cell) {
		DataFormatter dataFormatter = new DataFormatter();
		return dataFormatter.formatCellValue(cell).trim();
	}

	public String updateFilterDataMaster(TempData tm, String id) throws IOException {
		tm.setId("");
		UpdateRequest<TempData, TempData> updateRequest = UpdateRequest
				.of(req -> req.index("temp_data_mst").id(id).doc(tm));
		UpdateResponse<TempData> response = elasticsearchClient.update(updateRequest, TempData.class);
		return response.result().toString();
	}

	private List<Query> prepareQueryList(TempData tmd) {

		Map<String, String> conditionMap = new HashMap<>();
		if (tmd.getUserId() != null && !tmd.getUserId().equalsIgnoreCase(""))
			conditionMap.put("userId.keyword", tmd.getUserId());

		if (tmd.getDataMstId() != null && !tmd.getDataMstId().equalsIgnoreCase(""))
			conditionMap.put("dataMstId.keyword", tmd.getDataMstId());

		return conditionMap.entrySet().stream().filter(entry -> !ObjectUtils.isEmpty(entry.getValue()))
				.map(entry -> QueryBuilderUtils.termQuery(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

	public int getUniqueSearchData(String dataMstId, int startIndex, String uniqueHeader,
			HashMap<String, JSONObject> inputHash, String userId) throws ElasticsearchException, IOException {
		HashMap<String, String> resultMap = new HashMap<>();
		int cnt = 0;
		TempData tmp = new TempData();
		tmp.setDataMstId(dataMstId);
		tmp.setUserId(userId);

		List<Query> query1 = prepareQueryList(tmp);

		SearchResponse<Object> search = elasticsearchClient.search(req -> req.index("detail_data_mst")
				.query(query -> query.bool(bool -> bool.must(query1))).from(startIndex).size(500), Object.class);

		List<Hit<Object>> detailDataList = search.hits().hits();
		
		for (Hit<Object> hit : detailDataList) {
			cnt++;
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			String jsonString = objectMapper.writeValueAsString(hit.source());
			JSONObject json = new JSONObject(jsonString);
			String uniqueHeaderValue = json.getString(uniqueHeader);
			JSONObject removedValue = inputHash.remove(uniqueHeaderValue);
			if (removedValue != null) {
				System.out.println("Removed value: " + uniqueHeaderValue);
			}
		
		}
		
		
		return cnt;
	}

}
