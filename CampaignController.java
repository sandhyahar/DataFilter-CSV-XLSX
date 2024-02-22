package com.API.RestController;

import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.API.Common.QueryBuilderUtils;
import com.API.Common.common;
import com.API.Service.ParamMstService;
import com.API.pojo.CampaignMst;
import com.API.pojo.ParamMst;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

@RestController
public class CampaignController {

	@Autowired
	ElasticsearchClient elasticsearchClient;

	@Autowired
	RestHighLevelClient resthighLevelClient;

	@Autowired
	ParamMstService paramMstService;

	@RequestMapping("/insertCampaignData")
	public String InsertCampaignData(String userId, String campaignName, String campaignList,
			@RequestParam("filedata") MultipartFile filedata, HttpServletRequest req1) {
		JSONObject res = new JSONObject();
		try {
			if (filedata != null && filedata.getBytes().length > 0) {
				String fileName = filedata.getOriginalFilename();
				boolean flag;
				int index = fileName.lastIndexOf('.');
				if (index > 0) {
					String extension = fileName.substring(index + 1);
					if (extension.equalsIgnoreCase("csv") || extension.equalsIgnoreCase("xlsx")) {
						String paramName = "uploadfilePath";
						List<ParamMst> paramMsts = paramMstService.findByParamName(paramName);
						String currentPath = "";
						if (!paramMsts.isEmpty()) {
							String paramValue = paramMsts.get(0).getParamValue();
							currentPath = paramValue + "/" + System.currentTimeMillis() + "_" + fileName;
						}
						boolean flagWrite = false;

						if (extension.equalsIgnoreCase("csv")) {
							flagWrite = common.write(filedata, currentPath);

						} else if (extension.equalsIgnoreCase("xlsx")) {
							flagWrite = common.readAndWriteExcelFile(filedata, currentPath);
						}

						if (flagWrite) {
							CampaignMst cmst = new CampaignMst();
							cmst.setCampaignName(campaignName);
							cmst.setFilePath(currentPath);
							cmst.setSelectedCountry("india");
							cmst.setStatus(1);
							cmst.setDownloadFlag(0);
							cmst.setIsVisible(1);
							cmst.setUserId(userId);
							LocalDateTime currentDateTime = LocalDateTime.now();
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
							String formattedDateTime = currentDateTime.format(formatter);
							cmst.setStartDateTime(formattedDateTime);

							if (campaignList != null && !campaignList.equalsIgnoreCase("")) {
								cmst.setCampaignList(
										Stream.of(campaignList.split(",", -1)).collect(Collectors.toList()));
							}
							if (cmst.getCampaignList() != null && cmst.getCampaignList().size() > 0) {
								validateCampaignList(cmst.getCampaignList(), cmst);
								System.out.println("campaignList: " + campaignList);

							}
							List<Query> query1 = prepareQueryList(cmst);
							SearchResponse<CampaignMst> search = elasticsearchClient.search(req -> req
									.index("campaign_mst").query(query -> query.bool(bool -> bool.must(query1))),
									CampaignMst.class);

							if (search.hits().total().value() > 0) {
								flag = false;
								res.put("message", "Campaign name already exist.");
								res.put("flag", flag);
							} else {
								IndexRequest<CampaignMst> indexRequest = new IndexRequest.Builder<CampaignMst>()
										.index("campaign_mst").document(cmst).build();
								IndexResponse indexRes = elasticsearchClient.index(indexRequest);
								if (indexRes.result().jsonValue().equalsIgnoreCase("created")) {
									flag = true;
									res.put("message", "Campaign submitted successfully.");
									res.put("flag", flag);
								}
							}

						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Exception in InsertCampaignData " + e);
			// TODO: handle exception
		}

		return res.toString();
	}

	@PostMapping("/checkUniqueUserName")
	public String getUniqueCampaignName(@RequestBody CampaignMst cmt) throws IOException {
		JSONObject res = new JSONObject();
		boolean flag;
		List<Query> query1 = prepareQueryList(cmt);
		SearchResponse<CampaignMst> search = elasticsearchClient.search(
				req -> req.index("campaign_mst").query(query -> query.bool(bool -> bool.must(query1))),
				CampaignMst.class);

		if (search.hits().total().value() > 0) {
			flag = false;
			res.put("message", "Campaign name already exist.");
		} else {
			flag = true;
			res.put("message", "Campaign name insert sussefully");
		}

		res.put("flag", flag);
		return res.toString();
	}

	@GetMapping("/findCampaignList")
	public String getCampaignList(String userId, String startTimeDate) {
		JSONObject res = new JSONObject();
		try {
			CampaignMst cmst = new CampaignMst();
			cmst.setUserId(userId);
			cmst.setStartDateTime(startTimeDate);
			cmst.setIsVisible(1);

			org.elasticsearch.index.query.BoolQueryBuilder boolQuery = org.elasticsearch.index.query.QueryBuilders
					.boolQuery();

			// Add the range condition for startDateTime
			boolQuery.must(org.elasticsearch.index.query.QueryBuilders.rangeQuery("startDateTime").gte(startTimeDate)
					.lte(startTimeDate));

			// Add the term condition for userId
			boolQuery.must(org.elasticsearch.index.query.QueryBuilders.termQuery("userId", userId));

			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().query(boolQuery).size(10000);

			org.elasticsearch.action.search.SearchRequest searchRequest = new org.elasticsearch.action.search.SearchRequest(
					"campaign_mst");
			searchRequest.source(sourceBuilder);

			org.elasticsearch.action.search.SearchResponse searchResponse = resthighLevelClient.search(searchRequest,
					org.elasticsearch.client.RequestOptions.DEFAULT);

			SearchHits searchdata = searchResponse.getHits();
			ObjectMapper objectMapper = new ObjectMapper();
			JSONArray jsonArray = new JSONArray();

			for (SearchHit hit : searchdata.getHits()) {
				// Perform type casting to access specific fields
				String id = hit.getId();
				String index = hit.getIndex();
				String sourceAsString = hit.getSourceAsString();

				CampaignMst campaign = objectMapper.readValue(sourceAsString, CampaignMst.class);
				campaign.setId(id);

				JSONObject campaignJson = new JSONObject(campaign);
				jsonArray.put(campaignJson);

			}

			res.put("list", jsonArray);

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		return res.toString();

	}

	@GetMapping("/findCampaignNameAndId")
	public String getCampaignListID(String userId) {
		JSONObject res = new JSONObject();
		try {
			CampaignMst cmst = new CampaignMst();
			cmst.setUserId(userId);
			cmst.setIsVisible(1);

			List<Query> query1 = prepareQueryList(cmst);
			SearchResponse<CampaignMst> search = elasticsearchClient.search(
					req -> req.index("campaign_mst").size(1000).query(query -> query.bool(bool -> bool.must(query1))),
					CampaignMst.class);

			List<Hit<CampaignMst>> list = search.hits().hits();

			// Convert the list to a JSONArray
			JSONArray jsonArray = new JSONArray();

			for (Hit<CampaignMst> hit : list) {
				CampaignMst cm = hit.source();
				cm.setId(hit.id());
				JSONObject campaignJson = new JSONObject();
				campaignJson.put("id", cm.getId());
				campaignJson.put("campaignName", cm.getCampaignName());
				campaignJson.put("isVisible", cm.getIsVisible());

				jsonArray.put(campaignJson);
			}

			res.put("list", jsonArray);

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		return res.toString();

	}

	private List<Query> prepareQueryList(CampaignMst cm) {

		Map<String, String> conditionMap = new HashMap<>();
		if (cm.getCampaignName() != null && !cm.getCampaignName().equalsIgnoreCase(""))
			conditionMap.put("campaignName.keyword", cm.getCampaignName());

		if (cm.getUserId() != null && !cm.getUserId().equalsIgnoreCase(""))
			conditionMap.put("userId.keyword", cm.getUserId());

		if (cm.getId() != null && !cm.getId().equalsIgnoreCase(""))
			conditionMap.put("_id", cm.getId());

		return conditionMap.entrySet().stream().filter(entry -> !ObjectUtils.isEmpty(entry.getValue()))
				.map(entry -> QueryBuilderUtils.termQuery(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

	private List<String> validateCampaignList(List<String> list, CampaignMst cm) {

		CampaignMst cm1 = new CampaignMst();

		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			String str = (String) iterator.next();

			cm1.setId(str);
			cm1.setUserId(cm.getUserId());
			List<Query> query1 = prepareQueryList(cm1);
			try {
				SearchResponse<CampaignMst> search = elasticsearchClient.search(
						req -> req.index("campaign_mst").query(query -> query.bool(bool -> bool.must(query1))),
						CampaignMst.class);
				if (search.hits().total().value() == 0) {
					iterator.remove();
				}
			} catch (ElasticsearchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return list;
	}

	@PostMapping("/updateCampaignVisibility")
	public String updateCampaignVisibility(String userId, String id) throws IOException {
		CampaignMst cmst = new CampaignMst();
		cmst.setUserId(userId);
		cmst.setId(id);
		List<Query> query1 = prepareQueryList(cmst);
		SearchResponse<CampaignMst> search = elasticsearchClient.search(
				req -> req.index("campaign_mst").query(query -> query.bool(bool -> bool.must(query1))),
				CampaignMst.class);

		List<Hit<CampaignMst>> list = search.hits().hits();

		for (Hit<CampaignMst> hit : list) {
			CampaignMst campaign = hit.source();
			campaign.setIsVisible(0);
			UpdateRequest<CampaignMst, CampaignMst> updateRequest = UpdateRequest
					.of(req -> req.index("campaign_mst").id(hit.id()).doc(campaign));
			UpdateResponse<CampaignMst> response = elasticsearchClient.update(updateRequest, CampaignMst.class);
			// Handle the response if needed
		}
		return "Update completed.";
	}

	@PostMapping("/updateCampaignData")
	public String updateCampaignData(String userId, String id, @RequestParam("filedata") MultipartFile filedata)
			throws IOException {

		CampaignMst cmst = new CampaignMst();
		String fileName = filedata.getOriginalFilename();
		String currentPath = "";

		int index = fileName.lastIndexOf('.');
		if (index > 0) {
			String extension = fileName.substring(index + 1);
			if (extension.equalsIgnoreCase("csv") || extension.equalsIgnoreCase("xlsx")) {
				String paramName = "uploadfilePath";
				List<ParamMst> paramMsts = paramMstService.findByParamName(paramName);
				if (!paramMsts.isEmpty()) {
					String paramValue = paramMsts.get(0).getParamValue();
					currentPath = paramValue + "/" + System.currentTimeMillis() + "_" + fileName;
				}
				boolean flagWrite = false;

				if (extension.equalsIgnoreCase("csv")) {
					flagWrite = common.write(filedata, currentPath);
				} else if (extension.equalsIgnoreCase("xlsx")) {
					flagWrite = common.readAndWriteExcelFile(filedata, currentPath);
				}
			}
		}
		cmst.setUserId(userId);
		cmst.setId(id);

		List<Query> query1 = prepareQueryList(cmst);
		SearchResponse<CampaignMst> search = elasticsearchClient.search(
				req -> req.index("campaign_mst").query(query -> query.bool(bool -> bool.must(query1))),
				CampaignMst.class);

		List<Hit<CampaignMst>> list = search.hits().hits();

		for (Hit<CampaignMst> hit : list) {
			CampaignMst campaign = hit.source();
			campaign.setFilePath(currentPath);
			campaign.setStatus(1);
			campaign.setDownloadFlag(1);
			List<String> filterList = campaign.getFilterCampaignList();
			if (filterList == null) {
				filterList = new ArrayList<>();
				campaign.setFilterCampaignList(filterList);
				filterList.add(id);
			}
			if (!filterList.contains(id)) {
				filterList.add(id);
			}
			campaign.setFilterCampaignList(filterList);

			UpdateRequest<CampaignMst, CampaignMst> updateRequest = UpdateRequest
					.of(req -> req.index("campaign_mst").id(hit.id()).doc(campaign));
			UpdateResponse<CampaignMst> response = elasticsearchClient.update(updateRequest, CampaignMst.class);
			// Handle the response if needed
		}
		return "Update completed.";
	}
}
