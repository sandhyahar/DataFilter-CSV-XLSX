package com.API.RestController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.API.Common.QueryBuilderUtils;
import com.API.Common.common;
import com.API.Service.ParamMstService;
import com.API.pojo.CampaignMst;
import com.API.pojo.DataMaster;
import com.API.pojo.FileUpload;
import com.API.pojo.FilterDataMaster;
import com.API.pojo.Header;
import com.API.pojo.ParamMst;
import com.API.pojo.TempData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ibm.icu.text.SimpleDateFormat;
import com.monitorjbl.xlsx.StreamingReader;
import com.opencsv.CSVReader;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;

import co.elastic.clients.elasticsearch.core.search.Hit;

@RestController
public class ManageDataController {

	@Autowired
	ParamMstService paramMstService;

	@Autowired
	RestHighLevelClient resthighLevelClient;

	@Autowired
	ElasticsearchClient elasticsearchClient;

	@PostMapping("/upload")
	public String uploadFile(@RequestParam MultipartFile file) {
		JSONObject response = new JSONObject();
		String filePath = "";

		if (file == null) {
			response.put("message", "File is empty");
			return response.toString();
		}

		try {
			String fileName = file.getOriginalFilename();

			if (fileName == null) {
				response.put("message", "File Name is empty");
				return response.toString();
			}

			String fileExtension = getFileExtension(fileName);

			if (fileExtension != null && (fileExtension.equals("csv") || fileExtension.equals("xlsx"))) {
				if (fileExtension.equals("csv")) {
					try (CSVReader csvr = new CSVReader(new InputStreamReader(file.getInputStream()), ',')) {
						List<String[]> recordList = (List<String[]>) csvr.readAll();
						boolean isFirstLine = true;
						List<String> headers = new ArrayList<>();
						List<Map<String, String>> sampleRecords = new ArrayList<>();

						for (String[] values : recordList) {
							if (isFirstLine) {
								for (int i = 0; i < values.length; i++) {
									headers.add(common.removeSpacesAndSpecialCharacters(values[i]));
								}
								isFirstLine = false;
							} else {
								try {
									Map<String, String> record = new HashMap<>();
									for (int i = 0; i < values.length && i < headers.size(); i++) {
										record.put(headers.get(i), values[i]);
									}
									sampleRecords.add(record);

								} catch (Exception e) {
									System.out.println(e);
								}

							}
							if (sampleRecords.size() > 10) {
								break;
							}
						}

						// Save the file to the specified path
						String paramName = "datafilePath";
						List<ParamMst> paramMsts = paramMstService.findByParamName(paramName);
						if (paramMsts != null) {
							String paramValue = paramMsts.get(0).getParamValue();
							filePath = paramValue + "/" + System.currentTimeMillis() + "_" + fileName;
						}
						String filePaths = filePath;
						File destFile = new File(filePaths);
						file.transferTo(destFile);

						FileUpload fileUpload = new FileUpload();
						fileUpload.setHeaders(headers);
						fileUpload.setSampleRecords(sampleRecords);
						fileUpload.setFilepath(filePaths);
						response = createResponse(fileUpload, "File Upload Successfully");
					}
					return response.toString();

				} else if (fileExtension.equals("xlsx")) {
					try (InputStream fileInputStream = file.getInputStream()) {
						Workbook workbook = StreamingReader.builder().rowCacheSize(100).bufferSize(1024)
								.open(fileInputStream);

						Sheet sheet = workbook.getSheetAt(0);
						DataFormatter dataFormatter = new DataFormatter();

						List<String> headers = new ArrayList<>();
						List<Map<String, String>> sampleRecords = new ArrayList<>();
						int totalRecords = 0;

						for (Row row : sheet) {
							Map<String, String> recordMap = new LinkedHashMap<>();

							for (int i = 0; i < row.getLastCellNum(); i++) {
								Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
								String cellValue = dataFormatter.formatCellValue(cell);

								if (row.getRowNum() == 0) {
									headers.add(common.removeSpacesAndSpecialCharacters(cellValue));
								} else {
									if (cell != null) {
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
									recordMap.put(headers.get(i), cellValue);
								}
							}

							if (row.getRowNum() > 0 && sampleRecords.size() < 10) {
								sampleRecords.add(recordMap);
								totalRecords++;
							}
						}

						// Save the file to the specified path
						String paramName = "datafilePath";
						List<ParamMst> paramMsts = paramMstService.findByParamName(paramName);
						if (paramMsts != null) {
							String paramValue = paramMsts.get(0).getParamValue();
							filePath = paramValue + "/" + System.currentTimeMillis() + "_" + fileName;
						}
						String filePaths = filePath;
						File destFile = new File(filePaths);
						file.transferTo(destFile);

						FileUpload fileUpload = new FileUpload();
						fileUpload.setHeaders(headers);
						fileUpload.setSampleRecords(sampleRecords);
						// fileUpload.setTotal(totalRecords);
						fileUpload.setFilepath(filePaths);
						response = createResponse(fileUpload, "File Upload Successfully");
						return response.toString();
					} catch (Exception e) {
						e.printStackTrace();
						response.put("message", "Error processing the file");
						return response.toString();
					}
				}
			} else {
				response.put("message", "Invalid file format");
				return response.toString();
			}

		} catch (IOException e) {
			response.put("message", "Error reading the file");
			return response.toString();
		} catch (Exception e) {
			response.put("message", "Error processing the file");
			return response.toString();
		}

		return null;
	}

	private String getFileExtension(String fileName) {
		if (fileName != null && fileName.lastIndexOf(".") != -1) {
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		}
		return null;
	}

	private JSONObject createResponse(FileUpload fileUpload, String message) {
		JSONObject response = new JSONObject();
		response.put("headers", fileUpload.getHeaders());
		response.put("sampleRecords", fileUpload.getSampleRecords());
		response.put("filepath", fileUpload.getFilepath());
		response.put("totalRecords", fileUpload.getTotal());
		response.put("message", message);
		return response;

	}

	@PostMapping("/insertDataMaster")
	public String insertDataMaster(@RequestBody DataMaster dataMaster) {
		JSONObject res = new JSONObject();
		try {
			// Set default values
			dataMaster.setStatus(1);
			DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
			LocalDateTime currentDateTime = LocalDateTime.now();
			String formattedDateTime = currentDateTime.format(formatter);
			dataMaster.setInsert_time(formattedDateTime);
			dataMaster.getDataName();
			dataMaster.getUserId();
			dataMaster.getHeaderList();
			dataMaster.getFilePath();
			// dataMaster.getTotal();

			List<Header> headers = dataMaster.getHeaderList();

			if (headers != null) {
				dataMaster.setHeaderList(headers);
			}

			IndexRequest<DataMaster> indexRequest = new IndexRequest.Builder<DataMaster>().index("data_mst")
					.document(dataMaster).build();

			IndexResponse indexResponse = elasticsearchClient.index(indexRequest);

			if (indexResponse.result() == Result.Created) {
				boolean flag = true;
				res.put("message", "Campaign data upload successfully.");
				res.put("flag", flag);
			} else {
				// Indexing failed
				res.put("message", "Campaign submission failed.");
			}

		} catch (IOException e) {
			// Handle the exception
			res.put("message", "Error processing the request.");
		}

		return res.toString();
	}

	@PostMapping("/checkUniqueDataName")
	public String getUniqueDataName(@RequestBody DataMaster data, String userId, String id) throws IOException {
		JSONObject res = new JSONObject();
		boolean flag;

		DataMaster dm = new DataMaster();
		dm.setUserId(userId);
		dm.setId(id);

		// Check if userId already exists
		SearchResponse<DataMaster> userIdSearch = elasticsearchClient.search(
				s -> s.index("data_mst").query(q -> q.match(m -> m.field("dataName").query(data.getDataName()))),
				DataMaster.class);

		if (userIdSearch.hits().hits().size() > 0) {
			flag = false;
			res.put("message", "DataName already exists.");
		} else {
			flag = true;
			res.put("message", "You can use this DataName.");
		}

		res.put("flag", flag);
		return res.toString();
	}

	private List<Query> prepareQueryList(DataMaster dm) {
		Map<String, String> conditionMap = new HashMap<>();
		if (dm.getUserId() != null && !dm.getUserId().equalsIgnoreCase(""))
			conditionMap.put("userId.keyword", dm.getUserId());

		if (dm.getId() != null && !dm.getId().equalsIgnoreCase(""))
			conditionMap.put("_id", dm.getId());

		return conditionMap.entrySet().stream().filter(entry -> !ObjectUtils.isEmpty(entry.getValue()))
				.map(entry -> QueryBuilderUtils.termQuery(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

	@GetMapping("/findfiledata")
	public String getFileData(String userId, String insert_time) {
		JSONObject res = new JSONObject();
		try {
			DataMaster dm = new DataMaster();
			dm.setUserId(userId);
			dm.setInsert_time(insert_time);

			org.elasticsearch.index.query.BoolQueryBuilder boolQuery = org.elasticsearch.index.query.QueryBuilders
					.boolQuery();

			// Add the range condition for startDateTime
			boolQuery.must(org.elasticsearch.index.query.QueryBuilders.rangeQuery("insert_time").gte(insert_time)
					.lte(insert_time));

			// Add the term condition for userId
			boolQuery.must(org.elasticsearch.index.query.QueryBuilders.termQuery("userId", userId));

			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().query(boolQuery).size(2147483647); // Set the

			org.elasticsearch.action.search.SearchRequest searchRequest = new org.elasticsearch.action.search.SearchRequest(
					"data_mst");
			searchRequest.source(sourceBuilder);

			org.elasticsearch.action.search.SearchResponse searchResponse = resthighLevelClient.search(searchRequest,
					org.elasticsearch.client.RequestOptions.DEFAULT);

			SearchHits searchdata = searchResponse.getHits();
			ObjectMapper objectMapper = new ObjectMapper();
			JSONArray jsonArray = new JSONArray();

			for (SearchHit hit : searchdata.getHits()) {
				// Perform type casting to access specific fields
				String id = hit.getId();
				String sourceAsString = hit.getSourceAsString();

				DataMaster campaign = objectMapper.readValue(sourceAsString, DataMaster.class);
				campaign.setId(id);

				JSONObject dataJson = new JSONObject();
				dataJson.put("dataName", campaign.getDataName());
				dataJson.put("headerList", campaign.getHeaderList());
				dataJson.put("insert_time", campaign.getInsert_time());
				dataJson.put("total", campaign.getTotal());
				dataJson.put("id", campaign.getId());
				dataJson.put("status", campaign.getStatus());

				jsonArray.put(dataJson);
			}

			res.put("list", jsonArray);

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		return res.toString();
	}

	@PostMapping("/filterdata")
	public String filterData(@RequestBody FilterDataMaster filterDataMaster) {
		JSONObject response = new JSONObject();
		try {

			DataMaster dm = new DataMaster();
			int count = 0;

			List<Query> queryList = common.prepareQueryList(filterDataMaster);

			SearchResponse<Object> search = elasticsearchClient.search(req -> req.index("detail_data_mst")
					.query(query -> query.bool(bool -> bool.must(queryList))).from(0).size(100), Object.class);

			List<Hit<Object>> detailDataList = search.hits().hits();

			JSONArray jsonArray = new JSONArray();

			for (Hit<Object> hit : detailDataList) {
				if (count >= 10) {
					break;
				}
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
				String jsonString = objectMapper.writeValueAsString(hit.source());
				JSONObject json = new JSONObject(jsonString);
				jsonArray.put(json);
				count++;
			}

			long totalCount = search.hits().total().value();
			System.out.println("Total count: " + totalCount);

		
//			dm.setTotal(totalCount);
//			updateFilterDataMaster(dm, filterDataMaster.getDataMstId());


			response.put("totalCnt", totalCount);
			response.put("detailDataList", jsonArray);

			return response.toString();

		} catch (Exception e) {
			System.out.println(e);
			return response.toString();
		}
	}

	

	@PostMapping("downloadInsertData")
	public String getDownloadInsertRecords(@RequestBody FilterDataMaster filter) {
		JSONObject res = new JSONObject();
		try {
			filter.setStatus(1);
			LocalDateTime currentDateTime = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
			String formattedDateTime = currentDateTime.format(formatter);
			filter.setStartDateTime(formattedDateTime);

			// Continue with the rest of the code...
			IndexRequest<FilterDataMaster> indexRequest = new IndexRequest.Builder<FilterDataMaster>()
					.index("download_data_mst").document(filter).build();

			// Send the index request
			IndexResponse indexResponse = elasticsearchClient.index(indexRequest);

			if (indexResponse.result() == Result.Created) {
				boolean flag = true;
				res.put("message", "insert Data successfully.");
				res.put("flag", flag);
			} else {
				// Indexing failed
				res.put("message", "Data submission failed.");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return res.toString();

	}

	@GetMapping("/getAllDownloadInsertRecords")
	public String getAllDownloadInsertRecords(String userId) {
		JSONObject res = new JSONObject();
		FilterDataMaster fdm = new FilterDataMaster();
		fdm.setUserId(userId);
		List<Query> query1 = prepareQueryList(fdm);

		try {
			SearchResponse<FilterDataMaster> search = elasticsearchClient.search(req -> req.index("download_data_mst")
					.size(1000).query(query -> query.bool(bool -> bool.must(query1))), FilterDataMaster.class);

			List<Hit<FilterDataMaster>> hits = search.hits().hits();

			JSONArray jsonArray = new JSONArray();

			for (Hit<FilterDataMaster> hit : hits) {
				FilterDataMaster filter = hit.source();
				JSONObject filterJson = new JSONObject(filter);
				jsonArray.put(filterJson);
			}

			res.put("message", "Retrieved all inserted index records.");
			res.put("records", jsonArray);

		} catch (IOException e) {
			e.printStackTrace();
			res.put("message", "Error occurred while retrieving records.");
		}

		return res.toString();
	}

	private List<Query> prepareQueryList(FilterDataMaster fdm) {

		Map<String, String> conditionMap = new HashMap<>();
		if (fdm.getUserId() != null && !fdm.getUserId().equalsIgnoreCase(""))
			conditionMap.put("userId.keyword", fdm.getUserId());

		if (fdm.getId() != null && !fdm.getId().equalsIgnoreCase(""))
			conditionMap.put("_id", fdm.getId());

		return conditionMap.entrySet().stream().filter(entry -> !ObjectUtils.isEmpty(entry.getValue()))
				.map(entry -> QueryBuilderUtils.termQuery(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

	@PostMapping("/inserTempDataMaster")
	public String insertTempDataMaster(@RequestBody TempData tempMaster) {
		JSONObject res = new JSONObject();
		try {
			tempMaster.setStatus(1);

			IndexRequest<TempData> indexRequest = new IndexRequest.Builder<TempData>().index("temp_data_mst")
					.document(tempMaster).build();

			// Send the index request
			IndexResponse indexResponse = elasticsearchClient.index(indexRequest);

			if (indexResponse.result() == Result.Created) {
				boolean flag = true;
				res.put("message", "insert Data successfully.");
				res.put("flag", flag);
			} else {
				// Indexing failed
				res.put("message", "Data submission failed.");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return res.toString();

	}

	@GetMapping("/getDataMasterHeaderList")
	public String getDataMasterHeaderList(String id) {
		JSONObject res = new JSONObject();
		DataMaster fdm = new DataMaster();
		fdm.setId(id);
		List<Query> query1 = prepareQueryList(fdm);
		try {

			SearchResponse<DataMaster> search = elasticsearchClient.search(
					req -> req.index("data_mst").size(1000).query(query -> query.bool(bool -> bool.must(query1))),
					DataMaster.class);

			List<Hit<DataMaster>> hits = search.hits().hits();

			JSONArray jsonArray = new JSONArray();

			for (Hit<DataMaster> hit : hits) {
				DataMaster filter = hit.source();
				String dataName = filter.getDataName();
				JSONObject hitData = new JSONObject();
				hitData.put("dataName", dataName);

				JSONArray headerList = new JSONArray(filter.getHeaderList());
				hitData.put("headerList", headerList);

				jsonArray.put(hitData);
			}

			res.put("message", "Retrieved all inserted index records.");
			res.put("dataList", jsonArray);

		} catch (IOException e) {
			e.printStackTrace();
			res.put("message", "Error occurred while retrieving records.");
		}

		return res.toString();
	}

}
