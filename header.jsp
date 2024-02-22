<!DOCTYPE html>

<%@page import="java.util.Date"%>
<%@page import="java.sql.*"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<html lang="en">
<head>
<title>Filter Data</title>

<!-- ... Your other CSS and script includes ... -->

<link href="assets/css/feather.css" rel="stylesheet" type="text/css">
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
<title>Dashboard</title>
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
<link rel="stylesheet" type="text/css"
	href="assets/css/vendors/scrollbar.css">
<link rel="stylesheet" type="text/css"
	href="assets/css/vendors/animate.css">
<link rel="stylesheet" type="text/css"
	href="assets/css/vendors/chartist.css">
<link rel="stylesheet" type="text/css"
	href="assets/css/vendors/prism.css">
<link rel="stylesheet" type="text/css"
	href="assets/css/vendors/vector-map.css">
<!-- Bootstrap css-->
<link rel="stylesheet" type="text/css"
	href="assets/css/vendors/bootstrap.css">
<!-- App css-->
<link rel="stylesheet" type="text/css" href="assets/css/style.css">
<link rel="stylesheet" type="text/css" href="mycss.css">
<link id="color" rel="stylesheet" href="assets/css/color-1.css"
	media="screen">
<!-- Responsive css-->
<link rel="stylesheet" type="text/css" href="assets/css/responsive.css">
<style>
.small-text {
	font-size: 13px;
}
</style>
<style type="text/css">
.submenu {
	display: none; /* Hide the submenu by default */
}

.sidebar-list:hover .submenu {
	display: block; /* Show the submenu when the parent is hovered */
}
</style>
<script>
	function showSubMenu() {
		var submenu = document.getElementById("submenu");
		submenu.style.display = "block";
	}

	function hideSubMenu() {
		var submenu = document.getElementById("submenu");
		submenu.style.display = "none";
	}
