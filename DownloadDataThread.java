package com.API.Events;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.util.ObjectUtils;

import com.API.Common.QueryBuilderUtils;
import com.API.Service.ParamMstService;
import com.API.pojo.DataMaster;
import com.API.pojo.FilterDataMaster;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

public class DownloadDataThread implements Runnable {
	
	ElasticsearchClient elasticsearchClient;
	
	RestHighLevelClient restHighLevelClient;
	
	ParamMstService paramMstService;
	
	ServletContext servletContext;

	
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
	
	public ParamMstService getParamMstService() {
		return paramMstService;
	}

	public void setParamMstService(ParamMstService paramMstService) {
		this.paramMstService = paramMstService;
	}
	

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			// System.out.println("Thread Stated");
			try {
				FilterDataMaster fd = new FilterDataMaster();
				fd.setStatus(1);
				List<Query> query1 = prepareQueryList(fd);
				
				SearchResponse<FilterDataMaster> search = elasticsearchClient.search(
						req -> req.index("download_data_mst").query(query -> query.bool(bool -> bool.must(query1))),
						FilterDataMaster.class);

				if (search.hits().hits().size() > 0) {
					for (Hit<FilterDataMaster> hit : search.hits().hits()) {
						String id = hit.id();
						FilterDataMaster fdm = hit.source();
						fdm.setStatus(2);
						String res = updateDataMaster(fdm, id);
						fdm.setId(id);
						if (res.equalsIgnoreCase("updated")) {
							ProcessDownloadDataThread dmp = new ProcessDownloadDataThread(fdm,elasticsearchClient,restHighLevelClient,paramMstService);
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

	private List<Query> prepareQueryList(FilterDataMaster fd) {
		Map<String, Integer> conditionMap = new HashMap<>();
		conditionMap.put("status", fd.getStatus());
		return conditionMap.entrySet().stream().filter(entry -> !ObjectUtils.isEmpty(entry.getValue()))
				.map(entry -> QueryBuilderUtils.termQuery(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

	public String updateDataMaster(FilterDataMaster fd, String id) throws IOException {
		UpdateRequest<FilterDataMaster, FilterDataMaster> updateRequest = UpdateRequest
				.of(req -> req.index("download_data_mst").id(id).doc(fd));
		UpdateResponse<FilterDataMaster> response = elasticsearchClient.update(updateRequest, FilterDataMaster.class);
		return response.result().toString();
	}
	
}



