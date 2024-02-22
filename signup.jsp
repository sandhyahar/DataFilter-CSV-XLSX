<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.com">
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
<title>Sign up</title>
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
<script src="${pageContext.request.contextPath}/path.js"></script>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
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
	<div class="container-fluid p-0">
		<div class="row m-0">
			<div class="col-xl-7 p-0">
				<img class="bg-img-cover bg-center" src="assets/images/login/1.jpg"
					alt="looginpage">
			</div>
			<div class="col-xl-5 p-0">
				<div class="login-card">
					<div>
						<div>
							<a class="logo text-center" href="signin.jsp">
								<h2 class="text-dark">Filter Data</h2>
							</a>
						</div>
						<div class="login-main">
							<form class="theme-form" action="testing.jsp" method="post"
								id="myform">
								<h4 class="text-center">Create your account</h4>
								<span id="errorMsg1" style="color: red;"></span>
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

								<!--<p class="text-center">Enter your details to create account</p>-->
								<div class="form-group">
									<label class="col-form-label pt-0">Your Name</label>
									<div class="row g-2">
										<div class="col-6">
											<input class="form-control" type="text" required=""
												placeholder="First name" name="firstName" id="firstName">
										</div>
										<div class="col-6">
											<input class="form-control" type="text" required=""
												placeholder="Last name" name="lastName" id="lastName">
										</div>
									</div>
								</div>
								<div class="form-group">
									<label class="col-form-label">Email Address</label> <input
										class="form-control" type="email" required=""
										placeholder="Test@gmail.com" name="emailId" id="emailId">
								</div>
								<div class="form-group">
									<label class="col-form-label">Mobile Number</label> <input
										class="form-control" type="tel" required="" name="mobileNo"
										placeholder="MobileNumber" id="mobileNo" maxlength="10">
									<span id="errorMsg" style="color: red;"></span>
								</div>
								<div class="form-group">
									<label class="col-form-label">Password</label>
									<div class="form-input position-relative">
										<input class="form-control" type="password" required=""
											name="password" placeholder="*********" id="password">
										<div class="show-hide">
											<span class="show"></span>
										</div>
									</div>
								</div>
								<label>UserId</label>
								<div class="input-group mb-3">
									<div class="input-group-prepend">
										<input type="text" class="form-control"
											placeholder="Enter UserId" aria-label=""
											aria-describedby="basic-addon1" id="userId" name="userId">
									</div>
									<button class="btn btn-primary" type="button"
										id="checkUserIdBtn" onclick="checkUserId();">Check-UserId</button>
									<span id="errorMsg2"></span>

								</div>

								<div class="col-6"></div>
								<div class="form-group mb-0">
									<div class="checkbox p-0">
										<input id="checkbox1" type="checkbox"> <label
											class="text-muted" for="checkbox1">Agree with <a
											class="ms-2" href="#">Terms & Condtions</a>
										</label> <br><span id="errorMsg3" style="color:red"></span>

									</div>
									<input type="submit"
										class="btn btn-primary btn-block w-100 mt-3" value="Submit"
										onclick="return validateForm();">

								</div>
								<span id="errorMsg"></span>
								<p class="mt-4 mb-0 text-center">
									Already have an account? <a class="ms-2" href="signin.jsp">Sign
										in</a>
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
		<!-- login js-->
		<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

	</div>

</body>
</html>

<script>
  function checkUserId() {
    var userId = document.getElementById("userId").value.trim();
    
    if (userId === "") {
      document.getElementById("errorMsg2").classList.add("text-danger");
      document.getElementById("errorMsg2").innerText = "UserId is empty.";
      return false; // Exit the function if userId is empty
    }

    var attr = {
      "userId": userId
    };

    return new Promise(function(resolve, reject) {
      $.ajax({
        url: mainUrl + "checkUniqueUserId",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(attr),
        success: function(response) {
          var obj = JSON.parse(response);
          if (obj.flag === true) {
            $("#errorMsg2").removeClass("text-danger").addClass("text-success").text("You can use this UserId...");
            resolve(true);
          } else {
            $("#errorMsg2").removeClass("text-success").addClass("text-danger").text("UserId already exists.");
            resolve(false);
          }
        },
        error: function(error) {
          console.log(error);
          reject(error);
        }
      });
    });
  }

  function validateForm() {
    var mobileNo = document.getElementById("mobileNo").value;
    var firstName = document.getElementById("firstName").value.trim();
    var lastName = document.getElementById("lastName").value.trim();
    var emailId = document.getElementById("emailId").value.trim();
    var password = document.getElementById("password").value.trim();
    var userId = document.getElementById("userId").value.trim();

    var regex = /^[0-9]{10}$/; // Regular expression to match 10 digits

    if (!regex.test(mobileNo)) {
      document.getElementById('errorMsg').innerText = "Invalid mobile number.";
      return false; // Prevent form submission
    }

    if (firstName === "" && lastName === "" && emailId === "" && password === "" && userId === "" && mobileNo === "") {
      document.getElementById('errorMsg1').innerText = "Required All Fields";
      return false;
    }

    checkUserId().then(function(flags) {
      if (!flags) {
        return false; // Prevent form submission if any user ID is invalid
      } else {
        // Allow form submission
         if (!document.getElementById("checkbox1").checked) {
        document.getElementById('errorMsg3').innerText = "Please agree to the Terms & Conditions.";
        return false; // Prevent form submission
       }
        document.getElementById('myform').submit();
        return true;

      }
    }).catch(function(error) {
      console.log(error);
    });

    return false; // Prevent form submission until the asynchronous check is complete
  }
</script>
