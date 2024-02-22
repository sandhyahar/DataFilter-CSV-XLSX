<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="keywords" content="">
    <meta name="author" content="">
    <link rel="icon" href="assets/images/favicon/favicon.png" type="image/x-icon">
    <link rel="shortcut icon" href="assets/images/favicon/favicon.png" type="image/x-icon">
    <title>Verify Mobile</title>
    <link rel="preconnect" href="https://fonts.googleapis.com/">
    <link rel="preconnect" href="https://fonts.gstatic.com/" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;0,800;0,900;1,100;1,200;1,300;1,400;1,500;1,600;1,700;1,800;1,900&amp;display=swap" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="assets/css/vendors/font-awesome.css">
    <!-- ico-font-->
    <link rel="stylesheet" type="text/css" href="assets/css/vendors/icofont.css">
    <!-- Themify icon-->
    <link rel="stylesheet" type="text/css" href="assets/css/vendors/themify.css">
    <!-- Flag icon-->
    <link rel="stylesheet" type="text/css" href="assets/css/vendors/flag-icon.css">
    <!-- Feather icon-->
    <link rel="stylesheet" type="text/css" href="assets/css/vendors/feather-icon.css">
    <!-- Bootstrap css-->
    <link rel="stylesheet" type="text/css" href="assets/css/vendors/bootstrap.css">
    <!-- App css-->
    <link rel="stylesheet" type="text/css" href="assets/css/style.css">
    <link id="color" rel="stylesheet" href="assets/css/color-1.css" media="screen">
    <!-- Responsive css-->
    <link rel="stylesheet" type="text/css" href="assets/css/responsive.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    
</head>
<body style="background: white;">

    <div class="container-fluid p-0">
	<div class="row m-0">
		<div class="col-xl-7 p-0">
			<img class="bg-img-cover bg-center" src="assets/images/login/1.jpg" alt="looginpage">
				</div><div class="col-xl-4 p-0">
		<!--  	<button style="margin-top: 200px" onclick="myFunction();">OTPfree</button>-->
		</div>

</div>
</div>
<%
    // Get form data
    String firstName = request.getParameter("firstName");
    String lastName = request.getParameter("lastName");
    String emailId = request.getParameter("emailId");
    String mobileNumber = request.getParameter("mobileNo");
    String password = request.getParameter("password");
    String userId = request.getParameter("userId");

%>
<form>
		<input type="hidden" value="<%= firstName %>" name="firstName" id="firstName">
		<input type="hidden" value="<%= lastName %>" name="lastName" id="lastName">
		<input type="hidden" value="<%= emailId %>" name="emailId" id="emailId">
		<input type="hidden" value="<%= mobileNumber %>" name="mobileNo" id="mobileNo">
		<input type="hidden" value="<%= password %>" name="password" id="password">
		<input type="hidden" value="<%= userId %>" name="userId" id="userId">

</form>
<div id="waauth"></div>

<script>
window.onload = function() {
	  // Call your function here
	  myFunction('mobile');
	};
	
    function validateAthentication(AuthDetail) {
        var status = AuthDetail.status;
        var mobile = AuthDetail.mobile;
        var countryCode = "91";
        var formattedMobileNumber = mobile.replace(countryCode, "");
        var mobileNumber = document.getElementById("mobileNo").value.trim();
        
        if (formattedMobileNumber != mobileNumber) {
            alert('Registration and verification numbers do not match..');
        }else{
        	createUser();
        }
        
        return false;
    }
    
    function createUser() {
    	  var attr = {
    	    "firstName": document.getElementById("firstName").value.trim(),
    	    "lastName": document.getElementById("lastName").value.trim(),
    	    "emailId": document.getElementById("emailId").value.trim(),
    	    "mobileNo": document.getElementById("mobileNo").value,
    	    "password": document.getElementById("password").value.trim(),
    	    "userId": document.getElementById("userId").value.trim(),
    	  };

    	  // Check user ID uniqueness
    	  $.ajax({
    	    url:  mainUrl + "checkUniqueUserId",
    	    type: "POST",
    	    contentType: "application/json",
    	    data: JSON.stringify(attr),
    	    success: function (response) {
    	      var obj = JSON.parse(response);
    	      if (obj.flag === true) {
    	        // User ID is unique, proceed with creating the account
    	        $.ajax({
    	          url: mainUrl + "createUser",
    	          type: "POST",
    	          contentType: "application/json",
    	          data: JSON.stringify(attr),
    	          success: function (response) {
    	            var obj = JSON.parse(response);
    	            if (obj.flag == true) {
    	              window.location.href =
    	                "signin.jsp?message=Create+your+account+successfully&color=green";
    	            } else if(obj.flag == false){
    	              window.location.href =
    	                "signup.jsp?message=Please+enter+a+unique+mobile+number&color=red";
    	            }
    	          },
    	        });
    	      } else {
    	        // User ID already exists
    	        window.location.href =
    	          "signup.jsp?message=UserId+already+exists.&color=red";
    	      }
    	    },
    	  });
    	}
</script>
<script src="https://otpfree.com/ujs/3-enc.js"></script>
<script src="${pageContext.request.contextPath}/path.js"></script>
</body>
</html>
