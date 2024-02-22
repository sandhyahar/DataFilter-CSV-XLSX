<%@ include file="session.jsp"%>
<%@ include file="header.jsp"%>
<script src="${pageContext.request.contextPath}/path.js"></script>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<!-- Page Body Start-->
<style>
/* Set the table layout to auto */
table {
	table-layout: auto;
}
</style>
<div class="page-body-wrapper">
	<div class="page-body">
		<div class="container">
			<div class="page-title">
				<h4>Create FileData</h4>
				<div id="errorMsg"></div>
				<input type="hidden" value=<%=session.getAttribute("userId")%>
					id="userId" name="userId"> <input type="hidden"
					name="filepath" id="filepath">
				<div class="row">
					<div class="row">
						<div class="col-sm-2 mb-3">
							<input type="file" id="file" name="file" class="form-control"
								value="">
						</div>
						<div class="col-sm-4 mb-3">
							<button type="button" class="btn btn-primary small-text"
								id="uploadbtn" name="uploadbtn">Upload</button>
						</div>
						<div id="spinner" style="display: none;">
							<div class="spinner-border text-primary" role="status">
								<span class="visually-hidden">Loading...</span>
							</div>
							<div class="text-primary">Processing...</div>
						</div>
					</div>
					<form class="theme-form" id="myForm">
						<input type="hidden" value="<%=session.getAttribute("userId")%>"
							id="userId" name="userId"> <input type="hidden"
							name="dataMstId" id="dataMstId"
							value="<%=request.getParameter("id")%>"> <input
							type="hidden" name="filepath" id="filepath"
							value="<%=request.getParameter("filepath")%>"> <input
							type="hidden" name="hiddenHeaderList" id="hiddenHeaderList"
							value="">


						<div>
							<label class="fw-bold">Data Name</label>
							<div>
								<input type="text" id="dataNameValue" readonly>
							</div>
						</div>
						<div>
							<span id="errorMsg"></span>
						</div>
						<div class="form-group">
							<label class="col-form-label fw-bold">Headers</label>
							<div id="headerList" name="headerList"></div>
						</div>
						<!-- Add a div with an ID to display the header information -->
						<div id="headerInfo"></div>

						<div>
							<div id="sampleRecords" class="table-responsive"></div>
						</div>
						<input type="button" class="btn btn-primary small-text"
							value="Upload Process" id="UploadProcess">
					</form>
				</div>
			</div>
		</div>
	</div>
