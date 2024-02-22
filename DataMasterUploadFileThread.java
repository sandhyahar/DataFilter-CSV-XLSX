package com.API.Events;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.util.ObjectUtils;

import com.API.Common.QueryBuilderUtils;
import com.API.pojo.DataMaster;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

public class DataMasterUploadFileThread implements Runnable {
	
	ElasticsearchClient elasticsearchClient;
	
	RestHighLevelClient restHighLevelClient;
	
	public ElasticsearchClient getElasticsearchClient() {
		return elasticsearchClient;
	}

	public void setElasticsearchClient(ElasticsearchClient elasticsearchClient) {
		this.elasticsearchClient = elasticsearchClient;
	}
	
	
	public RestHighLevelClient getRestHighLevelClient() {
		return restHighLevelClient;
	}

	public void setRestHighLevelClient(RestHighLevelClient restHighLevelClient) {
		this.restHighLevelClient = restHighLevelClient;
	}
	
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			// System.out.println("Thread Stated");
			try {
				DataMaster dm = new DataMaster();
				dm.setStatus(1);
				List<Query> query1 = prepareQueryList(dm);
				
				SearchResponse<DataMaster> search = elasticsearchClient.search(
						req -> req.index("data_mst").query(query -> query.bool(bool -> bool.must(query1))),
						DataMaster.class);

				if (search.hits().hits().size() > 0) {
					for (Hit<DataMaster> hit : search.hits().hits()) {
						String id = hit.id();
						DataMaster dataMaster = hit.source();
						dataMaster.setStatus(2);
						String res = updateDataMaster(dataMaster, id);
						dataMaster.setId(id);
						if (res.equalsIgnoreCase("updated")) {
							DataMastProcessThread dmp = new DataMastProcessThread(dataMaster,elasticsearchClient,restHighLevelClient);
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

	private List<Query> prepareQueryList(DataMaster dm) {
		Map<String, Integer> conditionMap = new HashMap<>();
		conditionMap.put("status", dm.getStatus());
		return conditionMap.entrySet().stream().filter(entry -> !ObjectUtils.isEmpty(entry.getValue()))
				.map(entry -> QueryBuilderUtils.termQuery(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

	public String updateDataMaster(DataMaster dm, String id) throws IOException {
		UpdateRequest<DataMaster, DataMaster> updateRequest = UpdateRequest
				.of(req -> req.index("data_mst").id(id).doc(dm));
		UpdateResponse<DataMaster> response = elasticsearchClient.update(updateRequest, DataMaster.class);
		return response.result().toString();
	}
	
}


