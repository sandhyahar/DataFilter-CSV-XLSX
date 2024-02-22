package com.API.Events;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.elasticsearch.client.RestHighLevelClient;
import org.json.JSONArray;
import org.json.JSONObject;
import com.API.Common.common;
import com.API.Service.ParamMstService;
import com.API.pojo.DataMaster;
import com.API.pojo.FilterCondition;
import com.API.pojo.FilterDataMaster;
import com.API.pojo.ParamMst;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ProcessDownloadDataThread implements Runnable {

	FilterDataMaster fdm;

	RestHighLevelClient resthighLevelClient;

	ElasticsearchClient elasticsearchClient;

	ParamMstService paramMstService;

	String zipFilePath = "";

	public ProcessDownloadDataThread(FilterDataMaster fdm, ElasticsearchClient elasticsearchClient,
			RestHighLevelClient resthighLevelClient, ParamMstService paramMstService) {
		this.elasticsearchClient = elasticsearchClient;
		this.resthighLevelClient = resthighLevelClient;
		this.paramMstService = paramMstService;
		this.fdm = fdm;
	}

	@Override
	public void run() {

		if (fdm != null) {
			int starIndex = 0;
			int size = 500;
			int recordLimit = 500000;
			List<JSONObject> jsonObjectList = new ArrayList<>();

			// Retrieve the paramValue for "downloadPath"
			List<ParamMst> downloadPathParamMsts = paramMstService.findByParamName("downloadPath");
			if (downloadPathParamMsts != null) {
				String downloadPathParamValue = downloadPathParamMsts.get(0).getParamValue();

				// Generate the file name based on fdm.userId and current time
				String fileName = fdm.getUserId() + "_" + System.currentTimeMillis() + ".xlsx";
				String filePath = downloadPathParamValue + fileName;

				// Fetch records iteratively until all records are retrieved
				while (true) {
					List<JSONObject> batchList = fetchDataFromElasticsearch(starIndex, size);
					jsonObjectList.addAll(batchList);
					if (batchList.size() < size) {
						break;
					}
					starIndex += size;
				}

				System.out.println("file start time: " + new java.util.Date());
				if (jsonObjectList.size() > 0) {
					writeJSONObjectsToExcel(jsonObjectList, filePath, recordLimit);
					System.out.println("file end time: " + new java.util.Date());
				}

				fdm.setStatus(3);
				LocalDateTime currentDateTime = LocalDateTime.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
				String formattedDateTime = currentDateTime.format(formatter);
				fdm.setEndDateTime(formattedDateTime);

				long totalCount = jsonObjectList.size(); 
				System.out.println("Total count: " + totalCount);

				DataMaster dm = new DataMaster();
				dm.setTotal(totalCount);
				dm.setStatus(3);

				try {
					updateFilterDataMaster(dm, fdm.getDataMstId());
				} catch (IOException e) {
					e.printStackTrace();
				}

				List<ParamMst> applicationUrlParamMsts = paramMstService.findByParamName("applicationUrl");
				if (applicationUrlParamMsts != null) {
					String applicationUrlParamValue = applicationUrlParamMsts.get(0).getParamValue();
					// Retrieve only the file name from the zipFilePath
					String zipFileName = new File(zipFilePath).getName();
					filePath = applicationUrlParamValue + "downloadFile/" + zipFileName;
					fdm.setFilePath(filePath);
				}
				try {
					updateFilterDataMaster(fdm, fdm.getId());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
	}

	public String updateFilterDataMaster(DataMaster dm, String id) throws IOException {
		dm.setId("");
		UpdateRequest<DataMaster, DataMaster> updateRequest = UpdateRequest
				.of(req -> req.index("data_mst").id(id).doc(dm));
		UpdateResponse<DataMaster> response = elasticsearchClient.update(updateRequest, DataMaster.class);
		return response.result().toString();
	}

	private List<JSONObject> fetchDataFromElasticsearch(int starIndex, int size) {

		List<FilterCondition> filterList = new FilterDataMaster().getFilterList();
		List<Query> queryList = common.prepareQueryList(fdm);

		List<JSONObject> jsonObjectList = new ArrayList<>();
		try {
			SearchResponse<Object> search = elasticsearchClient.search(req -> req.index("detail_data_mst")
					.query(query -> query.bool(bool -> bool.must(queryList))).from(starIndex).size(size), Object.class);

			List<Hit<Object>> detailDataList = search.hits().hits();

			JSONArray jsonArray = new JSONArray();

			for (Hit<Object> hit : detailDataList) {
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
				String jsonString = objectMapper.writeValueAsString(hit.source());
				JSONObject json = new JSONObject(jsonString);
				jsonObjectList.add(json);
				jsonArray.put(json);
			}

			// System.out.println(jsonObjectList.toString());
			long totalCount = search.hits().total().value();
			System.out.println("Total count: " + totalCount);

		} catch (ElasticsearchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return jsonObjectList;
	}

	public void writeJSONObjectsToExcel(List<JSONObject> jsonObjectList, String filePath, int recordLimit) {
		try {
			int fileCount = 1;
			int recordCount = 0;
			FileOutputStream outputStream = null;
			Workbook workbook = null;
			Sheet sheet = null;

			Set<String> columnSet = new LinkedHashSet<>();

			for (JSONObject jsonObject : jsonObjectList) {
				if (recordCount == 0) {
					if (outputStream != null) {
						workbook.write(outputStream);
						outputStream.close();
					}
					outputStream = new FileOutputStream(filePath + "_" + fileCount + ".xlsx");
					workbook = new XSSFWorkbook();
					sheet = workbook.createSheet("Data");

					// Determine the order of columns based on the keys present in the JSON objects
					columnSet.clear();
					columnSet.addAll(jsonObject.keySet());

					// Exclude the columns you want to exclude
					columnSet.remove("dataMstId");
					columnSet.remove("userId");
					columnSet.remove("insert_time");

					// Create header row
					Row headerRow = sheet.createRow(0);
					int columnIndex = 0;
					for (String column : columnSet) {
						Cell cell = headerRow.createCell(columnIndex);
						cell.setCellValue(column);
						columnIndex++;
					}

					fileCount++;
				}

				// Write data rows
				Row dataRow = sheet.createRow(recordCount + 1);
				int columnIndex = 0;
				for (String column : columnSet) {
					Cell cell = dataRow.createCell(columnIndex);
					Object value = jsonObject.opt(column);
					cell.setCellValue(value != null ? value.toString() : "");
					columnIndex++;
				}

				recordCount++;

				if (recordCount == recordLimit) {
					recordCount = 0;
				}
			}

			if (outputStream != null) {
				workbook.write(outputStream);
				outputStream.close();
			}

			System.out.println("Files written successfully.");

			// Zip the generated files
			List<String> filesToZip = new ArrayList<>();
			for (int i = 1; i < fileCount; i++) {
				filesToZip.add(filePath + "_" + i + ".xlsx");
			}
			zipFiles(filesToZip, filePath);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void zipFiles(List<String> files, String zfilepath) {
		FileOutputStream fos = null;
		ZipOutputStream zipOut = null;
		FileInputStream fis = null;
		try {
			try {
				zipFilePath = zfilepath.replace(".xlsx", "") + ".zip";
				fos = new FileOutputStream(zipFilePath);
				zipOut = new ZipOutputStream(new BufferedOutputStream(fos));

				for (String filePath : files) {
					File input = new File(filePath);
					fis = new FileInputStream(input);
					ZipEntry ze = new ZipEntry(input.getName());
					zipOut.putNextEntry(ze);
					byte[] tmp = new byte[4096];
					int size;
					while ((size = fis.read(tmp)) != -1) {
						zipOut.write(tmp, 0, size);
					}
					zipOut.closeEntry();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fis != null) {
					fis.close();
				}
			}

			zipOut.close();
			System.out.println("Zipped the files successfully...");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (zipOut != null) {
					zipOut.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (Exception ex) {
				System.out.println(ex);
			}
		}
	}

	public String updateFilterDataMaster(FilterDataMaster fdm, String id) throws IOException {
		fdm.setId("");
		UpdateRequest<FilterDataMaster, FilterDataMaster> updateRequest = UpdateRequest
				.of(req -> req.index("download_data_mst").id(id).doc(fdm));
		UpdateResponse<FilterDataMaster> response = elasticsearchClient.update(updateRequest, FilterDataMaster.class);
		return response.result().toString();
	}

//	public void getTotalCount() {
//
//		String indexName = "detail_data_mst"; // Replace with the name of your index
//
//		org.elasticsearch.common.settings.Settings indexSettings = org.elasticsearch.common.settings.Settings.builder().put("index.max_result_window", 2147483647) // Set the desired
//				.build();
//
//		UpdateSettingsRequest updateSettingsRequest = new UpdateSettingsRequest(indexName)
//			    .settings(indexSettings);
//		try {
//			resthighLevelClient.indices().putSettings(updateSettingsRequest,org.elasticsearch.client.RequestOptions.DEFAULT);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		org.elasticsearch.action.search.SearchRequest searchRequest = new org.elasticsearch.action.search.SearchRequest(
//				indexName);
//		org.elasticsearch.search.builder.SearchSourceBuilder searchSourceBuilder = new org.elasticsearch.search.builder.SearchSourceBuilder();
//		searchSourceBuilder
//				.query(org.elasticsearch.index.query.QueryBuilders.matchQuery("dataMstId", fdm.getDataMstId()));
//		searchSourceBuilder.query(org.elasticsearch.index.query.QueryBuilders.matchQuery("userId", fdm.getUserId()));
//
//		searchSourceBuilder.size(0); // Set size to 0 to only get the count, not the documents
//		searchRequest.source(searchSourceBuilder);
//		try {
//			org.elasticsearch.action.search.SearchResponse searchResponse = resthighLevelClient.search(searchRequest,org.elasticsearch.client.RequestOptions.DEFAULT);
//
//			long totalCount = searchResponse.getHits().getTotalHits().value;
//			System.out.println("Total documents matching the search conditions: " + totalCount);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}
