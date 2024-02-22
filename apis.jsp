
<%@ include file="session.jsp"%>
<%@ include file="header.jsp"%>
<script src="${pageContext.request.contextPath}/path.js"></script>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<!-- Page Body Start-->
<style>
.small-text {
	font-size: 15px;
}
</style>

<div class="page-body-wrapper">
	<div class="page-body">
		<div class="container">
			<div class="page-title">
				<div class="row">
					<form class="theme-form" id="myForm">
						<h4>Create campaign</h4>
						<div class="alert alert-primary" role="alert">Duplicate data
							will automatically remove</div>
						<div id="errorMsg2"></div>
						<input type="hidden" value=<%=session.getAttribute("userId")%>
							id="userId" name="userId"> <label class="fw-bold">CampaignName</label>
						<div class="input-group mb-3">
							<div class="input-group-prepend">
								<input type="text" class="form-control" required=""
									placeholder="Enter CampangName" aria-label=""
									aria-describedby="basic-addon1" id="campaignName"
									name="campaignName">
							</div>
							<button class="btn btn-primary btn-xs small-text" type="button"
								id="checkCampaignNameBtn">Check duplicate</button>
						</div>
				</div>

				<div>
					<span id="errorMsg"></span>
				</div>
				<div class="form-group"></div>
				<div class="form-group">
					<label class="col-form-label fw-bold">Campaign List</label>
					<div class="dropdown">
						<button class="btn btn-default dropdown-toggle small-text"
							type="button" data-toggle="dropdown" aria-haspopup="true"
							aria-expanded="false" name="campaignList" id="campaignList">Select
							Campaign</button>
						<div class="dropdown-menu" aria-labelledby="dropdownMenuButton"
							style="max-height: 200px; overflow-y: auto;">
							<div id="campaignDropdown"></div>
						</div>
						<input type="hidden" id="selectedOptionsInput" name="campaignList">
						<label id="selectedOptionsLabel" placeholder="Selected Campaigns"></label>

					</div>

				</div>
				<div class="form-group">
					<label class="col-form-label fw-bold">File Data</label>
					<div class="form-input position-relative">
						<input type="file" name="filedata" id="filedata" required="">
					</div>
				</div>
				<div id="errorMsg3"></div>
				<div id="spinner" style="display: none;">
					<div class="spinner-border text-primary" role="status">
						<span class="visually-hidden">Loading...</span>
					</div>
					<div class="text-primary">Processing...</div>
				</div>
			</div>
			<div>
				<input type="button" class="btn btn-primary small-text"
					value="Submit" onclick="uploadFile();">
			</div>
		</div>
		</form>
	</div>
</div>
<%@ include file="footer.jsp"%>
<script>
	function uploadFile() {
		var campaignName = $('#campaignName').val().trim();

		if (!campaignName) {
			$("#errorMsg").removeClass("text-success").addClass("text-danger")
					.text("Campaign name is empty.");
			return false;
		}

		var fileData = $('#filedata')[0].files[0];
		if (!fileData) {
			$("#errorMsg3").removeClass("text-success").addClass("text-danger")
					.text("Please select a file.");
			return false;
		}
		
		//$("#spinner").show();

		data = new FormData();
		data.append('filedata', $('#filedata')[0].files[0]);
		data.append('campaignList', $('#selectedOptionsInput').val());
		data.append('campaignName', $('#campaignName').val());
		data.append('userId', $('#userId').val());

		$.ajax({
			url : mainUrl + 'insertCampaignData',
			data : data,
			processData : false,
			contentType : false,
			type : 'POST',
			cache : false,
			 beforeSend: function() {
		            // Show the spinner before making the AJAX call
		            $("#spinner").show();
		        },
			success : function(data) {
				var obj = JSON.parse(data);
				if (obj.flag === true) {
	                $(".spinner").hide();
					$("#errorMsg2").removeClass("text-danger").addClass(
							"text-success").text(
							"Campaign submitted successfully.");
					
					// Disable the button
					$(this).prop("disabled", true);
					

					window.location.href = "templates.jsp";

				} else {
					// Disable the button
					$(this).prop("disabled", false);
				}
			}
		});

		return checkCampaignName();

	}

	function checkCampaignName() {
		var flag = false;
		var campaignName = $('#campaignName').val().trim();
		if (campaignName === "") {
			$("#errorMsg").removeClass("text-success").addClass("text-danger")
					.text("Campaign name is empty.");
			return false; // Return false to indicate that the check failed
		}
		var attr = {
			"campaignName" : campaignName
		};
		$.ajax({
			url : mainUrl + "checkUniqueUserName",
			type : "POST",
			contentType : "application/json",
			data : JSON.stringify(attr),
			success : function(data) {
				var obj = JSON.parse(data);
				if (obj.flag === true) {
					$("#errorMsg").removeClass("text-danger").addClass(
							"text-success").text(
							"You can use this Campaign name.");
				} else {
					$("#errorMsg").removeClass("text-success").addClass(
							"text-danger").text(
							"Campaign name is already exists.");
				}
			}
		});

	}
	function loadCampaignList() {
		$
				.ajax({
					url : mainUrl + "findCampaignNameAndId?userId="
							+ $('#userId').val(),
					method : "GET",
					success : function(response) {
						var data = JSON.parse(response)
						var campaignList = data.list // Convert JSON to JavaScript object

						var dropdownContainer = $("#campaignDropdown");

						for (var i = 0; i < campaignList.length; i++) {
							var campaign = campaignList[i];
							console.log(campaign);
							if (campaign.isVisible === 1) {
								var checkbox = $('<div class="form-check"><input class="form-check-input" type="checkbox" value="' + campaign.id + '" id="checkbox' + i + '"><label class="form-check-label" for="checkbox' + i + '">'
										+ campaign.campaignName
										+ '</label></div>');
								dropdownContainer.append(checkbox);

							}
						}
					},
					error : function(xhr, status, error) {
						console.log("Error: " + error);
					}
				});
	}
	$(document).ready(
			function() {
				$('#checkCampaignNameBtn').click(function() {
					checkCampaignName();
				});

				loadCampaignList();

				// Toggle the dropdown when the dropdown button is clicked
				$(".dropdown-toggle").on("click", function() {
					$(this).siblings(".dropdown-menu").toggleClass("show");
				});

				// Handle checkbox click event
				$(".dropdown-menu .form-check-input").on("click",
						function(event) {
							event.stopPropagation();
						});

				// Update the selected options when a checkbox is clicked
				$(document).on(
						"change",
						".dropdown-menu .form-check-input",
						function() {
							var selectedOptions = [];
							var selectedCampaign = [];
							// Iterate over the checked checkboxes and get their values and labels
							$(".dropdown-menu .form-check-input:checked").each(
									function() {
										var value = $(this).val();
										var label = $(this).siblings("label")
												.text();
										selectedOptions.push(value);
										selectedCampaign.push(label);
									});

							var selectedValues = selectedOptions.join(",");
							$("#selectedOptionsInput").val(selectedValues);

							var selectedName = selectedCampaign.join(",");
							$("#selectedOptionsLabel").text(selectedName); // Set innerHTML of the label
						});

				// Close the dropdown when clicked outside
				$(document)
						.on(
								"click",
								function(event) {
									if ($(".dropdown-menu").hasClass("show")
											&& !$(event.target).closest(
													".dropdown").length) {
										$(".dropdown-toggle").siblings(
												".dropdown-menu").removeClass(
												"show");
									}
								});
			});
</script>
