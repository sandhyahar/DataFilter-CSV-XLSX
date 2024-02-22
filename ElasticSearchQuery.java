package com.API.Repository;

//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import co.elastic.clients.elasticsearch.core.*;
//import co.elastic.clients.elasticsearch.core.search.Hit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.API.Document.UserMst;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//@Repository
public class ElasticSearchQuery {
//	@Autowired
//	private ElasticsearchClient elasticsearchClient;
//
//	private final String indexName = "user_mst";
//
//	public String createOrUpdateDocument(UserMst usermst) throws IOException {
//
//		IndexResponse response = elasticsearchClient
//				.index(i -> i.index(indexName).document(usermst));
//		if (response.result().name().equals("Created")) {
//			return new StringBuilder("Document has been successfully created.").toString();
//		} else if (response.result().name().equals("Updated")) {
//			return new StringBuilder("Document has been successfully updated.").toString();
//		}
//		return new StringBuilder("Error while performing the operation.").toString();
//	}
//
//	public UserMst getDocumentById(String userid) throws IOException {
//		UserMst usermst = null;
//		GetResponse<UserMst> response = elasticsearchClient.get(g -> g.index(indexName).id(userid), UserMst.class);
//
//		if (response.found()) {
//			usermst = response.source();
//			System.out.println("Product name " + usermst.getId());
//		} else {
//			System.out.println("Product not found");
//		}
//
//		return usermst;
//	}
//
//	public String deleteDocumentById(String userid) throws IOException {
//
//		DeleteRequest request = DeleteRequest.of(d -> d.index(indexName).id(userid));
//
//		DeleteResponse deleteResponse = elasticsearchClient.delete(request);
//		if (Objects.nonNull(deleteResponse.result()) && !deleteResponse.result().name().equals("NotFound")) {
//			return new StringBuilder("Product with id " + deleteResponse.id() + " has been deleted.").toString();
//		}
//		System.out.println("Product not found");
//		return new StringBuilder("Product with id " + deleteResponse.id() + " does not exist.").toString();
//
//	}
//
//	public List<UserMst> searchAllDocuments() throws IOException {
//
//		SearchRequest searchRequest = SearchRequest.of(s -> s.index(indexName));
//		SearchResponse searchResponse = elasticsearchClient.search(searchRequest, UserMst.class);
//		List<Hit> hits = searchResponse.hits().hits();
//		List<UserMst> products = new ArrayList<>();
//		for (Hit object : hits) {
//
//			System.out.print(((UserMst) object.source()));
//			products.add((UserMst) object.source());
//
//		}
//		return products;
//	}
}