</div>
<%@ include file="footer.jsp"%>
<script>
	var dropdownHtml = "";
	var headerMaping = {}; // Store header mapping data as an object
	$("#myForm").hide();
	$(document)
			.ready(
					function() {
						// Function to handle the AJAX upload
						function uploadFile(file) {
							// Show the spinner
							showSpinner();

							var formData = new FormData();
							formData.append("file", file);

							// Make the AJAX request
							$.ajax({
								url : mainUrl + "upload",
								type : "POST",
								data : formData,
								contentType : false,
								processData : false,
								success : function(response) {
									// Hide the spinner
									hideSpinner();
									// Handle the successful response
									console.log("Upload successful. Response: "
											+ JSON.stringify(response));
									displayData(response); // Call the function to display the data

								},
								error : function(xhr, status, error) {
									// Hide the spinner
									hideSpinner();

									// Handle the error response
									alert("Upload failed. Error: " + error);
								}
							});
						}

						// Event listener for the Upload button
						$("#uploadbtn").click(function() {
							var fileInput = document.getElementById("file");
							var file = fileInput.files[0];
							uploadFile(file); // Call the uploadFile function with the file object

							// Hide the file input and upload button
							$("#file").hide();
							$("#uploadbtn").hide();
							$("#myForm").show();

							// Show the spinner
							showSpinner();
						});

						// Event listener for the dropdown with ID 'selectedHeader'
								$(document).on("change", "#selectedHeader", function() {
							  var selectedHeaderValue = $(this).val();
							  var headerName = $(this).closest(".row").find("label").text().trim();
							  
							  // Split headerName using space and get the first part (before the first space)
							  var headerNameFirstPart = headerName.split(' ')[0];
							
							  headerMaping[selectedHeaderValue] = headerNameFirstPart;
							  console.log("headerMaping:", headerMaping);
							});

								$("#UploadProcess")
										.click(
												function() {
													var dataName = $("#dataNameValue")
															.val();
													var userId = $("#userId").val();
													var filepath = $("#filepath").val();
													

													  var headerArray = Object.entries(headerMaping).map(([selectedHeaderValue, headerName]) => {
													    return { [selectedHeaderValue]: headerName };
													  });
													
											 var selectedHeader = $("#selectedHeader").val();

											    if (!selectedHeader) {
											        alert('Please select a header from the dropdown.');
											        return; 
											    }
											    
											    var hiddenHeaderListValue = $("#hiddenHeaderList").val();
											    var headerList = JSON.parse(hiddenHeaderListValue);
											    var uniqueHeaderNames = headerList
											    .filter(header => header.uniqueKey) // Filter only headers with uniqueKey set to true
											    .map(header => header.name); // Extract the 'name' property from each matching header
											    
											    console.log(headerList);
											    console.log(uniqueHeaderNames);
											    
											    var uniqueHeaderNamesString = uniqueHeaderNames.join(','); // Join the array elements into a string with commas as separators



											var requestData = {
												dataName : dataName,
												dataMstId : $("#dataMstId")
														.val(),
															userId : userId,
												headerList: headerList,
												uniqueHeaderName: uniqueHeaderNamesString, 
										        headerMaping: headerArray, 
												filePath : filepath
											};
											
											if (Object.keys(headerMaping).length > 0) {
												$
														.ajax({
															url : mainUrl
																	+ 'inserTempDataMaster',
															type : 'POST',
															data : JSON
																	.stringify(requestData),
															contentType : 'application/json',
															success : function(
																	response) {
																alert("Data submited successfully...");
																hideSpinner();
																window.location.href = "managedata.jsp";
															},
															
														});
												
											} else {
												$("#errorMsg")
														.text('Please select at least one header.');
											}
										});

						function displayData(response) {
							var obj = JSON.parse(response);
							var headers = obj.headers;
							dropdownHtml = '<select id="selectedHeader" name="selectedHeader" class="btn btn-default btn-sm dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">';
							dropdownHtml += '<option value="">Select a header</option>';

							for (var i = 0; i < headers.length; i++) {
								dropdownHtml += '<option value="' + headers[i] + '">'
										+ headers[i] + '</option>';
							}

							dropdownHtml += '</select>';
							$("#headerInfo").html(dropdownHtml);

							var filepath = obj.filepath;
							document.getElementById("filepath").value = filepath;

							var sampleRecords = obj.sampleRecords;
							var tableHtml = '<table class="table table-striped table-hover">';
							tableHtml += '<tr>';

							for (var j = 0; j < headers.length; j++) {
								tableHtml += '<th>' + headers[j] + '</th>';
							}

							tableHtml += '</tr>';

							for (var k = 0; k < sampleRecords.length; k++) {
								tableHtml += '<tr>';

								for (var l = 0; l < headers.length; l++) {
									tableHtml += '<td>'
											+ sampleRecords[k][headers[l]]
											+ '</td>';
								}

								tableHtml += '</tr>';
							}

							tableHtml += '</table>';
							$("#sampleRecords").html(tableHtml);
							getHeaderListData();
						}

						function getHeaderListData() {
							var id = $("#dataMstId").val();
							$
									.ajax({
										url : mainUrl
												+ 'getDataMasterHeaderList?id='
												+ id,
										type : 'GET',
										contentType : 'application/json',
										success : function(response) {
											var obj = JSON.parse(response);
									        var headerList = obj.dataList[0].headerList; 
									        var headerListJson = JSON.stringify(headerList);
									        var headers = obj.dataList;
											var firstData = headers[0];
											var dataName = firstData.dataName;
											
											$("#hiddenHeaderList").val(headerListJson);
											console.log(headerListJson); // Log the JSON string to the console

											
											$("#dataNameValue").val(dataName);
											
		
											if (headers.length > 0) {
												var headerList = headers[0].headerList;

												var headerInfoHtml = '';

												for (var i = 0; i < headerList.length; i++) {
													var header = headerList[i];

													headerInfoHtml += '<div class="row">';
													headerInfoHtml += '<div class="col-sm-2">';
													headerInfoHtml += '<label name="name">';
													headerInfoHtml += header.name
															+ '</label>';
													headerInfoHtml += '</div>';

													headerInfoHtml += '<div class="col-sm-2">';
													headerInfoHtml += '<label><input type="radio" name="uniqueKey" value="'
															+ header.name + '"';
													if (header.uniqueKey) {
														headerInfoHtml += ' checked';
													}
													headerInfoHtml += ' disabled>';
													headerInfoHtml += ' Unique Key</label>';
													headerInfoHtml += '</div>';

													headerInfoHtml += '<div class="col-sm-2">';
													headerInfoHtml += '<label><input type="checkbox" name="notNullList" value="'
															+ header.name + '"';
													if (header.notNull) {
														headerInfoHtml += ' checked';
													}
													headerInfoHtml += ' disabled>';
													headerInfoHtml += ' NotNull/Blank</label>';
													headerInfoHtml += '</div>';

													headerInfoHtml += '<div class="col-sm-2">';
													headerInfoHtml += '<select name="validationType">';
													headerInfoHtml += '<option value="text"'
															+ (header.validationType === 'text' ? ' selected'
																	: '')
															+ '>Text</option>';
													headerInfoHtml += '<option value="mobile"'
															+ (header.validationType === 'mobile' ? ' selected'
																	: '')
															+ '>Mobile</option>';
													headerInfoHtml += '<option value="email"'
															+ (header.validationType === 'email' ? ' selected'
																	: '')
															+ '>Email</option>';
											   		headerInfoHtml += '<option value="email"'
															+ (header.validationType === 'pincode' ? ' selected'
																	: '')
															+ '>Pincode</option>';
													
													headerInfoHtml += '</select>';
													headerInfoHtml += '</div>';

													headerInfoHtml += '<div class="col-sm-2">';
													headerInfoHtml += dropdownHtml;
													headerInfoHtml += '</div>';

													headerInfoHtml += '</div>';
												}
												// Set the generated HTML to the #headerInfo element
												$("#headerInfo").html(headerInfoHtml);
								                //window.location.href = "managedata.jsp";

											}
										}
									});
						}

						function showSpinner() {
							$("#spinner").show();
							$("#myForm").hide();
						}

						function hideSpinner() {
							$("#spinner").hide();
							$("#myForm").show();
						}
					});
</script>
