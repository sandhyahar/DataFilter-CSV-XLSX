<%@ include file="session.jsp"%>
<%@ include file="header.jsp"%>
<script src="${pageContext.request.contextPath}/path.js"></script>
<link rel="stylesheet"
	href="https://cdn.datatables.net/1.10.22/css/jquery.dataTables.min.css" />
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
<!-- Page Body Start-->
<div class="page-body-wrapper">
	<div class="page-body">
		<div class="container-fluid">
			<div class="page-title">
				<div class="row">
					<div class="col-sm-6">
						<h3 class="fw-bold">Download Data</h3>
					</div>
				</div>
			</div>
		</div>
		<!-- Container-fluid starts-->

		<div class="container-fluid basic_table">
			<div class="d-flex justify-content-start">
				<a href="">
					<button class="btn btn-info small-text" type="button">
						<i class="fas fa-sync-alt"></i> Refresh
					</button>
				</a>
			</div>

			<div class="col-sm-2 mb-3" style="padding-right: 100px"></div>

			<div class="row">
				<div class="col-sm-12">
					<div class="card">

						<div>
							<input type="hidden" value=<%=session.getAttribute("userId")%>
								id="userId" name="userId"> <input type="hidden" value=""
								name="id" id="id"> <input type="hidden" name="id"
								id="id">

							<table class="display" id="mytbl">
								<thead>
									<tr>
										<th scope="col">UserId</th>
										<th scope="col">Status</th>
										<th scope="col">Insert Time</th>
										<th scope="col">Action</th>
									</tr>
								</thead>
								<tbody id="myTable">

								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- Container-fluid Ends-->
	</div>
</div>
<%@ include file="footer.jsp"%>
<script type="text/javascript"
	src="https://cdn.datatables.net/1.10.22/js/jquery.dataTables.min.js">
	
</script>
<script>
	$(document).ready(function() {
		getcampaignNameList();
	});
	function getcampaignNameList() {
		$
				.ajax({
					method : "GET",
					url : mainUrl + "getAllDownloadInsertRecords?userId="
							+ $('#userId').val(),
					async : false, // Make the request synchronous
					success : function(response) {
						var records = JSON.parse(response).records;
						var tableBody = $("#myTable");
						for (var i = 0; i < records.length; i++) {
							var record = records[i];
							var userId = record.userId;
							var status = record.status;
							var startDateTime = record.startDateTime;
							var action = record.filePath;

							if (status == 1) {
								statusText = "<span class='badge badge-primary h5'>Processing</span>";
								rowClass = "text-primary";
							} else if (status == 2) {
								statusText = "<span class='badge badge-warning h5'>In Processing</span>";
								rowClass = "text-warning";
							} else if (status == 3) {
								statusText = "<span class='badge badge-success h5'>Complete</span>";
								rowClass = "text-success";
							} else {
								statusText = "<span class='badge badge-info h5'>Unknown</span>";
								rowClass = "text-info";
							}

							if (status == 3) {
								filePathContent = "<a href='" + action + "' class='btn btn-primary btn-sm small-text' download>Download</a>";
							} else {
								filePathContent = "-";
							}

							var row = "<tr>" + "<td>" + userId + "</td>"
									+ "<td>" + statusText + "</td>" + "<td>"
									+ startDateTime + "</td>" + "<td>"
									+ filePathContent + "</td>" + "</tr>";

							tableBody.append(row);
						}
						$('#mytbl').DataTable();

					}
				});
	}
</script>
