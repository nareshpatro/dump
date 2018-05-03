/* --------------------------------------
    File Name contactInfo.js
    Author Jules Rodriguez (PCCW)
    Date April 19, 2018
    Description
    All functions used uniquely in the Contact Info tab.
    
    ---------- Modification History ----------

   -------------------------------------- -*/
var errorMessages = [];
var fieldWithError;
var segment_name = [];

$(document).ready(function(){
	
	$('.dateType').datepicker({ dateFormat: 'dd/mm/yy' });
	
	if(!isNotNullAddress() && $('#addressTable tr').length < 3){
		disableIcons();
	}
	//Validation
	$('.toDate').on("change paste keyup", function() {
		var fromInput = $(this).parents('tr').find('.fromDate');
		if (fromInput.val() !== "" && $(this).val() !== "") {
			var adrFrom = fromInput.val().split('/');
			var to = $(this).val().split('/');
			var fromDate = adrFrom[1] + '/' + adrFrom[0] + '/' + adrFrom[2];
			var toDate = to[1] + '/' + to[0] + '/'  + to[2];
			if (new Date(fromDate).getTime() > new Date(toDate).getTime()) {
				generateError('07301', 'Payroll'); //INVALID_DATE_TO
				if($(this).parents('tr').find('.addrTypeSelect').val() == ""){
					segment_name.push('Type');
				}
				if($(this).parents('tr').find('.fromDate').val() == ""){
					segment_name.push('From');
				}
				if($(this).parents('tr').find('.addressText').val() == ""){
					segment_name.push('Address');
					generateError('01023', 'Application Object Library'); //MISSING_VALUE
				}
				setTimeout(function(){}, 500);
				showErrors('errorAlert');
				fieldWithError = $(this).parents('tr').find('.addressText');
				focusOnError(fieldWithError);
			}
    	}
	});
	var delayedFn, blurredFrom;
	$('.addressTRow').on('blur', 'input select checkbox', function(event) {
		blurredFrom = event.delegateTarget;
		delayedFn = setTimeout(function() {
			//alert('Blurred');
		}, 0);
	});
	$('.addressTRow').on('focus', 'input select checkbox', function(event) {
		if (blurredFrom === event.delegateTarget) {
			clearTimeout(delayedFn);
		}
	});
		
});

function isNotNullAddress(){
	var addrType = $('#addrType').val() !== "";
	var addrFrom = $('#addrFrom').val() !== "";
	var addrAddress = $('#addrAddress').val() !== "";
	return addrType && addrFrom && addrAddress;
}

//for enabling/disabling update and delete icons
function disableIcons(){
	$('#addressTable .forDisabling').css({'pointer-events': 'none', 'cursor': 'default'});
}

//removing a table row line
function removeLine() {
	$(event.target).closest("tr").remove();
}

//updating a table row line
function updateAddressLine() {
	
	$(event.target).parents('tr').find('#empAddPrimaryChkbx').removeAttr("disabled");
	
	var currentTD = $(event.target).parents('tr').find('.empAdd');
	$.each(currentTD, function () {
		$(this).removeAttr("disabled");
	});
	
	$(event.target).parents('tr').find('#addrType').focus();
	$(event.target).parents('tr').find('.dateType').removeAttr("disabled");
	$(event.target).parents('tr').find('.dateType').attr('placeholder','dd/mm/yyyy');
	
}

