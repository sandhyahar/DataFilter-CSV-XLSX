package com.API.Common;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.API.pojo.ApiKeyMst;
import com.google.gson.JsonObject;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

@Component
public class SessionFilter implements Filter {

	@Autowired
	ElasticsearchClient elasticsearchClient;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest request1 = (HttpServletRequest) request;
		// HttpServletResponse response1 = (HttpServletResponse) response;
		String pagename = request1.getRequestURI();
		if (!pagename.contains("/rest/")) {
			pagename = pagename.substring(pagename.lastIndexOf("/") + 1, pagename.length());
			System.out.println("Enter to pagename = " + pagename);
			if (!pagename.trim().equalsIgnoreCase("") && (!pagename.trim().endsWith(".css")
					&& !pagename.trim().endsWith(".png") && !pagename.trim().endsWith(".jpeg")
					&& !pagename.trim().endsWith(".jpg") && !pagename.trim().endsWith(".mp4")
					&& !pagename.trim().endsWith(".js") && !pagename.trim().endsWith(".ttf")
					&& !pagename.trim().endsWith(".woff") && !pagename.trim().endsWith(".html")
					&& !pagename.trim().equalsIgnoreCase("login.jsp")
					&& !pagename.trim().equalsIgnoreCase("verify-mobile.jsp")
					&& !pagename.trim().equalsIgnoreCase("signin.jsp")
					&& !pagename.trim().equalsIgnoreCase("testing.jsp")
					&& !pagename.trim().equalsIgnoreCase("createsession.jsp")
					&& !pagename.trim().equalsIgnoreCase("verifyOtp")
					&& !pagename.trim().equalsIgnoreCase("RegisterUser") && !pagename.trim().equalsIgnoreCase("login")
					&& !pagename.trim().equalsIgnoreCase(".zip") && !pagename.trim().equalsIgnoreCase("signup.jsp"))) {
				if (request1.getSession() == null || request1.getSession().getAttribute("userId") == null) {
					request1.getSession().invalidate();
					((HttpServletResponse) response).sendRedirect(request1.getContextPath() + "/signin.jsp");
					return; // Make sure to return after sending the redirect
				}

			}
		} else {
			JSONObject res = new JSONObject();
			String user = "", password = "";
			String authorizationHeader = request1.getHeader("Authorization");
			String userpass = isUserAuthenticated(authorizationHeader);

			if (userpass != null && !userpass.equalsIgnoreCase("") && userpass.contains(":")) {
				String userpassarr[] = userpass.split(":");
				user = userpassarr[0];
				password = userpassarr[1];

				ApiKeyMst api = new ApiKeyMst();
				api.setUserId(user);
				api.setPassword(password);

				List<Query> queryList = prepareQueryList(api);

				SearchResponse<ApiKeyMst> searchResponse = elasticsearchClient.search(
						req -> req.index("api_key_mst").query(query -> query.bool(bool -> bool.must(queryList))),
						ApiKeyMst.class);

				if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
					request.setAttribute("Authorization", authorizationHeader);
				}

				List<Hit<ApiKeyMst>> hits = searchResponse.hits().hits();

				for (Hit<ApiKeyMst> hit : hits) {
					if (hit != null) {
						String userId = hit.source().getUserId();
						String passwords = hit.source().getPassword();

						if (userId.equals(user) && passwords.equals(password)) {
							// System.out.println(authorizationHeader);
							chain.doFilter(request, response);

						}
					}
				}

				HttpServletResponse httpResponse = (HttpServletResponse) response;
				httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				res.put("message", "Authorization failed");
				httpResponse.getWriter().write(res.toString());
				return;
			}

		}
		chain.doFilter(request, response);

	}

	private List<Query> prepareQueryList(ApiKeyMst apis) {
		Map<String, String> conditionMap = new HashMap<>();
		if (apis.getPassword() != null && !apis.getPassword().equalsIgnoreCase(""))
			conditionMap.put("password.keyword", apis.getPassword());
		if (apis.getUserId() != null && !apis.getUserId().equalsIgnoreCase(""))
			conditionMap.put("userId.keyword", apis.getUserId());
		return conditionMap.entrySet().stream().filter(entry -> !ObjectUtils.isEmpty(entry.getValue()))
				.map(entry -> QueryBuilderUtils.termQuery(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

	private String isUserAuthenticated(String authString) {

		String decodedAuth = "";
		String[] authParts = authString.split("\\s+");
		String authInfo = authParts[1];
		// Decode the data back to original string
		byte[] bytes = null;
		try {
			bytes = Base64.getDecoder().decode(authInfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		decodedAuth = new String(bytes);

		return decodedAuth;
	}
}
