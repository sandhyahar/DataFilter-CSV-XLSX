<%@page import="org.json.JSONArray"%>
<%@ page import="org.json.JSONObject"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="javax.servlet.http.HttpSession"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

	<%
	// Get the param value from the request
	String param = request.getParameter("param");

	if (param != null && !param.isEmpty()) {
		try {
			// Parse the JSON param value
			JSONObject paramObject = new JSONObject(param);

			JSONArray dataArray = paramObject.getJSONArray("data");
			if (dataArray.length() > 0) {
		// Get the first object from the dataArray
		JSONObject dataObject = dataArray.getJSONObject(0);
		// Get the userId from the dataObject
		String userId = dataObject.getString("userId");
		//create session
		HttpSession session2 = request.getSession();
		// session timeout 
		session2.setMaxInactiveInterval(3600);
		// Set the userId in the session
		session2.setAttribute("userId", userId);
		// Redirect to dashboard.jsp
		response.sendRedirect("dashboard.jsp");
			} else {
		out.print("No data found in the param.");
			}

		} catch (Exception e) {
			out.print("Error parsing the param value: " + e.getMessage());
		}

	}
	%>

</body>
</html>