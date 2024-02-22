<%@ include file="session.jsp"%>
<%@ include file="header.jsp"%>
<script src="${pageContext.request.contextPath}/path.js"></script>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
	$(document)
			.ready(
					function() {
						// Set the width of the textbox and dropdown to 20%
						$('.form-control').css('width', '15%');
						$('.form-select').css('width', '15%');

						// Click event for the search button
						$('#searchbtn')
								.click(
										function(event) {
									        event.preventDefault(); // Prevent page refresh
											$("#spinner").show();

											var userId = $('#userId').val();
											var dataMstId = $('#dataMstId')
													.val();
											var filterList = [];

											// Iterate through each header value checkbox
											$('input[name="checkbox"]')
													.each(
															function() {
																if ($(this)
																		.prop(
																				'checked')) {
																	var headerValue = $(
																			this)
																			.attr(
																					'id');
																	var selectValue = $(
																			this)
																			.parent()
																			.siblings(
																					'.form-select')
																			.val();
																	var inputValue = $(
																			this)
																			.parent()
																			.siblings(
																					'.form-control')
																			.val();
																	var searchObj = generateJsonSearchObject(
																			headerValue,
																			selectValue,
																			inputValue)
																	if (searchObj != null) {
																		filterList
																				.push(JSON
																						.stringify(searchObj))
																	}
																}
															});

											var requestData = {
												"userId" : userId,
												"dataMstId" : dataMstId,
												"filterList" : filterList
											};
											//console.log(requestData);
											// Send the AJAX request
											$
													.ajax({
														url : mainUrl
																+ 'filterdata',
														type : 'POST',
														contentType : 'application/json',
														data : JSON.stringify(requestData),
														success : function(
																response) {
															$("#spinner").hide();
															var data = JSON.parse(response);
															var totalCnt = data.totalCnt;
															var detailDataList = data.detailDataList;
														 // console.log(detailDataList);

															// Display total count
															/* $('#totalCount')
																	.text(
																			totalCnt);
 */
															var headerList = $(
																	'#headerList')
																	.val();
															var headerListArr = headerList
																	.split(",");
															
														    // Clear previous table rows
											                $('#myTable tbody').empty();


															// Display header list as table headers
															var tableHeaders = '<tr>';
															console.log(headerListArr);
															for (var i = 0; i < headerListArr.length; i++) {
																tableHeaders += '<th>'
																		+ headerListArr[i]
																		+ '</th>';
															}
															tableHeaders += '</tr>';
															$('#myTable thead')
																	.html(
																			tableHeaders);
															console.log(detailDataList)
															// Display detail data as table rows with corresponding columns
															for (var i = 0; i < detailDataList.length; i++) {
																var detailData = detailDataList[i];
																var rowData = '<tr>';
																for (var j = 0; j < headerListArr.length; j++) {
																	rowData += '<td>'
																			+ detailData[headerListArr[j]]
																			+ '</td>';
																}
																rowData += '</tr>';
																	$(
																		'#myTable tbody')
																		.append(
																				rowData);
															}
															$('#resultSection')
																	.show();
														},
														error : function(error) {
															console.log(error);
															$("#spinner").hide();

															// Handle the error here
														}
													});
										});

					});

	function generateJsonSearchObject(field, condition, value) {
		var requestData = {
			"field" : field,
			"condition" : condition,
			"value" : value
		};
		return requestData;
	}
	
	function downloadBtn() {
	    var userId = $('#userId').val();
	    var dataMstId = $('#dataMstId').val();
		var filterList = [];

		// Iterate through each header value checkbox
	    $('input[name="checkbox"]').each(function() {
	        if ($(this).prop('checked')) {
	            var headerValue = $(this).attr('value');
	            var selectValue = $(this).parent().siblings('.form-select').val();
	            var inputValue = $(this).parent().siblings('.form-control').val();
	            var searchObj = generateJsonSearchObject(headerValue, selectValue, inputValue);
	            if (searchObj != null) {
	                filterList.push(JSON.stringify(searchObj));
	            }
	        }
	    });

	    var requestData = {
				"userId" : userId,
				"dataMstId" : dataMstId,
				"filterList" : filterList
			};
	
	    
	    // Send the AJAX request to download the data
	    $.ajax({
	        url: mainUrl + 'downloadInsertData',
	        type: 'POST',
	        contentType: 'application/json',
	        data: JSON.stringify(requestData),
	        success: function(response) {
	            alert("Download Request Send Successfully...");
	            window.location.href = "DownloadData.jsp";
	            // Perform any necessary actions after successful download
	        },
	        error: function(error) {
	            // Handle the error here
	            console.log(error);
	        }
	    });
	}
	
	


