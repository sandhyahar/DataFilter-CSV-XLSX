
<%@ include file="session.jsp"%>
<%@ include file="header.jsp"%>
<script src="${pageContext.request.contextPath}/path.js"></script>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<!-- Page Body Start-->
<style>
.small-text {
	font-size: 15px;
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
				<h4>Create FileData</h4>
				<input type="hidden" value=<%=session.getAttribute("userId")%>
					id="userId" name="userId"> <input type="hidden"
					name="filepath" id="filepath">
					<input type="hidden" id="totalRecords" name="totalRecords">
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
						<div>
							<label class="fw-bold">Data Name</label>
							<div class="input-group mb-3">
								<div class="input-group-prepend">
									<input type="text" name="dataName" id="dataName" required>
									<div id="errorMsg"></div>

									<div class="mt-3">
										<span class="fw-bold" style="color: red">Note: Only
											checked data will be inserted.</span>
									</div>
								</div>
								<div id="errorMsg"></div>
							</div>
						</div>
						<div>
							<span id="errorMsg"></span>
						</div>
						<div class="form-group">
							<label class="col-form-label fw-bold">Headers</label>
							<div id="headerList"></div>
						</div>
						<div>
							<div id="sampleRecords" class="table-responsive"></div>
						</div>
						<input type="button" class="btn btn-primary small-text"
							value="UploadProecess" id="UploadProcess"
							onclick="checkUniqueDataName();">
					</form>
				</div>
			</div>
		</div>
	</div>
</div>
<%@ include file="footer.jsp"%>
<script>
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

						function displayData(response) {
							var obj = JSON.parse(response);
							var totalRecords = obj.totalRecords;
							var headers = obj.headers;
							var checkboxesHtml = '';
							
							$('#totalRecords').val(totalRecords);

							

							for (var i = 0; i < headers.length; i++) {
								checkboxesHtml += '<div class="row">';
								checkboxesHtml += '<div class="col-sm-3">';
								checkboxesHtml += '<label><input type="checkbox" name="name" value="' + headers[i] + '"> '
										+ headers[i] + '</label>';
								checkboxesHtml += '</div>';
								checkboxesHtml += '<div class="col-sm-3">';
								checkboxesHtml += '<label><input type="radio" name="uniqueKey" value="' + headers[i] + '"> Unique Key</label>';
								checkboxesHtml += '</div>';
								checkboxesHtml += '<div class="col-sm-3">';
								checkboxesHtml += '<label><input type="checkbox" name="notNullList" value="' + headers[i] + '"> NotNull/Blank </label>';
								checkboxesHtml += '</div>';
								checkboxesHtml += '<div class="col-sm-3">';
								checkboxesHtml += '<select name="validationType">';
								checkboxesHtml += '<option value="text">Text</option>';
								checkboxesHtml += '<option value="mobile">Mobile</option>';
								checkboxesHtml += '<option value="email">Email</option>';
								checkboxesHtml += '<option value="pincode">Pincode</option>';
								checkboxesHtml += '</select>';
								checkboxesHtml += '</div>';
								checkboxesHtml += '</div>';
							}

							$("#headerList").html(checkboxesHtml);

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

						// Event listener for the UploadProcess button
						$("#UploadProcess").click(function() {
							UploadProcess();

						});

						function UploadProcess() {
							var dataName = $("#dataName").val();
							var userId = $("#userId").val();
							var filepath = $("#filepath").val();
							var totalRecords = $("#totalRecords").val();
							var selectedHeaders = [];

							// Validate the dataName field
							if (dataName.trim() === "") {
								alert('Data Name is required.');
								return; // Prevent further processing if the dataName is empty
							}

							// Disable the button
							$(this).prop("disabled", true);

							$("input[name='name']:checked")
									.each(
											function() {
												var name = $(this).val();
												var notNull = $(
														"input[name='notNullList'][value='"
																+ name + "']")
														.is(":checked");
												var validationType = $(this)
														.closest(".row")
														.find(
																"select[name='validationType']")
														.val();
												var uniqueKey = $(
														"input[name='uniqueKey'][value='"
																+ name + "']")
														.is(":checked");

												var header = {
													name : name,
													notNull : notNull,
													validationType : validationType,
													uniqueKey : uniqueKey,

												};
												selectedHeaders.push(header);
											});

							var selectedUniqueKeys = selectedHeaders.filter(
									function(header) {
										return header.uniqueKey === true;
									}).map(function(header) {
								return header.name;
							});

							var selectedNotNulls = selectedHeaders.filter(
									function(header) {
										return header.notNull === true;
									}).map(function(header) {
								return header.name;
							});

							var selectedvalidationType = selectedHeaders
									.map(function(header) {
										return header.validationType;
									});

							var requestData = {
								dataName : dataName,
								headerList : selectedHeaders.map(function(
										header) {
									return {
										name : header.name,
										notNull : header.notNull ? "true"
												: "false",
										validationType : header.validationType,
										uniqueKey : header.uniqueKey ? "true"
												: "false"

									};
								}),
								uniqueKey : selectedUniqueKeys,
								notNullList : selectedNotNulls,
								validationType : selectedvalidationType,
								userId : userId,
								filePath : filepath,// Set the filepath value correctly
								total : totalRecords,
							};

							console.log(requestData.headerList);

							if (selectedHeaders.length > 0) {
								// Call checkUniqueDataName function with a callback
								checkUniqueDataName(
										dataName,
										function(isUnique) {
											if (isUnique) {
												$
														.ajax({
															url : mainUrl
																	+ 'insertDataMaster',
															type : 'POST',
															data : JSON
																	.stringify(requestData), // Serialize the object
															contentType : 'application/json',
															success : function(
																	response) {
																alert("Upload.");
																hideSpinner();
																window.location.href = "managedata.jsp";
															},
															error : function(
																	xhr,
																	status,
																	error) {
																console
																		.error(
																				'Data insertion failed:',
																				error);
																$(
																		"#UploadProcess")
																		.prop(
																				"disabled",
																				false);

															}
														});
											}
										});
							}
						}
						function checkUniqueDataName(dataName, callback) {
							var requestData = {
								dataName : dataName
							};
							$.ajax({
								url : mainUrl + 'checkUniqueDataName',
								type : 'POST',
								data : JSON.stringify(requestData),
								contentType : 'application/json',
								success : function(response) {
									var obj = JSON.parse(response);
									if (obj.flag === true) {
										$("#errorMsg").removeClass("text-danger").addClass(
										"text-success").text(
										"Use this dataName.");
										callback(true);

									} else {
									    $("#errorMsg").removeClass("text-success").addClass("text-danger").text("Invalid dataName.");
										callback(false);
									}
								},
								error : function(xhr, status, error) {
									console.error(
											'Error checking unique data name:',
											error);
									callback(false);
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
