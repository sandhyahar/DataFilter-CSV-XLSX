package com.API.Events;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.util.ObjectUtils;

import com.API.Common.QueryBuilderUtils;
import com.API.pojo.TempData;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

public class TempDataUploadFileThread implements Runnable {

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
				TempData dm = new TempData();
				dm.setStatus(1);
				List<Query> query1 = prepareQueryList(dm);

				SearchResponse<TempData> search = elasticsearchClient.search(
						req -> req.index("temp_data_mst").query(query -> query.bool(bool -> bool.must(query1))),
						TempData.class);

				if (search.hits().hits().size() > 0) {
					for (Hit<TempData> hit : search.hits().hits()) {
						String id = hit.id();
						TempData tempMaster = hit.source();
						tempMaster.setStatus(2);
						String res = updateTempData(tempMaster, id);
						tempMaster.setId(id);
						if (res.equalsIgnoreCase("updated")) {
							ProcessTempDataThread dmp = new ProcessTempDataThread(tempMaster, elasticsearchClient,restHighLevelClient);
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

	private List<Query> prepareQueryList(TempData tm) {
		Map<String, Integer> conditionMap = new HashMap<>();
		conditionMap.put("status", tm.getStatus());
		return conditionMap.entrySet().stream().filter(entry -> !ObjectUtils.isEmpty(entry.getValue()))
				.map(entry -> QueryBuilderUtils.termQuery(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

	public String updateTempData(TempData tm, String id) throws IOException {
		UpdateRequest<TempData, TempData> updateRequest = UpdateRequest
				.of(req -> req.index("temp_data_mst").id(id).doc(tm));
		UpdateResponse<TempData> response = elasticsearchClient.update(updateRequest, TempData.class);
		return response.result().toString();
	}

}
