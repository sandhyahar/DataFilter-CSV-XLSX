<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="keywords" content="">
<meta name="author" content="">
<link rel="icon" href="assets/images/favicon/favicon.png"
	type="image/x-icon">
<link rel="shortcut icon" href="assets/images/favicon/favicon.png"
	type="image/x-icon">
<title>Sign in</title>
<link rel="preconnect" href="https://fonts.googleapis.com/">
<link rel="preconnect" href="https://fonts.gstatic.com/" crossorigin>
<link
	href="https://fonts.googleapis.com/css2?family=Montserrat:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;0,800;0,900;1,100;1,200;1,300;1,400;1,500;1,600;1,700;1,800;1,900&amp;display=swap"
	rel="stylesheet">
<link rel="stylesheet" type="text/css"
	href="assets/css/vendors/font-awesome.css">
<!-- ico-font-->
<link rel="stylesheet" type="text/css"
	href="assets/css/vendors/icofont.css">
<!-- Themify icon-->
<link rel="stylesheet" type="text/css"
	href="assets/css/vendors/themify.css">
<!-- Flag icon-->
<link rel="stylesheet" type="text/css"
	href="assets/css/vendors/flag-icon.css">
<!-- Feather icon-->
<link rel="stylesheet" type="text/css"
	href="assets/css/vendors/feather-icon.css">
<!-- Bootstrap css-->
<link rel="stylesheet" type="text/css"
	href="assets/css/vendors/bootstrap.css">
<!-- App css-->
<link rel="stylesheet" type="text/css" href="assets/css/style.css">
<link id="color" rel="stylesheet" href="assets/css/color-1.css"
	media="screen">
<!-- Responsive css-->
<link rel="stylesheet" type="text/css" href="assets/css/responsive.css">

</head>
<body>
	<!-- Loader starts-->
	<div class="loader-wrapper">
		<div class="dot"></div>
		<div class="dot"></div>
		<div class="dot"></div>
		<div class="dot"></div>
		<div class="dot"></div>
	</div>
	<!-- Loader ends-->
	<!-- login page start-->
	<div class="container-fluid">
		<div class="row">
			<div class="col-xl-7">
				<img class="bg-img-cover bg-center" src="assets/images/login/1.jpg"
					alt="looginpage">
			</div>
			<div class="col-xl-5 p-0">
				<div class="login-card">
					<div>
						<div>
							<a class="logo" href="signin.jsp"> 
							<h2 class="text-dark p-3 mb-4">Filter Data</h2>
							</a>
						</div>
						<div class="login-main">
							<form class="theme-form" name="myForm" id="myForm">
							<input type="hidden" name="param" id="param" value="">
								<h4 class="text-center">Sign in to account</h4>
								<%
								String message = request.getParameter("message");
								String color = request.getParameter("color");
								%>
								<%
								if (message != null && !message.isEmpty()) {
								%>
								<span style="color: <%=color%>;"><%=message%></span>
								<%
								}
								%>
								<p class="text-center">Enter your UserId & password to login</p>
								<span id="errorMsg" style="color: red;"></span>
								
								<div class="form-group">
									<label class="col-form-label">Enter UserId</label> 
									<input
										class="form-control" type="text" required="" name="userId"
										id="userId" placeholder="userid">
								</div>
								<div class="form-group">
									<label class="col-form-label">Password</label>
									<div class="form-input position-relative">
										<input class="form-control" type="password" name="password"
											id="password" required="" placeholder="Enter your password">
										<div class="show-hide">
											<span class="show"></span>
										</div>
									</div>
								</div>
								<div class="form-group mb-0">
									<div class="text-end mt-3">
										<input type="submit" class="btn btn-primary btn-block w-100 mt-3" id="submitBtn" value="Submit">
									</div>
								</div>
								<p class="mt-4 mb-0 text-center">
									Don't have account? <a class="ms-2" href="signup.jsp">Create
										Account</a>
								</p>
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- latest jquery-->
		<script src="assets/js/jquery-3.6.0.min.js"></script>
		<!-- Bootstrap js-->
		<script src="assets/js/bootstrap/bootstrap.bundle.min.js"></script>
		<!-- feather icon js-->
		<script src="assets/js/icons/feather-icon/feather.min.js"></script>
		<script src="assets/js/icons/feather-icon/feather-icon.js"></script>
		<!-- scrollbar js-->
		<!-- Sidebar jquery-->
		<script src="assets/js/config.js"></script>
		<!-- Template js-->
		<script src="assets/js/script.js"></script>
		<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
		<script src="${pageContext.request.contextPath}/path.js"></script>

		<!-- login js-->
	</div>
</body>
</html>
<script>
$(document).ready(function() {
	  $('#submitBtn').click(function() {
		  
		  event.preventDefault(); // Prevent default form submission

		    var userId = document.getElementById("userId").value.trim();
		    var password = document.getElementById("password").value.trim();

		    // Check if userId and password are not empty
		    if (userId === "" || password === "") {
		    	 $('#errorMsg').text("Please enter both UserId and Password");
		      return; // Exit the function if fields are empty
		    }
		 
	    var attr = {
	      "password" : password,
	      "userId" : userId
	    };
		  
	    $.ajax({
	      url : mainUrl + "login",
	      type : "POST",
	      contentType : "application/json",
	      data : JSON.stringify(attr),
	      success : function(response) {
	    	  var obj = JSON.parse(response);
	    	  if (obj.flag === true) {
	    	      $("#param").val(response);
	    	      // Set the action of the form to createsession.jsp
	              $('#myForm').attr('action', 'createsession.jsp');
	              // Submit the form
	              $('#myForm').submit();	
	    	   }else{
	    		   $('#errorMsg').text("UserId or password is wrong"); // Set the error message within the span element with id "errorMsg"
	    	   }
	      	 }
	    });
	  });
	});
</script>