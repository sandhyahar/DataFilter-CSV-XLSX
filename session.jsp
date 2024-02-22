<% response.setHeader("Cache-Control","no-cache");
response.setHeader("Pragma","no-cache");
response.setDateHeader ("Expires", 0); %>

<% if(session.getAttribute("userId")== null)
{	
	session.invalidate(); 
%>
	<jsp:forward page="signin.jsp">
    <jsp:param name="validlogin" value="false"/>
	<jsp:param name="msg" value="Your Session has Expired.. Please Login Again" />
	</jsp:forward>
<%}%>