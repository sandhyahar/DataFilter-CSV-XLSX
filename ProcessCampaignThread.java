package com.API.Events;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.util.ObjectUtils;

import com.API.Common.QueryBuilderUtils;
import com.API.Service.ParamMstService;
import com.API.pojo.CampaignMst;
import com.API.pojo.FilteredCampaignData;
import com.API.pojo.ParamMst;
import com.monitorjbl.xlsx.StreamingReader;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

public class ProcessCampaignThread implements Runnable {

	CampaignMst cm;

	ElasticsearchClient elasticsearchClient;

	RestHighLevelClient resthighLevelClient;

	ParamMstService paramMstService;

	public ProcessCampaignThread(CampaignMst cm, ElasticsearchClient elasticsearchClient,
			ParamMstService paramMstService, RestHighLevelClient resthighLevelClient) {
		this.cm = cm;
		this.elasticsearchClient = elasticsearchClient;
		this.paramMstService = paramMstService;
		this.resthighLevelClient = resthighLevelClient;

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (cm != null) {
			HashMap input_hash = new HashMap<String, String>();
			input_hash = readFile(cm.getFilePath());

			if (checkFileExist(cm.getFilePath())) {
				if (cm.getFilterCampaignList() != null && cm.getFilterCampaignList().size() > 0) {
					for (String id : cm.getFilterCampaignList()) {
						Boolean flag = true;
						int startIndex = 0;
						int size = 1000;
						try {
							while (flag) {
								List<Hit<FilteredCampaignData>> filteredCampaignDataList = getTotalCount(id,
										cm.getUserId(), startIndex, size);
								if (filteredCampaignDataList == null || filteredCampaignDataList.size() == 0) {
									flag = false;
									break;
								} else {
									processFilteredCampaignData(filteredCampaignDataList, input_hash);
									startIndex += size;
									System.out.println("start index" + startIndex);
								}

							}

						} catch (Exception e) {
							System.out.println(e);

						}
					}
					String desiredFormat = ".csv";
					String extension = cm.getFilePath().substring(cm.getFilePath().lastIndexOf("."));
					if (extension.equalsIgnoreCase(".csv") || extension.equalsIgnoreCase(".xlsx")) {
						desiredFormat = extension;
					}

					String filename = cm.getId() + "_" + cm.getUserId() + desiredFormat;
					String downloadPaths;
					String file;
					// Retrieve the paramValue for "applicationUrl"
					String applicationUrlParamName = "applicationUrl";
					List<ParamMst> applicationUrlParamMsts = paramMstService.findByParamName(applicationUrlParamName);
					if (applicationUrlParamMsts != null) {
						String applicationUrlParamValue = applicationUrlParamMsts.get(0).getParamValue();
						downloadPaths = applicationUrlParamValue + "downloadFile/" + filename;
						cm.setProcessedFilePath(downloadPaths);
					}
					// Retrieve the paramValue for "downloadPath"
					String downloadPathParamName = "downloadPath";
					List<ParamMst> downloadPathParamMsts = paramMstService.findByParamName(downloadPathParamName);
					if (downloadPathParamMsts != null) {
						String downloadPathParamValue = downloadPathParamMsts.get(0).getParamValue();
						file = downloadPathParamValue + filename;
						System.out.println("file start time: " + new java.util.Date());
						insertFilteredData(input_hash, file, desiredFormat);
						System.out.println("file end time: " + new java.util.Date());
					}

					int totalRecords = input_hash.size();
					System.out.println("Total records: " + totalRecords);

					try {
						cm.setStatus(3);
						cm.setDownloadFlag(1);
						cm.setTotalRecords(totalRecords);
						LocalDateTime currentDateTime = LocalDateTime.now();
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
						String formattedDateTime = currentDateTime.format(formatter);
						cm.setEndDateTime(formattedDateTime);
						updateCampaignMst(cm, cm.getId());

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {

					String desiredFormat = ".csv";
					String extension = cm.getFilePath().substring(cm.getFilePath().lastIndexOf("."));
					if (extension.equalsIgnoreCase(".csv") || extension.equalsIgnoreCase(".xlsx")) {
						desiredFormat = extension;
					}

					String filename = cm.getId() + "_" + cm.getUserId() + desiredFormat;
					String downloadPaths;
					String file;
					// Retrieve the paramValue for "applicationUrl"
					String applicationUrlParamName = "applicationUrl";
					List<ParamMst> applicationUrlParamMsts = paramMstService.findByParamName(applicationUrlParamName);
					if (applicationUrlParamMsts != null) {
						String applicationUrlParamValue = applicationUrlParamMsts.get(0).getParamValue();
						downloadPaths = applicationUrlParamValue + "downloadFile/" + filename;
						cm.setProcessedFilePath(downloadPaths);
					}
					// Retrieve the paramValue for "downloadPath"
					String downloadPathParamName = "downloadPath";
					List<ParamMst> downloadPathParamMsts = paramMstService.findByParamName(downloadPathParamName);
					if (downloadPathParamMsts != null) {
						String downloadPathParamValue = downloadPathParamMsts.get(0).getParamValue();
						file = downloadPathParamValue + filename;
						System.out.println("file start time: " + new java.util.Date());
						insertFilteredData(input_hash, file, desiredFormat);
						System.out.println("file end time: " + new java.util.Date());
					}

					int totalRecords = input_hash.size();
					System.out.println("Total records: " + totalRecords);

					try {
						cm.setStatus(3);
						cm.setDownloadFlag(3);
						cm.setTotalRecords(totalRecords);
						LocalDateTime currentDateTime = LocalDateTime.now();
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
						String formattedDateTime = currentDateTime.format(formatter);
						cm.setEndDateTime(formattedDateTime);
						updateCampaignMst(cm, cm.getId());

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

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

	public void processFilteredCampaignData(List<Hit<FilteredCampaignData>> filteredCampaignDataList,
			HashMap<String, String> input_hash) {
		try {
			for (Iterator iterator = filteredCampaignDataList.iterator(); iterator.hasNext();) {
				Hit<FilteredCampaignData> hit = (Hit<FilteredCampaignData>) iterator.next();
				if (input_hash.containsKey(hit.source().getMobileNo())) {
					System.out.println(hit.source().getMobileNo() + " found ");
					input_hash.remove(hit.source().getMobileNo());
				}

			}
		} catch (Exception e) {
			System.out.print(e);
		}
	}

	public static HashMap<String, String> readFile(String fileName) {
		HashMap<String, String> input = new HashMap<>();

		if (fileName.endsWith(".xlsx")) {
			readXlsxFile(fileName, input);
		} else if (fileName.endsWith(".csv")) {
			String line = "";
			String cvsSplitBy = ",";

			try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
				while ((line = br.readLine()) != null) {
					String[] myvalue = line.split(cvsSplitBy);
					for (int i = 0; i < myvalue.length; i++) {
						 String value = myvalue[i].trim(); 
			                input.put(value, "");
			                
					}
				}
			} catch (IOException e) {
				System.out.println("Exception: " + e);
			}

		} else {
			System.out.println("Unsupported file format");
		}

		return input;
	}

	public static void readXlsxFile(String filePath, HashMap<String, String> input) {
		try {
			InputStream fileInputStream = new FileInputStream(new File(filePath));
			Workbook workbook = StreamingReader.builder().rowCacheSize(100).bufferSize(1024).open(fileInputStream);

			Sheet sheet = workbook.getSheetAt(0);
			DataFormatter dataFormatter = new DataFormatter();

			for (Row row : sheet) {
				for (Cell cell : row) {
					String cellValue = dataFormatter.formatCellValue(cell);
					
					input.put(cellValue, "");

					System.out.println(cellValue);
				}
			}

			workbook.close();
			fileInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean insertFilteredData(HashMap<String, ?> inputHash, String file, String desiredFormat) {
		// TODO Auto-generated method stub
		boolean flag = false;
		try {
			if (desiredFormat.equalsIgnoreCase(".csv")) {
				File outputFile = new File(file);

				FileWriter out = new FileWriter(outputFile);

				Set<String> keys = inputHash.keySet();
				int cnt = 0;
				List<FilteredCampaignData> list = new ArrayList<>();
				for (String key : keys) {
					FilteredCampaignData FCD = new FilteredCampaignData();
					FCD.setCampaignId(cm.getId());
					FCD.setUserId(cm.getUserId());
					FCD.setMobileNo(key);
					out.write(key + "\r\n");
					list.add(FCD);
					cnt++;
					if (cnt > 500) {
						try {
							bulkInsertEmployees(list);
							cnt = 0;
							list = new ArrayList<>();
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
				if (cnt > 0) {
					try {
						bulkInsertEmployees(list);
						cnt = 0;
						list = new ArrayList<>();
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				out.close();
				flag = true;
			} else if (desiredFormat.equalsIgnoreCase(".xlsx")) {
				Workbook workbook = new XSSFWorkbook();
				Sheet sheet = workbook.createSheet("Data");

				Set<String> keys = inputHash.keySet();
				int cnt = 0;
				List<FilteredCampaignData> list = new ArrayList<>();

				for (String key : keys) {
					FilteredCampaignData FCD = new FilteredCampaignData();
					FCD.setCampaignId(cm.getId());
					FCD.setUserId(cm.getUserId());
					FCD.setMobileNo(key);
					Row row = sheet.createRow(cnt++);
					Cell cell = row.createCell(0);
					cell.setCellValue(key);
					list.add(FCD);
					if (list.size() > 500) {
						try {
							bulkInsertEmployees(list);
							list.clear();
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
				if (cnt > 0) {
					try {
						bulkInsertEmployees(list);
						cnt = 0;
						list = new ArrayList<>();
					} catch (Exception e) {
						// TODO: handle exception
					}
				}

				FileOutputStream fileOut = new FileOutputStream(file);
				workbook.write(fileOut);
				fileOut.close();
				workbook.close();

				flag = true;
			} else {
				System.out.println("Invalid desiredFormat value: " + desiredFormat);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return flag;
	}

	public boolean bulkInsertEmployees(List<FilteredCampaignData> filteredCampaignDataList) throws IOException {
		BulkRequest.Builder builder = new BulkRequest.Builder();
		filteredCampaignDataList.stream().forEach(filteredCampaign -> builder
				.operations(op -> op.index(i -> i.index("filtered_campaign_data").document(filteredCampaign))));
		BulkResponse bulkResponse = elasticsearchClient.bulk(builder.build());
		return !bulkResponse.errors();
	}

	public String updateCampaignMst(CampaignMst cm, String id) throws IOException {
		UpdateRequest<CampaignMst, CampaignMst> updateRequest = UpdateRequest
				.of(req -> req.index("campaign_mst").id(id).doc(cm));
		UpdateResponse<CampaignMst> response = elasticsearchClient.update(updateRequest, CampaignMst.class);
		return response.result().toString();
	}

	public List<Hit<FilteredCampaignData>> getTotalCount(String campaignId, String userId, int starIndex, int size) {
		long cnt = 0;
		List<Hit<FilteredCampaignData>> filteredCampaignDataList = null;
		FilteredCampaignData data = new FilteredCampaignData();
		data.setCampaignId(campaignId);
		data.setUserId(userId);
		List<Query> query1 = prepareQueryList(data);
		try {
			SearchResponse<FilteredCampaignData> search = elasticsearchClient.search(
					req -> req.index("filtered_campaign_data").query(query -> query.bool(bool -> bool.must(query1)))
							.from(starIndex).size(size),
					FilteredCampaignData.class);
			filteredCampaignDataList = search.hits().hits();
		} catch (ElasticsearchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return filteredCampaignDataList;
	}

	private List<Query> prepareQueryList(FilteredCampaignData cm) {
		Map<String, String> conditionMap = new HashMap<>();
		conditionMap.put("campaignId.keyword", cm.getCampaignId());
		conditionMap.put("userId.keyword", cm.getUserId());
		return conditionMap.entrySet().stream().filter(entry -> !ObjectUtils.isEmpty(entry.getValue()))
				.map(entry -> QueryBuilderUtils.termQuery(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

	public void DoFilter() {
		try {
			// List<Query> query1 = prepareQueryList(cm);
			// SearchResponse<CampaignMst> search = elasticsearchClient.search(
			// req -> req.index("campaign_mst").query(query -> query.bool(bool ->
			// bool.must(query1))),
			// CampaignMst.class);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