</script>

<!-- Page Body Start-->
<style>
.small-text {
	font-size: 15px;
}

.form-group {
	margin-bottom: 10px;
}

.row {
	display: flex;
	flex-wrap: wrap;
	justify-content: center;
}

.form-inline {
	margin: 5px;
}
	  /* Set the table layout to auto */
  table {
    table-layout: auto;
  }

</style>

<div class="page-body-wrapper">
	<div class="page-body">
		<div class="container">
			<div class="page-title">
				<h4>FilterData</h4>
				<input type="hidden" value="<%=session.getAttribute("userId")%>"
					id="userId" name="userId"> <input type="hidden"
					name="dataMstId" id="dataMstId"
					value="<%=request.getParameter("id")%>"> <input
					type="hidden" name="filepath" id="filepath"
					value="<%=request.getParameter("filepath")%>"> <input
					type="hidden" name="headerList" id="headerList"
					value="<%=request.getParameter("headerList")%>">
					
				<div class="row">
					<div class="col-md-12">
						<div class="card">
							<div class="card-body">
								<label style="font-weight: bold;">Field:</label>
								<%
								String[] headerList = request.getParameterValues("headerList");
								String[] id = request.getParameterValues("id");
								if (headerList != null) {
									for (int i = 0; i < headerList.length; i++) {
										String header = headerList[i];
										String[] headerValues = header.split(",");
								%>
								<div class="row">
									<div class="col-md-12">
										<%
										for (int j = 0; j < headerValues.length; j++) {
											String value = headerValues[j];
										%>
										<div class="form-inline">
											<div class="form-check form-check-inline">
												<input class="form-check-input" type="checkbox"
													name="checkbox" value="<%=headerValues[j]%>" id="<%=value%>">
												<label class="form-check-label" for="<%=value%>"><%=value%></label>
											</div>
											<select class="form-select mx-3" style="width: 20%;">
												<option value="equalvalue">EqualValue</option>
												<option value="contains">Contains</option>
											</select> <input type="text" class="form-control mx-3"
												style="width: 20%;" placeholder="Enter Value"
												id="<%=value%>-input">
										</div>
										<%
										}
										%>
									</div>
								</div>
								<%
								if (i < headerList.length - 1) {
								%>
								<hr>
								<%
								}
								%>
								<%
								}
								}
								%>
							</div>
						</div>
						<div class="col-sm-4 mb-3">
							<button type="button" class="btn btn-primary small-text"
								id="searchbtn">Search</button>
						</div>
						<div id="spinner" style="display: none;">
								<div class="spinner-border text-primary" role="status">
									<span class="visually-hidden">Loading...</span>
								</div>
								<div class="text-primary">Processing...</div>
						</div>
						<div id="resultSection" style="display: none;">
							<div class="row">
								<div class="col">
									<!-- <b style="font-size: 20px;">Total Count: <span
										id="totalCount" class="ml-2" style="font-size: 20px;"></span></b> -->
									<div class="text-end mb-10">
										<button type="button" class="btn btn-info small-text"
											id="downloadBtn" onclick="downloadBtn();">Download</button>
									</div>
								</div>
							</div>
						</div>

						<table id="myTable" class="table table-border table-responsive">
							<thead>
								<tr>
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>

					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<%@ include file="footer.jsp"%>
