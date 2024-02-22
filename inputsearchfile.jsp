<%@ include file="session.jsp"%>
<%@ include file="header.jsp"%>
<script src="${pageContext.request.contextPath}/path.js"></script>
<!-- Page Body Start-->
<style>
.small-text {
	font-size: 15px;
}
</style>
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
			<div class="row">
				<div class="col-sm-2 mb-3">
					<input type="text" id="targetFileName" name="targetFileName"
						class="form-control" placeholder="Search" value="">
				</div>
				<div class="col-sm-4 mb-3">
					<button type="button" class="btn btn-primary small-text"
						id="searchbtn" name="searchbtn">Search</button>
				</div>
			</div>
			<!-- Container-fluid starts-->
			<div class="container-fluid basic_table">
				<div class="row">
					<div class="col-sm-12">
						<div class="card">
							<div>
								<input type="hidden" value=<%=session.getAttribute("userId")%>
									id="userId" name="userId"> <input type="hidden"
									value="" name="id" id="id">
								<table class="table table-border">
									<thead>
										<tr>
											<th scope="col">File List</th>
											<th scope="col">Download</th>
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
		<%@ include file="footer.jsp"%>
	</div>
</div>

<script>
	$(document)
			.ready(
					function() {
						$('#searchbtn').click(function() {
							var targetFileName = $('#targetFileName').val();

							if (targetFileName.trim() === '') {
								alert('Please enter a search value.');
								return; // Stop execution if the search value is empty
							}

							$.ajax({
								url : mainUrl + 'search',
								type : 'POST',
								data : {
									targetFileName : targetFileName,
								},
								success : function(response) {
									updateFileList(response);
								},
								error : function(error) {
									console.log('Error:', error);
								},
							});
						});

						function updateFileList(response) {
							var fileList = $('#myTable');
							fileList.empty();

							if (response.fileNames.length === 0) {
								fileList
										.append('<tr><td colspan="2">No files found</td></tr>');
							} else {
								var headerRow = $('<tr>');
								for (var i = 0; i < response.fileNames.length; i++) {
									var fileName = response.fileNames[i];
									var filePath = response.filePaths[i];
									var row = $('<tr>');
									var fileNameCell = $('<td>').text(fileName);
									var downloadCell = $('<td>');
									var downloadButton = $('<a>').addClass(
											'btn btn-primary small-text').text(
											'Download').attr('href',
											mainUrl + 'filedata/' + fileName)
											.appendTo(downloadCell);
									row.append(fileNameCell);
									row.append(downloadCell);
									fileList.append(row);
								}
							}
						}
					});
</script>