//inserting a row line or enabling a row for update if no records
function insertAddressLine() {
	
	//if with records already
	if($('#addressTable tr').length > 2 || isNotNullAddress()){
		var tbody = document.getElementById("addressTBody");
		var row = tbody.insertRow(0);
		var cellInsert = row.insertCell(0);
		var cellUpdate = row.insertCell(1);
		var cellDelete = row.insertCell(2);
		var cellPrimary = row.insertCell(3);
		var cellType = row.insertCell(4);
		var cellFrom = row.insertCell(5);
		var cellTo = row.insertCell(6);
		var cellAddress = row.insertCell(7);
		var cellDistrict = row.insertCell(8);
		var cellArea = row.insertCell(9);
		var cellCountry = row.insertCell(10);
		
		cellInsert.innerHTML = '<form:form class="employeeAddressForm" action="processEmpAddress" method="POST" modelAttribute="contactInfoAddress">  '+
			'<center><a onclick="insertAddressLine()" href="#"><span class="glyphicon glyphicon-plus" style="color:#337ab7;"/></a></center>';		
		cellUpdate.innerHTML = '<center><a onclick="updateAddressLine()" class="forDisabling" href="#"><span class="glyphicon glyphicon-edit" style="color:#337ab7;"/></a></center>';
		cellDelete.innerHTML = '<center><a onclick="removeLine()" class="forDisabling" href="#"><span class="glyphicon glyphicon-remove" style="color:#337ab7;"/></a></center>';
		cellPrimary.innerHTML = '<center><form:checkbox path="addrPrimary" id="empAddPrimaryChkbx" style="background-color:white; border: none" disabled="true"/></center>';
		cellType.innerHTML = '<form:select path="addrType" class="form-control" id="addrType" style="background-color: white; border:none; width: 100%; 	height: 100%;" disabled="true" required="required">'+
								'<form:option value="" label=" " /><form:options items="${addrTypeList}" itemValue="name" itemLabel="desc"/></form:select>';
		cellFrom.innerHTML = '<form:input path="addrFrom"  class="form-control empAddDate dateType fromDate" style="background-color: white; border:none; width: 100%; height: 100%;" id="addrFrom" required="required" disabled="true"/>';
		cellTo.innerHTML = '<form:input path="addrTo" class="form-control empAddDate dateType toDate" style="background-color: white; border:none; width: 100%; height: 100%;"  id="addrTo" disabled="true"/>';
		cellAddress.innerHTML = '<form:input path="addrAddress"  class="form-control empAdd" style="border:none; width: 100%; height: 100%;" id="addrAddress" type="text" maxlength="240" required="required" disabled="true"/>';
		cellDistrict.innerHTML = '<form:input path="addrDistrict" class="form-control empAdd" style="border:none; width: 100%; height: 100%;" id="addrDistrict" maxlength="30" disabled="true"/>';
		cellArea.innerHTML = '<form:input path="addrArea" class="form-control empAdd" style="border:none; width: 100%; height: 100%;" id="addrArea" disabled="true"/>';
		cellCountry.innerHTML = '<form:input path="addrCountry"  class="form-control empAdd" style="border:none; width: 100%; height: 100%;" disabled="true"/>'+
			'</form:form>';
		
	//if no records	
	}else if(!isNotNullAddress() && $('#addressTable tr').length < 3){
		updateAddressLine();
		disableIcons();
	}
}

function generateError(errorCode, applicationName){
	var errorObj = {'errorCode' : errorCode, 'applicationName' : applicationName};
	$.ajax({
		type : "POST",
		url : "./getErrorMessages",
		data : JSON.stringify(errorObj),
		contentType: 'application/json',
		success: function(data){
			errorMessages.push(data);
		}
	});
	return true;
}

function showErrors(alertId){
	var errors = "";
	$('#' + alertId).removeClass("alert-success");
	$('#' + alertId).removeClass("alert-warning");
	$('#' + alertId).addClass("alert-danger");
	
	for(var i = 0; i < errorMessages.length; i++){
		var err = errorMessages[i].replace('&SEGMENT_NAME', segment_name.join(', '));
		errors += "<strong><span class='glyphicon glyphicon-exclamation-sign'></span> Error!</strong> " + err + "<br>";
	}
	$('#' + alertId).html(errors);	
	if(errorMessages.length > 0) $('#' + alertId).show();
    $('html, #errorAlert').animate({ scrollTop: 0 }, 'slow');
	errorMessages = [];
	segment_name = [];
}

function focusOnError(fieldWithError){
	fieldWithError.focus();
}