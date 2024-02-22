
package com.API.Events;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.json.JSONObject;

import com.API.Common.common;
import com.API.pojo.DataMaster;
import com.API.pojo.FilterDataMaster;
import com.API.pojo.Header;
import com.API.pojo.ValidationMaster;
import com.ibm.icu.text.SimpleDateFormat;
import com.monitorjbl.xlsx.StreamingReader;
import com.opencsv.CSVReader;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;

public class DataMastProcessThread implements Runnable {

	DataMaster dm;

	RestHighLevelClient resthighLevelClient;

	ElasticsearchClient elasticsearchClient;
	
	long totalcnt = 0;

	public DataMastProcessThread(DataMaster dataMaster, ElasticsearchClient elasticsearchClient,
			RestHighLevelClient resthighLevelClient) {
		this.elasticsearchClient = elasticsearchClient;
		this.resthighLevelClient = resthighLevelClient;
		this.dm = dataMaster;
	}

	@Override
	public void run() {
		if (dm != null) {
			HashMap<String, String> inputHash = readFile(dm.getFilePath());
			dm.setTotal(totalcnt);
			dm.setStatus(3);
			try {
				updateFilterDataMaster(dm, dm.getId());
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

	public HashMap<String, String> readFile(String fileName) {
		if (fileName.endsWith(".xlsx")) {
			return readXlsxFile(fileName);
		} else if (fileName.endsWith(".csv")) {
			return readCsvFile(fileName);
		} else {
			System.out.println("Unsupported file format");
			return new HashMap<>(); // Return an empty HashMap
		}
	}

	public HashMap<String, String> readCsvFile(String fileName) {
		HashMap<String, String> inputHash = new HashMap<>();
		HashMap<String, Set<String>> uniqueValuesMap = new HashMap<>();

		try (CSVReader csvr = new CSVReader(new FileReader(fileName))) {
			String[] header = csvr.readNext(); // Read the header line

			if (header == null) {
				System.out.println("Empty or missing header in the CSV file.");
				return inputHash;
			}

			HashMap<Integer, Header> map = new HashMap<>();
			readHeader(header, map); // Process the header line and store the header mapping

			if (map.isEmpty()) {
				System.out.println("Header mapping is empty. Please check the Header list in dm.getHeaderList().");
				return inputHash;
			}

			int count = 0; // Counter to keep track of the number of records
			BulkRequest bulkRequest = new BulkRequest();

			String[] line;
			while ((line = csvr.readNext()) != null) {
				JSONObject json = new JSONObject();
				json.put("userId", dm.getUserId());
				json.put("dataMstId", dm.getId());
				json.put("insert_time", dm.getInsert_time());

				boolean rowFlag = true;

				for (int i = 0; i < line.length; i++) {
					Header key = map.get(i);
					if (key != null) {
						String cellValue = line[i];
						if (key.getNotNull() && (cellValue == null || cellValue.trim().isEmpty())) {
							rowFlag = false;
							// break;
						}

						String validationType = key.getValidationType();
						cellValue = validationData(validationType, cellValue);

						System.out.println(cellValue);

						if (key.getUniqueKey()) {
							String headerName = key.getName();
							Set<String> uniqueValues = uniqueValuesMap.get(headerName);
							// If the uniqueValues Set is not present in the map, create a new HashSet and
							// put it in the map
							if (uniqueValues == null) {
								uniqueValues = new HashSet<>();
								uniqueValuesMap.put(headerName, uniqueValues);
							}

							// Check if the cellValue already exists in the uniqueValues Set
							if (!uniqueValues.add(cellValue)) {
								rowFlag = false;
								break;
							}
						}

//						if (cellValue.trim().equals("-")) {
//		                    rowFlag = false;
//		                    break;
//		                }

						json.put(key.getName(), cellValue);

					}

				}

				if (rowFlag) {
					bulkRequest.add(new IndexRequest("detail_data_mst").source(json.toString(), XContentType.JSON));
					count++;
					totalcnt++;
				}
				


				if (count > 500) {
					insertRecords(bulkRequest);
					bulkRequest = new BulkRequest();
					count = 0;
				}
			}

			if (bulkRequest.numberOfActions() > 0) {
				insertRecords(bulkRequest);
			}

			System.out.println("Finish reading Total row count CSV: " + totalcnt);

			
		} catch (IOException e) {
			System.out.println("Exception: " + e);
		}

		return inputHash;
	}

	public void readHeader(String[] header, HashMap<Integer, Header> headerMap) {
		if (header != null) {
			for (int i = 0; i < header.length; i++) {
				String trimmedHeader = header[i].trim();
				String headers = common.removeSpacesAndSpecialCharacters(trimmedHeader);
				List<Header> headerList = dm.getHeaderList();
				for (Header headerObj : headerList) {
					if (headerObj.getName().equals(headers)) {
						headerMap.put(i, headerObj);
					}
				}
			}
		}
	}

	public HashMap<String, String> readXlsxFile(String filePath) {
		HashMap<String, String> inputHash = new HashMap<>();
		boolean rowFlag = true;
		HashMap<String, Set<String>> uniqueValuesMap = new HashMap<>();

		try {
			InputStream fileInputStream = new FileInputStream(new File(filePath));
			Workbook workbook = StreamingReader.builder().rowCacheSize(100) // number of rows to keep in memor //
					.bufferSize(1024) // buffer size to use when reading InputStream to file (defaults to 1024)
					.open(fileInputStream);

			Sheet sheet = workbook.getSheetAt(0); // Assuming you want to read the first sheet

			// Get the header row and create a mapping of column index to header name
			Row headerRow = sheet.iterator().next();
			HashMap<Integer, Header> headerMap = new HashMap<>();
			readHeader(headerRow, headerMap);

			BulkRequest bulkRequest = new BulkRequest();

			// Read each row
			Iterator<Row> rowIterator = sheet.iterator();
			// rowIterator.next(); // Skip the header row
			int rowCount = 0;
			
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if (row != null) {
					rowFlag = true;
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("userId", dm.getUserId());
					jsonObject.put("dataMstId", dm.getId());

					for (Map.Entry<Integer, Header> entry : headerMap.entrySet()) {
						int index = entry.getKey();
						Header header = entry.getValue();
						Cell cell = row.getCell(index);

						String cellValue = getCellValueAsString(cell);

						 if (cell != null) {
							// Apply conversion for numeric cells in scientific notation
							if (cell.getCellType() == CellType.NUMERIC) {
								if (DateUtil.isCellDateFormatted(cell)) {
									Date dateValue = cell.getDateCellValue();
									SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
									cellValue = dateFormat.format(dateValue);
								} else {
									double numericValue = cell.getNumericCellValue();
									long originalValue = (long) numericValue;
									cellValue = String.valueOf(originalValue);
								}
							}
						}

						if (header.getNotNull() && (cellValue == null || cellValue.trim().equalsIgnoreCase(""))) {
							rowFlag = false;
							break;
						}

						if (header.getUniqueKey()) {
							String headerName = header.getName();
							Set<String> uniqueValues = uniqueValuesMap.get(headerName);

							if (uniqueValues == null) {
								uniqueValues = new HashSet<>();
								uniqueValuesMap.put(headerName, uniqueValues);
							}

							if (!uniqueValues.add(cellValue)) {
								rowFlag = false;
								break;
							}
						}

						String validationType = header.getValidationType();
						cellValue = validationData(validationType, cellValue);

						jsonObject.put(header.getName(), cellValue);

					}
					if (rowFlag) {
						bulkRequest.add(
								new IndexRequest("detail_data_mst").source(jsonObject.toString(), XContentType.JSON));
						rowCount++;
						totalcnt++;

					}
				}

				if (rowCount > 500) {
					insertRecords(bulkRequest);
					bulkRequest = new BulkRequest();
					rowCount = 0;
				}
			}

			if (bulkRequest.numberOfActions() > 0) {
				insertRecords(bulkRequest);

			}
			System.out.println("Finish reading Total row count Xlsx: " + totalcnt);

			workbook.close();
			fileInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return inputHash;
	}

	public String validationData(String name, String cellValue) {
		ValidationMaster vdm = new ValidationMaster();
		vdm.setName(name);

		if ("mobile".equalsIgnoreCase(vdm.getName())) {
			cellValue = common.mobileValidation(cellValue);
			if (cellValue == null || cellValue.equalsIgnoreCase("")) {
				cellValue = "-";
			}
		} else if ("email".equalsIgnoreCase(vdm.getName())) {
			cellValue = common.emailValidation(cellValue);
			if (cellValue == null || cellValue.equalsIgnoreCase("")) {
				cellValue = "-";
			}
		} else if ("pincode".equalsIgnoreCase(vdm.getName())) {
			cellValue = common.pincodeValidation(cellValue);
			if (cellValue == null || cellValue.equalsIgnoreCase("")) {
				cellValue = "-";
			}
		}

		return cellValue;
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

	public void readHeader(Row headerRow, HashMap<Integer, Header> headerMap) {
		if (headerRow != null) {
			int cellCount = headerRow.getLastCellNum();
			for (int i = 0; i < cellCount; i++) {
				Cell cell = headerRow.getCell(i);
				if (cell != null) {
					String header = getCellValueAsString(cell);
					header = common.removeSpacesAndSpecialCharacters(header.trim());
					List<Header> headerList = dm.getHeaderList();
					for (Header headerObj : headerList) {
						if (headerObj.getName().equals(header)) {
							headerMap.put(i, headerObj);

						}
					}
				}
			}
		}
	}

	public String getCellValueAsString(Cell cell) {
		DataFormatter dataFormatter = new DataFormatter();
		return dataFormatter.formatCellValue(cell).trim();
	}

	public String updateFilterDataMaster(DataMaster dm, String id) throws IOException {
		dm.setId("");
		UpdateRequest<DataMaster, DataMaster> updateRequest = UpdateRequest
				.of(req -> req.index("data_mst").id(id).doc(dm));
		UpdateResponse<DataMaster> response = elasticsearchClient.update(updateRequest, DataMaster.class);
		return response.result().toString();
	}

}
