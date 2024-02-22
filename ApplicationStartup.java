package com.API.Servlet;

import org.springframework.stereotype.Component;

import com.API.Events.CampaignDownladThread;
import com.API.Events.DataMasterUploadFileThread;
import com.API.Events.DownloadDataThread;
import com.API.Events.FilterUploadFileThread;
import com.API.Events.TempDataUploadFileThread;
import com.API.Service.ParamMstService;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	ElasticsearchClient elasticsearchClient;

	@Autowired
	RestHighLevelClient restHighLevelClient;

	@Autowired
	ParamMstService paramMstService;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		// TODO Auto-generated method stub
		FilterUploadFileThread FUFT = new FilterUploadFileThread();
		FUFT.setElasticsearchClient(elasticsearchClient);
		FUFT.setParamMstService(paramMstService);
		FUFT.setResthighLevelClient(restHighLevelClient);
		Thread th = new Thread(FUFT);

		
		DataMasterUploadFileThread DMUFT = new DataMasterUploadFileThread();
		DMUFT.setElasticsearchClient(elasticsearchClient);
		DMUFT.setRestHighLevelClient(restHighLevelClient);
		Thread dm = new Thread(DMUFT);

		
		DownloadDataThread DDT = new DownloadDataThread();
		DDT.setElasticsearchClient(elasticsearchClient);
		DDT.setRestHighLevelClient(restHighLevelClient);
		DDT.setParamMstService(paramMstService);
		Thread dt = new Thread(DDT);

		
		TempDataUploadFileThread TDT = new TempDataUploadFileThread();
		TDT.setElasticsearchClient(elasticsearchClient);
		TDT.setRestHighLevelClient(restHighLevelClient);
		Thread tm = new Thread(TDT);

		
		
		CampaignDownladThread CDT = new CampaignDownladThread();
		CDT.setElasticsearchClient(elasticsearchClient);
		CDT.setParamMstService(paramMstService);
		Thread cd = new Thread(CDT);

		
		
		
		th.start();
		cd.start();
		dm.start();
		dt.start();
		tm.start();

	}
}