</script>
</head>
<body>
	<!-- tap on top starts-->
	<div class="tap-top">
		<i data-feather="chevrons-up"></i>
	</div>
	<!-- tap on tap ends-->
	<!-- Loader starts-->
	<div class="loader-wrapper">
		<div class="dot"></div>
		<div class="dot"></div>
		<div class="dot"></div>
		<div class="dot"></div>
		<div class="dot"></div>
	</div>
	<!-- Loader ends-->
	<!-- page-wrapper Start-->
	<div class="page-wrapper compact-wrapper" id="pageWrapper">
		<!-- Page Header Start-->
		<div class="page-header">
			<div class="header-wrapper row m-0">
				<div class="header-logo-wrapper col-auto p-0">
					<div class="toggle-sidebar">
						<i class="status_toggle middle sidebar-toggle" data-feather="grid"></i>
					</div>
					<div class="logo-header-main">
						<a href="index.jsp"> <img class="img-fluid for-light img-100"
							src="assets/images/logo/otpfree_white.png" alt=""> <img
							class="img-fluid for-dark"
							src="assets/images/logo/otpfree_white.png" alt="">
						</a>
					</div>
				</div>
				<div class="left-header col horizontal-wrapper ps-0">
					<div class="left-menu-header">
						<ul class="app-list">
							<li class="onhover-dropdown">
								<div class="app-menu">
									<i data-feather="folder-plus"></i>
								</div>
								<ul class="onhover-show-div left-dropdown">
									<li><a href="#">SMS</a></li>
									<li><a href="#">Whatsapp</a></li>
									<li><a href="#">Email</a></li>
								</ul>
							</li>
						</ul>
						<ul class="header-left">
							<li></li>

							<!--<li><a href="#"><span class="f-w-600">SMS</span><span style="margin-left: 25px;"><img src="assets/images/sms.png" alt="" title="" height="40px" width="40px"></span></a></li><li><a href="#"><span class="f-w-600">Whatsapp</span><span style="margin-left: 25px;"><img src="assets/images/whatsapp.png" alt="" title="" height="40px" width="40px"></span></a></li><li><a href="#"><span class="f-w-600">Email</span><span style="margin-left: 25px;"><img src="assets/images/email.png" alt="" title="" height="40px" width="40px"></span></a></li>-->
						</ul>
					</div>
				</div>
				<div class="nav-right col-6 pull-right right-header p-0">
					<ul class="nav-menus">


						<li class="maximize"><a href="#!"
							onclick="javascript:toggleFullScreen()"> <i
								data-feather="maximize-2"></i>
						</a></li>
						<li class="profile-nav onhover-dropdown">
							<div class="account-user">
								<i data-feather="user"></i>
							</div>
							<ul style="width: 205px;"
								class="profile-dropdown onhover-show-div">
								<li>

									<div class="mode" style="white-space: nowrap;">
										<a> <i data-feather="moon"></i><span
											style="font-size: 12px;">Change Theam</span></a>
									</div>
								</li>



								<li><a href="reset-password.jsp"> <i
										data-feather="save"></i> <span style="font-size: 12px;">change
											Password</span>
								</a></li>

								<li><a href="signin.jsp"> <i data-feather="log-in"></i>
										<span style="font-size: 12px;">Log out</span>
								</a></li>
							</ul>
						</li>
					</ul>
				</div>
				<script class="result-template" type="text/x-handlebars-template"> <div class="ProfileCard u-cf">
    					<div class="ProfileCard-avatar">
    						<svg
    							xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-airplay m-0">
    							<path d="M5 17H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2h-1"></path>
    							<polygon points="12 15 17 21 7 21 12 15"></polygon>
    						</svg>
    					</div>
    					<div class="ProfileCard-details">
    						<div class="ProfileCard-realName">{{name}}</div>
    					</div>
    				</div>
    			</script>
				<script class="empty-template" type="text/x-handlebars-template"> <div class="EmptyMessage">Your search turned up 0 results. This most likely means the backend is down, yikes!</div></script>
			</div>
		</div>
		<!-- Page Header Ends-->
		<!-- Page Body Start-->
		<div class="page-body-wrapper">
			<!-- Page Sidebar Start-->
			<div class="sidebar-wrapper">
				<div>
					<div class="logo-wrapper">
						<!-- <a href="#"> <img class="img-fluid for-light"
							src="assets/images/logo/otpfree_white.png" alt="" width="160px">
						</a> -->
						<a class="logo" href="singnin.jsp">
							<h2 class="text-white">Filter Data</h2>
						</a>
						<div class="back-btn">
							<i data-feather="grid"></i>
						</div>

					</div>
					<div class="logo-icon-wrapper">
						<a href="#">
							<div class="icon-box-sidebar">
								<i data-feather="grid"></i>
							</div>
						</a>
					</div>
					<nav class="sidebar-main">
						<div class="left-arrow" id="left-arrow">
							<i data-feather="arrow-left"></i>
						</div>
						<div id="sidebar-menu">
							<ul class="sidebar-links" id="simple-bar">
								<li class="back-btn">
									<div class="mobile-back text-end">
										<span>Back</span> <i class="fa fa-angle-right ps-2"
											aria-hidden="true"></i>
									</div>
								</li>
								<li class="menu-box">
									<ul>

										<li class="sidebar-list"><a href="dashboard.jsp"
											class="sidebar-link sidebar-title link-nav active"> <i
												data-feather="home"></i> <span>Dashboard</span>
										</a></li>
										<li class="sidebar-list">
										<li class="sidebar-list"><a class="sidebar-link "
											href="templates.jsp"> <i data-feather="download"></i> <span>Manage
													Campaign</span>
										</a></li>
										<li class="sidebar-list"><a class="sidebar-link "
											href="inputsearchfile.jsp"> <i data-feather="download"></i>
												<span>Manage File</span>
										</a></li>
										<li class="sidebar-list"><a class="sidebar-link "
											href="managedata.jsp"> <i data-feather="download"></i> <span>Manage
													Data</span>
										</a></li>
										<li class="sidebar-list"><a class="sidebar-link "
											href="DownloadData.jsp"> <i data-feather="download"></i>
												<span>Download Data</span>
										</a></li>				
										<%
										String user_type = "0";

										if (session.getAttribute("userType") != null) {
											user_type = session.getAttribute("userType").toString();

										}

										if (user_type.equalsIgnoreCase("1")) {
										%>
										<li class="sidebar-list"><a class="sidebar-link "
											href="#"> <i data-feather="users"></i> <span>Manage
													Users</span>
										</a></li>

										<li class="sidebar-list"><a class="sidebar-link "
											href="#"> <i data-feather="dollar-sign"></i> <span>Recharge
											</span>
										</a></li>
										<li class="sidebar-list"><a class="sidebar-link "
											href="#"> <i data-feather="activity"></i> <span>Recharge
													History </span>
										</a></li>
										<%
										}
										%>
									</ul>
								</li>
							</ul>

						</div>
						<div class="right-arrow" id="right-arrow">
							<i data-feather="arrow-right"></i>
						</div>
					</nav>
				</div>
			</div>
			<!-- Page Sidebar Ends-->