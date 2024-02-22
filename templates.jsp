<%@ include file="session.jsp"%>
<%@ include file="header.jsp"%>
<script src="${pageContext.request.contextPath}/path.js"></script>
<link rel="stylesheet"
	href="https://cdn.datatables.net/1.10.22/css/jquery.dataTables.min.css" />
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">

<div class="page-body-wrapper">
	<div class="page-body">
		<div class="container-fluid">
			<div class="page-title">
				<div class="row">
					<div class="col-sm-6">
						<h3 class="fw-bold">Campaigns</h3>
					</div>
				</div>
			</div>
		</div>

		<div class="row">
			<div class="col-sm-2 mb-3">
				<input type="hidden" id="startDateTime" name="startDateTime"
					class="form-control" placeholder="Search" value="">
			</div>
			<div class="col-sm-4 mb-3" style="display: none">
				<button type="button" class="btn btn-primary  small-text"
					id="searchbtn" name="searchbtn">Search</button>


			</div>

			<div class="d-flex justify-content-between">
				<a href="apis.jsp">
					<button class="btn btn-info small-text" type="button">Add
						Campaign</button>
				</a> <a href="">
					<button class="btn btn-primary small-text" type="button">
						<i class="fas fa-sync-alt"></i> Refresh
					</button>
				</a>

			</div>

			<div class="col-sm-2 mb-3" style="padding-right: 100px"></div>


			<!-- Container-fluid starts-->
			<div class="container-fluid basic_table">
				<div class="row">
					<div class="col-sm-12">
						<div class="card">
							<div>
								<form id="addDataForm" action="AddCampaignFile.jsp"
									method="post">
									<input type="hidden"
										value="<%=session.getAttribute("userId")%>" id="userId"
										name="userId"> <input type="hidden" value="" name="id"
										id="addDataId"> <input type="hidden" value=""
										name="filePath" id="filePath">
								</form>

								<table class="display" id="mytbl">
									<thead>
										<tr>
											<th scope="col">Campaign Name</th>
											<th scope="col">Total Count</th>
											<th scope="col">Filter Campaign</th>
											<th scope="col">Status</th>
											<th scope="col">Insert Time</th>
											<th scope="col">End Time</th>
											<th scope="col">Add Data</th>
											<th scope="col">DownladFile</th>
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
			fetchCampaigns();
		});

		// Attach a click event listener to the search button
		/* document.getElementById('searchbtn').addEventListener('click', function() {
			var userId = $('#userId').val();
			var startDateTime = $('#startDateTime').val();
			fetchCampaigns(startDateTime);
		
		}); */

		function fetchCampaigns() {
			$
					.ajax({
						method : "GET",
						url : mainUrl + "findCampaignList?userId="
								+ $('#userId').val(),
						success : function(response) {
							var data = JSON.parse(response);
							var campaignList = data.list;
							buildTable(campaignList);

							$('#mytbl').DataTable().draw();

						},
						error : function(xhr, status, error) {
							console.error(error);
						}
					});
		}

		function buildTable(data) {
			var table = document.getElementById('myTable');
			var respons = getcampaignNameList();
			var campaignNamelist = JSON.parse(respons).list;
			table.innerHTML = ''; // Clear existing table data

			for (var i = 0; i < data.length; i++) {
				var statusText;
				var filePathContent;
				var totalRecords;
				var addData;
				var deleteButton = "";
				var rowClass = ""; // CSS class for the table row

				deleteButton = "<button class='btn btn-danger btn-sm small-text' onclick='deleteRecord(\""
						+ data[i].id + "\")'>Delete</button>";

				if (data[i].status == 1) {
					statusText = "<span class='badge badge-primary h5'>Processing</span>";
					rowClass = "text-primary";
				} else if (data[i].status == 2) {
					statusText = "<span class='badge badge-warning h5'>In Processing</span>";
					rowClass = "text-warning";
				} else if (data[i].status == 3) {
					statusText = "<span class='badge badge-success h5'>Complete</span>";
					rowClass = "text-success";
				} else {
					statusText = "<span class='badge badge-info h5'>Unknown</span>";
					rowClass = "text-info";
				}

				if (data[i].downloadFlag == 1) {
					filePathContent = "<span class='badge badge-primary h5'>Processing</span>";
					rowClass = "text-primary";
				} else if (data[i].downloadFlag == 2) {
					filePathContent = "<span class='badge badge-warning h5'>In Processing</span>";
					rowClass = "text-warning";
				} else if (data[i].downloadFlag == 3) {
					filePathContent = "<a href='" + data[i].processedFilePath + "' class='btn btn-primary btn-sm small-text' download>Download</a>";
				} else {
					filePathContent = "<span class='badge badge-info h5'>Unknown</span>";
					rowClass = "text-info";
				}

				if (data[i].totalRecords == 0) {
					totalRecords = "-";
				} else {
					totalRecords = data[i].totalRecords;
				}

				addData = "<a href='#' class='btn btn-info btn-sm small-text' onclick='submitAddDataForm(\""
						+ data[i].id + "\")'>Add Data</a>";

				var campaignName;
				if (data[i].campaignList !== undefined
						&& data[i].campaignList.length > 0) {
					campaignName = getCampaignNameById(campaignNamelist,
							data[i].campaignList);
				} else {
					campaignName = "undefined";
				}

				if (data[i].isVisible === 1) {
					var row = "<tr id='row_" + data[i].id + "'>" + "<td>"
							+ data[i].campaignName + "</td>" + "<td>"
							+ totalRecords + "</td>" + "<td>" + campaignName
							+ "</td>" + "<td class='" + rowClass + "'>"
							+ statusText + "</td>" + "<td>"
							+ data[i].startDateTime + "</td>" + "<td>"
							+ data[i].endDateTime + "</td>" + "<td>" + addData
							+ "</td>" + "<td>" + filePathContent + "</td>"
							+ "<td>" + deleteButton + "</td>" + "</tr>";
					table.innerHTML += row;

				}
			}
		}

		function deleteRecord(id) {
				var userId = $('#userId').val();
				var url = mainUrl + "updateCampaignVisibility?userId=" + userId
						+ "&id=" + id;
				$.ajax({
					method : "POST",
					url : url,
					data : {
						isVisible : 0
					},
					success : function(response) {
						// You can remove the row from the table using the ID
						$('#row_' + id).remove();
						confirm("Are you sure you want to delete this record?");
					},
					error : function(xhr, status, error) {
						// Handle the error if needed
						console.error(error);
					}
				});
			
		}

		function submitAddDataForm(id) {
			document.getElementById("addDataId").value = id;
			document.getElementById("addDataForm").submit();
		}

		function getCampaignNameById(campaignNamelist, campaignIds) {
			var idlist = campaignIds.toString().split(",");
			var campaignarray = [];
			for (var i = 0; i < campaignNamelist.length; i++) {
				for (var j = 0; j < idlist.length; j++) {
					if (campaignNamelist[i].id === idlist[j]) {
						var camping = campaignNamelist[i].campaignName;
						campaignarray.push(camping);

					}

				}

			}
			return campaignarray
		}

		function getcampaignNameList() {
			var campaignNamelist;
			$.ajax({
				method : "GET",
				url : mainUrl + "findCampaignNameAndId?userId="
						+ $('#userId').val(),
				async : false, // Make the request synchronous
				success : function(response) {
					campaignNamelist = response;

				}
			});
			return campaignNamelist;
		}
	</script>