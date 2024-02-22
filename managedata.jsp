<jsp:include page="session.jsp" />
<jsp:include page="header.jsp" />
<link rel="stylesheet"
	href="https://cdn.datatables.net/1.10.22/css/jquery.dataTables.min.css" />
<script src="${pageContext.request.contextPath}/path.js"></script>
<script>
	function searchData() {
		var userId = document.getElementById("userId").value;
		var searchInput = document.getElementById("searchInput").value;
		var id = document.getElementById("id").value;

		var requestData = {
			userId : userId
		};

		if (searchInput) {
			requestData.insert_time = searchInput;
		}

		$
				.ajax({
					type : 'GET',
					url : mainUrl + 'findfiledata',
					data : requestData,
					success : function(response) {
						var tableBody = document.getElementById("myTable");
						tableBody.innerHTML = ""; // Clear the existing table body
						var data = JSON.parse(response).list;
						console.log(data);
						for (var i = 0; i < data.length; i++) {
							var row = tableBody.insertRow();
							var dataNameCell = row.insertCell(0);
							var totalFileCell = row.insertCell(1);
							var headerListCell = row.insertCell(2);
							var insertTimeCell = row.insertCell(3);
							var statusCell = row.insertCell(4);
							var searchFileCell = row.insertCell(5);
							var addFileCell = row.insertCell(6);

							var headerListNames = data[i].headerList.map(
									function(header) {
										return header.name;
									}).join(',');

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
							
							if (data[i].total == 0) {
								totalRecords = "-";
							} else {
								totalRecords = data[i].total;
							}


							// Assuming 'data' is an array of objects and 'i' is the index of the desired object
							var headerListDataJSON = JSON
									.stringify(data[i].headerList);

							dataNameCell.innerHTML = data[i].dataName;
							totalFileCell.innerHTML = totalRecords;
							headerListCell.innerHTML = formatFieldsText(headerListNames);
							insertTimeCell.innerHTML = data[i].insert_time;
							statusCell.innerHTML = statusText; // Display the status
							statusCell.classList.add(rowClass); // Add row class if needed              
							searchFileCell.innerHTML = '<form action="searchfiledata.jsp" method="post">'
									+ '<input type="hidden" name="headerList" value="' + headerListNames + '">'
									+ '<input type="hidden" name="id" value="' + data[i].id + '">'
									+ '<button type="submit" class="btn small-text btn-info">Search</button>'
									+ '</form>';
							addFileCell.innerHTML = '<form action="headermapingdatafile.jsp" method="post">'
									+ '<input type="hidden" name="id" value="' + data[i].id + '">'
									+ '<button type="submit" class="btn small-text btn-primary">AddFile</button>'
									+ '</form>';
						}
						$('#mytbl').DataTable();

					},
					error : function(xhr, status, error) {
						console.log(error);
					}
				});
	}

	function formatFieldsText(fields) {
		// Split the fields string into an array of values using comma as the separator
		const fieldValues = fields.split(',');
		const lineBreakAfter = 4;

		// Create an array to store chunks of values based on the lineBreakAfter value
		const fieldChunks = [];

		// Loop through the fieldValues array and group them into chunks based on the lineBreakAfter value
		for (let i = 0; i < fieldValues.length; i += lineBreakAfter) {
			const chunk = fieldValues.slice(i, i + lineBreakAfter);
			fieldChunks.push(chunk.join(','));
		}

		// Join the fieldChunks array with line breaks
		return fieldChunks.join('<br>');
	}
</script>
<div class="page-body-wrapper">
	<div class="page-body">
		<div class="container-fluid">
			<div class="page-title">
				<div class="row">
					<div class="col-sm-6">
						<h3 class="fw-bold">ManageData</h3>
					</div>
				</div>
			</div>
		</div>

		<div class="row">
			<div class="col-sm-2 mb-3">
				<input type="date" id="searchInput" name="searchInput"
					class="form-control" placeholder="Search" value="">
			</div>
			<div class="col-sm-4 mb-3">
				<button type="button" class="btn btn-primary  small-text"
					id="searchbtn" name="searchbtn" onclick="searchData()">Search</button>
				<a href="createfile.jsp" alert="button login">
					<button class="btn btn-info small-text ml-3" type="button">Add
						Data</button>
				</a>

			</div>
			<div class="col-sm-2 mb-3" style="padding-right: 100px"></div>
		</div>
		<!-- Container-fluid starts-->
		<div class="container-fluid basic_table">
			<div class="row">
				<div class="col-sm-12">
					<div class="card">
						<div>
							<input type="hidden" value=<%=session.getAttribute("userId")%>
								id="userId" name="userId"> <input type="hidden" value=""
								name="id" id="id"> <input type="hidden" name="id"
								id="id">

							<table id="mytbl" class="display">
								<thead>
									<tr>
										<th scope="col">DataName</th>
										<th scope="col">Total</th>
										<th scope="col">Fields</th>
										<th scope="col">InsertTime</th>
										<th scope="col">Status</th>
										<th scope="col">Search</th>
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

<jsp:include page="footer.jsp" />
<!-- <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
 -->
<!-- Load DataTables -->
<!-- <script
	src="https://cdn.datatables.net/1.13.5/js/jquery.dataTables.min.js"></script> -->
<script type="text/javascript"
	src="https://cdn.datatables.net/1.10.22/js/jquery.dataTables.min.js">
	
</script>
<script>
	$(document).ready(function() {
		searchData();
	});
</script>
