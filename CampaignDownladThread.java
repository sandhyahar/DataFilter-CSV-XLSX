package com.API.Events;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.ObjectUtils;

import com.API.Common.QueryBuilderUtils;
import com.API.Service.ParamMstService;
import com.API.pojo.CampaignMst;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

public class CampaignDownladThread implements Runnable{
	
	CampaignMst fdm;
	

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
		


	@Override
	public void run() {
		while (true) {
			try {
				CampaignMst fd = new CampaignMst();
				fd.setStatus(3);
				fd.setDownloadFlag(1);

				List<Query> query1 = prepareQueryList(fd);
				
				SearchResponse<CampaignMst> search = elasticsearchClient.search(
						req -> req.index("campaign_mst").query(query -> query.bool(bool -> bool.must(query1))),
						CampaignMst.class);

				if (search.hits().hits().size() > 0) {
					for (Hit<CampaignMst> hit : search.hits().hits()) {
						String id = hit.id();
						CampaignMst fdm = hit.source();
						fdm.setDownloadFlag(2);
						String res = updateDataMaster(fdm, id);
						fdm.setId(id);
						if (res.equalsIgnoreCase("updated")) {
							CampaignProcessDownladThread dmp = new CampaignProcessDownladThread(fdm,elasticsearchClient,paramMstService);
							Thread th = new Thread(dmp);
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

	private List<Query> prepareQueryList(CampaignMst fd) {
		Map<String, Integer> conditionMap = new HashMap<>();
		conditionMap.put("status", fd.getStatus());
		conditionMap.put("downloadFlag", fd.getDownloadFlag());
		return conditionMap.entrySet().stream().filter(entry -> !ObjectUtils.isEmpty(entry.getValue()))
				.map(entry -> QueryBuilderUtils.termQuery(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

	public String updateDataMaster(CampaignMst fd, String id) throws IOException {
		UpdateRequest<CampaignMst, CampaignMst> updateRequest = UpdateRequest
				.of(req -> req.index("campaign_mst").id(id).doc(fd));
		UpdateResponse<CampaignMst> response = elasticsearchClient.update(updateRequest, CampaignMst.class);
		return response.result().toString();
	}
}