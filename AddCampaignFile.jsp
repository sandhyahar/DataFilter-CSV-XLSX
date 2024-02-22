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
				<div id="errorMsg"></div>
				<input type="hidden" value=<%=session.getAttribute("userId")%>
					id="userId" name="userId"> <input type="hidden"
					name="filepath" id="filepath">
				<div class="row">
					<div class="row">
					<!-- <div class="mb-3">
							<span class="fw-bold" style="color: red">Note: Only xlsx
								file upload.</span>
						</div> -->
						<div class="col-sm-2 mb-3">
							<input type="file" id="filedata" name="filedata"
								class="form-control" value="" accept="" >
						</div>
						<div class="col-sm-4 mb-3">
							<button type="button" class="btn btn-primary small-text"
								id="uploadbtn" name="uploadbtn" onclick="uploadFile()">Upload</button>

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
							id="userId" name="userId"> <input type="hidden" name="id"
							id="id" value="<%=request.getParameter("id")%>"> <input
							type="hidden" name="filepath" id="filepath"
							value="<%=request.getParameter("filepath")%>">
					</form>
				</div>
			</div>
		</div>
	</div>
</div>
<%@ include file="footer.jsp"%>
<script>
	function uploadFile() {
		// Get the selected file from the input element
		var fileData = $('#filedata')[0].files[0];
		if (!fileData) {
			$("#errorMsg").removeClass("text-success").addClass("text-danger")
					.text("Please select a file.");
			return false;
		}

		// Create a FormData object to send the file and other parameters to the server
		var formData = new FormData();
		formData.append('filedata', $('#filedata')[0].files[0]);
		formData.append('userId', $("#userId").val());
		formData.append('id', $("#id").val());

		// Make an AJAX request to update the campaign data and upload the file to the server
		$.ajax({
			url : mainUrl + '/updateCampaignData',
			type : 'POST',
			data : formData,
			processData : false,
			contentType : false,
			beforeSend : function() {
				// Show the loading spinner while uploading
				$("#spinner").show();
			},
			success : function(response) {
				$("#spinner").hide();
				alert(response);
				window.location.href = "templates.jsp";

			},
			error : function(xhr, status, error) {
				console.error(error);
				$("#spinner").hide();
				alert('Update failed.'); // Handle the failure case
			}
		});
	}
</script>