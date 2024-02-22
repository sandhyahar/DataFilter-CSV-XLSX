package com.API;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticRestHighLevelClient {

    private static final String USERNAME = "elastic";
    private static final String PASSWORD = "rootcause";
    
    
	@Bean
	public RestHighLevelClient getRestHighLevelClient() {
	    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
	    credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(USERNAME, PASSWORD));

	    RestHighLevelClient client = new RestHighLevelClient(
	            RestClient.builder(new HttpHost("localhost", 9200, "http"))
	                    .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
	    );
	    
	    return client;
	    
	}

//	
//	RestClient httpClient = RestClient.builder(
//		    new HttpHost("localhost", 9200)
//		).build();
//
//		// Create the HLRC
//		RestHighLevelClient hlrc = new RestHighLevelClientBuilder(httpClient)
//		    .setApiCompatibilityMode(true) 
//		    .build();
//
//		// Create the Java API Client with the same low level client
//		ElasticsearchClient transport = new RestClientTransport(
//		    httpClient,
//		    new JacksonJsonpMapper()
//		);
//
//		ElasticsearchClient esClient = new ElasticsearchClient(transport);

}