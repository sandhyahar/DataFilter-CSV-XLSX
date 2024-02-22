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
			<img class="bg-img-cover bg-center" src="assets/images/login/1.jpg" alt="looginpage">
		</div>
		<div class="col-xl-5 p-0">
			<div class="login-card">
				<div>
					<div>
						<a class="logo text-center">
							<img class="img-fluid for-light" src="assets/images/logo/logo-24-7-color.png" alt="looginpage" width="150px">
						</a>
					</div>
					<div class="login-main">
						<form class="theme-form" action="select-industry.html">
							<h4 class="text-center">Verify Mobile Number</h4>
							<p class="text-center">A code has been sent to *******9897</p>
                            <div class="form-group">
								<label class="col-form-label">Enter OTP</label>
    					        <div class="text-center"> 
								    <div id="otp" class="inputs d-flex flex-row justify-content-center mt-2"> 
								        <input class="m-2 text-center form-control rounded" type="text" id="first" maxlength="1" /> 
								        <input class="m-2 text-center form-control rounded" type="text" id="second" maxlength="1" /> 
								        <input class="m-2 text-center form-control rounded" type="text" id="third" maxlength="1" /> 
								        <input class="m-2 text-center form-control rounded" type="text" id="fourth" maxlength="1" /> 
								        <input class="m-2 text-center form-control rounded" type="text" id="fifth" maxlength="1" /> 
								        <input class="m-2 text-center form-control rounded" type="text" id="sixth" maxlength="1" /> 
								    </div> 
    					        </div> 
							</div>
							<div class="form-group mb-0">
								<button class="btn btn-primary btn-block w-100 mt-3" type="submit">Submit</button>
							</div>
							<br/>
							<div class="card-2"> 
					            <div class="content d-flex justify-content-center align-items-center"> 
					                <span>Didn't get the code</span> <a href="#" class="text-decoration-none ms-3">Resend(0/3)</a> 
					            </div> 
					        </div> 
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
	<script>
	    document.addEventListener("DOMContentLoaded", function(event) {
   
        function OTPInput() {
            const editor = document.getElementById('first');
            editor.onpaste = pasteOTP;
    
            const inputs = document.querySelectorAll('#otp > *[id]');
            for (let i = 0; i < inputs.length; i++) { 
                inputs[i].addEventListener('input', function(event) { 
                    if(!event.target.value || event.target.value == '' ){
                        if(event.target.previousSibling.previousSibling){
                            event.target.previousSibling.previousSibling.focus();    
                        }
                    
                    }else{ 
                        if(event.target.nextSibling.nextSibling){
                            event.target.nextSibling.nextSibling.focus();
                        }
                    }               
                });             
            } 
        } 
        OTPInput(); 
        });
    
        function pasteOTP(event){
            event.preventDefault();
            let elm = event.target;
            let pasteVal = event.clipboardData.getData('text').split("");
            if(pasteVal.length > 0){
                while(elm){
                    elm.value = pasteVal.shift();
                    elm = elm.nextSibling.nextSibling;
                }
            }
        }
	</script>
    </div>
</body>
</html>