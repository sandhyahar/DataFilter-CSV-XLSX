package com.API.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.catalina.connector.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.API.Common.QueryBuilderUtils;
import com.API.Common.common;
import com.API.Document.UserMst;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

@RestController
public class UserRestController {


	@Autowired
	ElasticsearchClient elasticsearchClient;

	@PostMapping("/createUser")
	public String createOrUpdateDocument(@RequestBody UserMst user) throws IOException {
		JSONObject res = new JSONObject();
		boolean flag;
		
		
		SearchResponse<UserMst> search = elasticsearchClient.search(
				s -> s.index("user_mst")
						.query(q -> q.term(t -> t.field("mobileNo").value(v -> v.stringValue(user.getMobileNo())))),
				UserMst.class);
		

        // Check if user mobile number already exists
		if (search.hits().hits().size() > 0) {
			flag=false;
			res.put("message", "User mobile number already exist.");
			res.put("flag", flag);

			return res.toString();
		} 
				
		 // Encrypt the password
	    String encryptedPassword = common.encryptStringAdvance(user.getPassword());
	    // Set the encrypted password in the UserMst object
	    user.setPassword(encryptedPassword);
		
		 IndexRequest<UserMst> indexRequest = new IndexRequest.Builder<UserMst>()
		        .index("user_mst")
		        .document(user)
		        .build();
		    elasticsearchClient.index(indexRequest);
		    flag=true;
		    res.put("message", "User registered successfully.");
		    res.put("flag", flag);

		  		return res.toString();
	}
	
	@PostMapping("/checkUniqueUserId")
	public String getUniqueUserId(@RequestBody UserMst user) throws IOException {
	    JSONObject res = new JSONObject();
	    boolean flag;
	    
	    // Check if userId already exists
	    SearchResponse<UserMst> userIdSearch = elasticsearchClient.search(
	        s -> s.index("user_mst")
	            .query(q -> q.match(m -> m.field("userId").query(user.getUserId()))),
	        UserMst.class);
	    
	    if (userIdSearch.hits().hits().size() > 0) {
	        flag = false;
	        res.put("message", "User ID already exists.");
	    } else {
	        flag = true;
	        res.put("message", "You can use this userId.");
	    }
	    
	    res.put("flag", flag);
	    return res.toString();
	}
	

	
	private List<Query> prepareQueryList(UserMst um) {
		Map<String, String> conditionMap = new HashMap<>();
		if (um.getPassword() != null && !um.getPassword().equalsIgnoreCase(""))
		    // Encrypt the entered password for comparison
			conditionMap.put("password.keyword",common.encryptStringAdvance(um.getPassword()));

		if (um.getUserId() != null && !um.getUserId().equalsIgnoreCase(""))
			conditionMap.put("userId.keyword", um.getUserId());

		if (um.getId() != null && !um.getId().equalsIgnoreCase(""))
			conditionMap.put("_id", um.getId());
		
		
		return conditionMap.entrySet().stream().filter(entry -> !ObjectUtils.isEmpty(entry.getValue()))
				.map(entry -> QueryBuilderUtils.termQuery(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}
		 
	
	@PostMapping("/login")
	public String getLoginData(@RequestBody UserMst um) throws IOException {
	    JSONObject res = new JSONObject();
	    boolean flag = false;
	    
	    List<Query> queryList = prepareQueryList(um);
	    SearchResponse<UserMst> searchResponse = elasticsearchClient.search(
	        req -> req.index("user_mst")
	                  .query(query -> query.bool(bool -> bool.must(queryList))),
	        UserMst.class
	    );
	     
	    if (!searchResponse.hits().hits().isEmpty()) {
	        flag = true;
	        res.put("flag", flag);
	        res.put("message", "Login successful.");

	        List<JSONObject> sourceValues = new ArrayList<>();
	        for (Hit<UserMst> hit : searchResponse.hits().hits()) {
	            JSONObject source = new JSONObject(hit.source());
	            sourceValues.add(source);
	        }
	        res.put("data", sourceValues);
	    } else {
	        flag = false;
	        res.put("flag", flag);
	        res.put("message", "User ID and password do not match.");
	        
	    }

	    return res.toString();
	}	

	@PostMapping("/forgotpassword")
	public String forgotPassword(String userId,String password) throws IOException {
	    JSONObject res = new JSONObject();
	    
	    // Check if the userId exists in your user database
	    SearchResponse<UserMst> searchResponse = elasticsearchClient.search(
	        req -> req.index("user_mst")
	            .query(q -> q.term(t -> t.field("userId").value(userId))),
	        UserMst.class
	    );

	    List<Hit<UserMst>> hits = searchResponse.hits().hits();

	    for (Hit<UserMst> hit : hits) {
	        UserMst user = hit.source();   
	        // Encrypt the password
		    String encryptedPassword = common.encryptStringAdvance(password);
		    user.setPassword(encryptedPassword);

	        UpdateRequest<UserMst, UserMst> updateRequest = UpdateRequest
	            .of(req -> req.index("user_mst").id(hit.id()).doc(user));
	        UpdateResponse<UserMst> response = elasticsearchClient.update(updateRequest, UserMst.class);
	    }

	    res.put("message", "Password updated successfully.");
	    return res.toString();
	}
	
	@GetMapping("/rest/test")
	public ResponseEntity<String> restLogin(@RequestHeader("Authorization") String authString) {
	    return ResponseEntity.ok("User Authorization successfully.");
	}
}
		
	