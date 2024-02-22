<%@ include file="session.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="keywords" content="">
    <meta name="author" content="">
    <link rel="icon" href="assets/images/favicon/favicon.png" type="image/x-icon">
    <link rel="shortcut icon" href="assets/images/favicon/favicon.png" type="image/x-icon">
    <title>Reset password</title>
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
</head>
<body>
    <!-- tap on top starts-->
    <div class="tap-top"><i data-feather="chevrons-up"></i></div>
    <!-- tap on tap ends-->
    <!-- Loader starts-->
    <div class="loader-wrapper">
        <div class="dot"></div>
        <div class="dot"></div>
        <div class="dot"></div>
        <div class="dot"> </div>
        <div class="dot"></div>
    </div>
    <!-- Loader ends-->
    <!-- page-wrapper Start-->
    <div class="page-wrapper">
	<div class="container-fluid p-0">
		<div class="row">
			<div class="col-12">
				<div class="login-card">
					<div>
						<div>
							<a class="logo" href="index.html">
								<img class="img-fluid for-light" src="assets/images/logo/logo-24-7-color.png" alt="looginpage" width="110px">
							</a>
						</div>
						<div class="login-main">
							<form class="theme-form" id="resetPasswordForm">
							<input type="hidden" value=<%=session.getAttribute("userId")%> id="userId" name="userId">
								<h4 class="text-center">Reset Your Password</h4>
								<p class="text-center">Enter your New password here</p>
								<span id="errorMsg2"></span>
								<div class="form-group">
									<label class="col-form-label">New Password</label>
									<div class="form-input position-relative">
										<input class="form-control" type="password" id="password" name="password" required="" placeholder="*********">
										<div class="show-hide">
											<span class="show"></span>
										</div>
									</div>
								</div>
								<div class="form-group">
									<label class="col-form-label">Retype Password</label>
									<input class="form-control" type="password" id="retypepassword" name="retypepassword" required="" placeholder="*********">
								</div>
								<span id="errorMsg1"></span>
								<div class="form-group mb-0">
									<button class="btn btn-primary btn-block w-100 mt-3" type="submit">Reset it Now</button>
								</div>
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
	</div>
    </div>
    <!-- page-wrapper Ends-->
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
    <script src="${pageContext.request.contextPath}/path.js"></script>
    
    
    <script>
    // Function to check if passwords match
    function validatePassword() {
        var password = document.getElementById("password").value;
        var retypepassword = document.getElementById("retypepassword").value;

        if (password !== retypepassword) {
            $("#errorMsg1").removeClass("text-danger").addClass("text-danger").text(
			"Enter valid password");

            return false;
        }
        return true;
    }

        // Function to handle form submission
        function handleFormSubmit(event) {
            event.preventDefault();

            var isValid = validatePassword();
            if (isValid) {
                var userId = document.getElementById("userId").value;
                var password = document.getElementById("password").value;
                var url = mainUrl + "forgotpassword?userId=" + userId + "&password=" + password;

                // Perform AJAX request
                $.ajax({
                    url: url,
                    method: "POST",
                    success: function (response) {
                        $("#errorMsg2").removeClass("text-success").addClass("text-success").text(
						"Password reset successful. Please sign in again.");
                        
                    },
                    error: function (xhr, status, error) {
                        $("#errorMsg2").removeClass("text-danger").addClass("text-danger").text(
						"Failed to reset password. Please try again later.");
                    }
                });
            }
        }

        // Attach event listener to the form submit event
        var resetPasswordForm = document.getElementById("resetPasswordForm");
        resetPasswordForm.addEventListener("submit", handleFormSubmit);
    </script>
</body>
</html>
