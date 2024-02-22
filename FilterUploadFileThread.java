package com.API.Events;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.API.Common.QueryBuilderUtils;
import com.API.Service.CampaignService;
import com.API.Service.ParamMstService;
import com.API.pojo.CampaignMst;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

@Service
public class FilterUploadFileThread implements Runnable {

	@Autowired
	CampaignService campainService;
	
	RestHighLevelClient resthighLevelClient;

	ElasticsearchClient elasticsearchClient;
	
	ParamMstService paramMstService;

	public ParamMstService getParamMstService() {
		return paramMstService;
	}

	public void setParamMstService(ParamMstService paramMstService) {
		this.paramMstService = paramMstService;
	}

	public ElasticsearchClient getElasticsearchClient() {
		return elasticsearchClient;
	}

	public void setElasticsearchClient(ElasticsearchClient elasticsearchClient) {
		this.elasticsearchClient = elasticsearchClient;
	}
		
	public RestHighLevelClient getResthighLevelClient() {
		return resthighLevelClient;
	}

	public void setResthighLevelClient(RestHighLevelClient resthighLevelClient) {
		this.resthighLevelClient = resthighLevelClient;
	}

	@Override
	public void run() {
		while (true) {
			try {
				CampaignMst cm = new CampaignMst();
				cm.setStatus(1);
				List<Query> query1 = prepareQueryList(cm);
				SearchResponse<CampaignMst> search = elasticsearchClient.search(
						req -> req.index("campaign_mst").query(query -> query.bool(bool -> bool.must(query1))),
						CampaignMst.class);

				if (search.hits().hits().size() > 0) {
					for (Hit<CampaignMst> hit : search.hits().hits()) {
						String id = hit.id();
						CampaignMst campaignmst = hit.source();
						campaignmst.setStatus(2);
						String res = updateCampaignMst(campaignmst, id);
						campaignmst.setId(id);
						
						if (res.equalsIgnoreCase("updated")) {
							ProcessCampaignThread pct = new ProcessCampaignThread(campaignmst, elasticsearchClient,paramMstService,resthighLevelClient);
							Thread th = new Thread(pct);
							th.start();
						}

					}

					Thread.sleep(5000);
				}
				Thread.sleep(5000);
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e);
			}

		}

	}

	private List<Query> prepareQueryList(CampaignMst cm) {
		Map<String, Integer> conditionMap = new HashMap<>();
		conditionMap.put("status", cm.getStatus());
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
