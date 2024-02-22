package com.API.Service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.API.pojo.CampaignMst;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;

@Service
public class CampaignService {
	@Autowired
	private ElasticsearchClient elasticsearchClient;

	public String updateCampaignMst(CampaignMst cm, String id) throws IOException {
		UpdateRequest<CampaignMst, CampaignMst> updateRequest = UpdateRequest
				.of(req -> req.index("campaign_mst").id(id).doc(cm));
		UpdateResponse<CampaignMst> response = elasticsearchClient.update(updateRequest, CampaignMst.class);
		return response.result().toString();
	}

}
