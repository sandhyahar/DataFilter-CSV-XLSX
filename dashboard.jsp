<%@ include file="session.jsp" %>

<%@page import="java.util.Date"%>
<%@page import="java.sql.*"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*"%>



<%
String status = "";
String status1 = "";

if (session != null && session.getAttribute("dashboardStatus") != null) {
	status = session.getAttribute("dashboardStatus").toString();
}
if (status.equalsIgnoreCase("0")) {
	status1 = "False";
} else {
	status1 = "True";
}
%>
<jsp:include page="header.jsp" />
<div class="page-body">
	<div class="container-fluid">
		<div class="page-title">
			<div class="row">
				<div class="col-sm-6">
					<h3>Dashboard</h3>
				</div>
				<div class="col-sm-6"></div>
			</div>
		</div>
	</div>
	<!-- Container-fluid starts-->
	<div class="container-fluid dashboard-default">
		<div class="row">
			<div class="col-xxl-4 col-xl-4 col-lg-6 box-col-40 xl-33">
				<div class="card profile-greeting" style="height: 260px;">
					<div class="card-body">
						<div class="greeting-user">
							<div class="profile-vector">
								<img class="img-fluid"
									src="assets/images/dashboard/default/profile.png" alt=""
									width="60px">
							</div>
							<h4>
								<span>Welcome Back</span>
								<%
								if (session != null && session.getAttribute("userId") != null) {
								%>
								<%=session.getAttribute("userId")%>
								<%
								}
								%>
							</h4>
							<h5>
								<!-- <span>Balance :-</span> ${balanceMv.balance} -->

							</h5>


						</div>
					</div>
				</div>
			</div>
		<!-- 	<div class="col-xxl-4 col-xl-4 col-md-6 box-col-40 xl-33">
				<div class="card our-user" style="height: 260px;">
					<div class="card-header pb-0">
						<div class="d-flex justify-content-between">
							<div class="flex-grow-1">
								<p class="square-after f-w-600 header-text-primary">
									Monthly Summary <i class="fa fa-circle"></i>
								</p>
								<!--<h4>96.564%</h4>-->
		<!--					</div>
						</div>
					</div>
					<div class="card-body">
						<div class="user-chart">

							<div class="icon-donut">
								<i data-feather="arrow-up-circle"></i>
							</div>
						</div>
						<ul>


							<c:forEach var="data" items="${dashboardPercentage}">

								<li>
									<p class="f-w-600 font-primary f-12">${data.key}</p> <span
									class="f-w-600">${data.value} %</span>

								</li>

							</c:forEach>


						</ul>
					</div>
				</div>
			</div>
			<div class="col-xxl-4 col-xl-4 col-md-6 box-col-40 xl-33">
				<div class="card" style="height: 260px;">
					<div class="card-header pb-0">
						<div class="d-flex justify-content-between">
							<div class="flex-grow-1">
								<p class="square-after f-w-600 header-text-primary">
									Last Login <i class="fa fa-circle"></i>
								</p>
							</div>
						</div>
					</div>
					<div class="card-body">
						<br>
						<div class="table-responsive">
							<table class="table">

								<tbody style="border-bottom: none;">
									<c:forEach var="item" items="${sessionScope.dashboardValues}">
										<tr>


											<td>${item.loginTime}</td>


											<td><c:choose>
													<c:when test="${item.status == 0}">
														<label 
															style="color: black;">False</label>
													</c:when>
													<c:when test="${item.status == 1}">
														<label 
															style="color: black;">True</label>
													</c:when>

												</c:choose></td>

											<td>${item.ip}</td>




										</tr>
									</c:forEach>

									
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>


			<div class="container-fluid dashboard-default">
				<div class="row">

					<div class="col-xxl-4 col-xl-4 col-md-6 box-col-40 xl-33">
						<div class="card">
							<div class="card-header pb-0">
								<div class="d-flex justify-content-between">
									<div class="flex-grow-1">
										<p class="square-after f-w-600 header-text-primary">
											Pricing <i class="fa fa-circle"></i>
										</p>
									</div>
								</div>
							</div>
							<div class="card-body">
								<br>
								<div class="table-responsive">
									<table class="table">

										<tbody>
											<tr>

												<td>Whatsaap Authentication</td>
												<td>
													<button class="btn2 btn-outline-primary" type="button"
														style="color: white; height: 38.68px; width: 91.58px;">
														<%
														if (session != null && session.getAttribute("whatsapprate") != null) {
														%>
														<%=session.getAttribute("whatsapprate")%>
														paisa<%
														}
														%>
													</button>
												</td>

											</tr>


											<tr>
												<td>Voice Authentication</td>
												<td>
													<button class="btn2 btn-outline-primary" type="button"
														style="color: white; height: 38.68px; width: 91.58px;">

														<%
														if (session != null && session.getAttribute("voiceRate") != null) {
														%>
														<%=session.getAttribute("voiceRate")%>

														paisa<%
														}
														%>
													</button>
												</td>
											</tr>
											<tr>
												<td>SMS OTP Authentication</td>
												<td>

													<button class="btn2 btn-outline-primary" type="button"
														style="color: white; height: 38.68px; width: 91.58px;">
														<%
														if (session != null && session.getAttribute("smsRate") != null) {
														%>
														<%=session.getAttribute("smsRate")%>
														paisa
														<%
														}
														%>
													</button>
												</td>
											</tr>

										</tbody>
									</table>
								</div>
							</div>

						</div>
					</div>
					<div class="col-xxl-8 col-xl-8 col-md-6 box-col-40 xl-33">
						<div class="card best-seller" style="height: 339px;">
							<div class="card-header pb-0">
								<div class="d-flex justify-content-between">
									<div class="flex-grow-1">
										<p class="square-after f-w-600">
											Helping Videos <i class="fa fa-circle"></i>
										</p>
									</div>
								</div>
							</div>
							<div class="card-body pt-0" style="margin-top: 30px;">
								<div class="table-responsive theme-scrollbar">
									<br>
									<table class="table">

										<tbody>
											<tr>
												<td>1</td>
												<td>How OTPfree works?</td>
												<td>
													<button class="btn2 btn-outline-warning" type="button"
														style="color: white; height: 38.68px; width: 91.58px; border-radius: 0; background: #eeb82f;"
														data-bs-toggle="modal" data-original-title="test"
														data-bs-target="#exampleModal">Video</button>

												</td>
											</tr>
											<tr>
												<td>2</td>
												<td>How to intigrate OTPfree in your application?</td>
												<td>
													<button class="btn2 btn-outline-warning" type="button"
														style="color: white; height: 38.68px; width: 91.58px; border-radius: 0; background: #eeb82f;"
														data-bs-toggle="modal" data-original-title="test"
														data-bs-target="#exampleModal">Video</button>

												</td>
											</tr>
											<tr>
												<td>3</td>
												<td>Our other products, which might be very helpful for
													your brand.</td>
												<td>
													<button class="btn2 btn-outline-warning" type="button"
														style="color: white; height: 38.68px; width: 91.58px; border-radius: 0; background: #eeb82f;"
														data-bs-toggle="modal" data-original-title="test"
														data-bs-target="#exampleModal">Video</button>

												</td>
											</tr>


										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>

		</div>
		<!-- Container-fluid Ends-->
	</div> 
	<!-- footer start-->
	<jsp:include page="footer.jsp" />
