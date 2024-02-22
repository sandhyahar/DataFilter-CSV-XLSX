package com.API.Events;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.elasticsearch.client.RestHighLevelClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.ObjectUtils;

import com.API.Common.QueryBuilderUtils;
import com.API.Service.ParamMstService;
import com.API.pojo.CampaignMst;
import com.API.pojo.FilteredCampaignData;
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

public class CampaignProcessDownladThread implements Runnable {

	CampaignMst cdm;

	ElasticsearchClient elasticsearchClient;

	ParamMstService paramMstService;

	String zipFilePath = "";

	public CampaignProcessDownladThread(CampaignMst cdm, ElasticsearchClient elasticsearchClient,
			ParamMstService paramMstService) {
		this.elasticsearchClient = elasticsearchClient;
		this.paramMstService = paramMstService;
		this.cdm = cdm;
	}

	@Override
	public void run() {
		if (cdm != null) {
			int recordLimit = 500000;
			int starIndex = 0;
			int size = 1000;
			long totalCount = 0;
			List<JSONObject> jsonObjectList = new ArrayList<>();

			List<ParamMst> downloadPathParamMsts = paramMstService.findByParamName("downloadPath");
			if (downloadPathParamMsts != null) {
				String downloadPathParamValue = downloadPathParamMsts.get(0).getParamValue();

				// Generate the file name based on fdm.userId and current time
				String fileName = cdm.getUserId() + "_" + System.currentTimeMillis() + ".xlsx";
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

				cdm.setDownloadFlag(3);
				List<ParamMst> applicationUrlParamMsts = paramMstService.findByParamName("applicationUrl");
				if (applicationUrlParamMsts != null) {
					String applicationUrlParamValue = applicationUrlParamMsts.get(0).getParamValue();
					String zipFileName = new File(zipFilePath).getName();
					filePath = applicationUrlParamValue + "downloadFile/" + zipFileName;
					cdm.setProcessedFilePath(filePath);
					totalCount = jsonObjectList.size();
					cdm.setTotalRecords(totalCount);
				}
				try {
					updateCampaignMst(cdm, cdm.getId());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			System.out.println("Total count: " + totalCount);
		}
	}

	private List<JSONObject> fetchDataFromElasticsearch(int starIndex, int size) {

		FilteredCampaignData fdms = new FilteredCampaignData();
		fdms.setUserId(cdm.getUserId());
		fdms.setCampaignId(cdm.getId());
		List<Query> queryList = prepareQueryList(fdms);

		List<JSONObject> jsonObjectList = new ArrayList<>();
		try {
			SearchResponse<Object> search = elasticsearchClient.search(req -> req.index("filtered_campaign_data")
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

					columnSet.remove("campaignId");
					columnSet.remove("userId");

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
				System.out.println("write records:" + recordCount);

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

	private List<Query> prepareQueryList(FilteredCampaignData cm) {
		Map<String, String> conditionMap = new HashMap<>();
		conditionMap.put("campaignId.keyword", cm.getCampaignId());
		conditionMap.put("userId.keyword", cm.getUserId());
		return conditionMap.entrySet().stream().filter(entry -> !ObjectUtils.isEmpty(entry.getValue()))
				.map(entry -> QueryBuilderUtils.termQuery(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

	public String updateCampaignMst(CampaignMst cm, String id) throws IOException {
		UpdateRequest<CampaignMst, CampaignMst> updateRequest = UpdateRequest
				.of(req -> req.index("campaign_mst").id(id).doc(cm));
		UpdateResponse<CampaignMst> response = elasticsearchClient.update(updateRequest, CampaignMst.class);
		return response.result().toString();
	}

}
