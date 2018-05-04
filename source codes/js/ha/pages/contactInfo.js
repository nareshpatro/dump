/* --------------------------------------
    File Name contactInfo.js
    Author Jules Rodriguez (PCCW)
    Date April 19, 2018
    Description
    All functions used uniquely in the Contact Info tab.
    
    ---------- Modification History ----------

   -------------------------------------- -*/
   
var errorMessages = {"messages": []} 
var fieldWithError;
var segment_name = [];

$(document).ready(function(){
	
	$('.dateType').datepicker({ dateFormat: 'dd/mm/yy' });
	
	//Insert record
	$('.insertLine').click(function(){
		var row = $(this).parents('tr');
		insertAddressLine(row.closest('table').prop('id'), row);
	});	
	
	//Update record
	$('.updateAddressRec').click(function(){
		var row = $(this).parents('tr');
		enableFields('addressTable', row);
	});
	
	//Delete record
	$('.deleteRec').click(function(){
		var row = $(this).parents('tr');
		$('<div></div>').appendTo('body')
		.html('<div><h6>Do you really want to delete this record?</h6></div>')
		.dialog({
			modal: true, title: 'Warning', zIndex: 10000, autoOpen: true,
			width: 'auto', resizable: false,
			buttons: {
				Yes: function () {	
					var tableId = row.closest('table').prop('id');
					if($('#' + tableId + ' tr').length > 2){
						
						//insert ajax delete function rec here
						row.remove();
						
					}else{	
					
						//insert ajax delete function rec here
						
						row.find('select').val('');
						row.find('input').val('');
						row.find('.addrPrimary').prop('checked', false);				
						disableIcons(tableId);
					}
					disableFields(tableId, row);
					$(this).dialog("close");
				},
				No: function () {                                                    
					$(this).dialog("close");
				}
			},
			close: function (event, ui) {
				$(this).remove();
			}
		});				
	});
	
	//Validation for Address region
	$('.toDate').on("change paste keyup", function() {
		var row = $(this).parents('tr');
		var fromInput = row.find('.fromDate');
		if (fromInput.val() !== "" && $(this).val() !== "") {
			var adrFrom = fromInput.val().split('/');
			var to = $(this).val().split('/');
			var fromDate = adrFrom[1] + '/' + adrFrom[0] + '/' + adrFrom[2];
			var toDate = to[1] + '/' + to[0] + '/'  + to[2];
			if (new Date(fromDate).getTime() > new Date(toDate).getTime()) {
				addErrMsgParams('07301', 'Payroll'); //for INVALID_DATE_TO error
			}
    	}
		
		if(row.find('.addrTypeSelect').val() == ""){
			segment_name.push('Type');
			addErrMsgParams('01023', 'Application Object Library'); //for MISSING_VALUE error
		}	
		if(row.find('.fromDate').val() == ""){
			segment_name.push('From');
			addErrMsgParams('01023', 'Application Object Library'); //for MISSING_VALUE error
		}
		if(row.find('.addressText').val() == ""){
			segment_name.push('Address');
			addErrMsgParams('01023', 'Application Object Library'); //for MISSING_VALUE error
		}
		if(errorMessages.messages.length > 0) {
			generateError('errorAlert');
			fieldWithError = $(this).parents('tr').find('.addrTypeSelect'); //tentative
			focusOnError(fieldWithError);
		}else{
			var row = $(this).parents('tr');
			$('#errorAlert').hide();	
			enableIcons('addressTable');
			disableFields('addressTable', row);		
		}			
	});
	
	/*
	* For selecting the primary address.
	* This will allow only one checkbox checked.
	*/
	$("input:checkbox").on('click', function() {  
 	 	var $box = $(this);
 	 	if ($box.is(":checked")) {
    		var group = "input:checkbox[name='" + $box.attr("name") + "']";
   			$(group).prop("checked", false);
    		$box.prop("checked", true);
  		} else {
    		$box.prop("checked", false);
 		}
	});
	
	$.ajax({
		type : "POST",
		url : "./getAddressTypeList",
		success: function(list){
			for(var i = 0; i < list.length; i++){
				$('.addrTypeSelect').append('<option value="'+ list[i]["name"] +'">' + list[i]["desc"] +'</option>');
			}
		}
	});
		
	
	$.ajax({
		type : "POST",
		url : "./getArea",
		success: function(list){
			for(var i = 0; i < list.length; i++){
				$('.areaSelect').append('<option value="'+ list[i]["name"] +'">' + list[i]["desc"] +'</option>');
			}
		}
	});
	
	$.ajax({
		type : "POST",
		url : "./getCountry",
		success: function(list){
			for(var i = 0; i < list.length; i++){
				$('.countrySelect').append('<option value="'+ list[i]["name"] +'">' + list[i]["desc"] +'</option>');
			}
			
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
function enableIcons(tableId){	
	$('#' + tableId + ' .forDisabling').css({'pointer-events': '', 'cursor': ''});
}

function enableFields(tableId, row){
	if(tableId == 'addressTable'){
		var primaryInput = row.find('.addrPrimary');		
		var typeInput = row.find('.addrTypeSelect');
//		var typeInputDiv = row.find('.addrTypeSelectDiv');
		var fromInput = row.find('.fromDate');
		var toInput = row.find('.toDate');
		var addressInput = row.find('.addressText');
		var districtInput = row.find('.districtText');
		var areaInput = row.find('.areaSelect');
//		var areaInputDiv = row.find('.areaSelectDiv');
		var countryInput = row.find('.countrySelect');	
//		var countryInputDiv = row.find('.countrySelectDiv');	
		
		var typeDiv = row.find('.addrTypeDiv');
		var fromDiv = row.find('.addrFromDiv');
		var toDiv = row.find('.addrToDiv');
		var addressDiv = row.find('.addrAddressDiv');
		var districtDiv = row.find('.addrDistrictDiv');
		var areaDiv = row.find('.addrAreaDiv');
		var countryDiv = row.find('.addrCountryDiv');
		
		var typeVal = typeDiv.text();
		var fromVal = fromDiv.text();
		var toVal = toDiv.text();
		var addressVal = addressDiv.text();
		var districtVal = districtDiv.text();
		var areaVal = areaDiv.text();
		var countryVal = countryDiv.text();	
	
		primaryInput.removeAttr("disabled");
		primaryInput.focus();
				
		typeInput.filter(function() {return (typeVal)? $(this).text() == typeVal : $(this).text() ==  " ";}).prop('selected', true);
		fromInput.val(fromVal);
		toInput.val(toVal);
		addressInput.val(addressVal);
		districtInput.val(districtVal);
		areaInput.filter(function() {return (areaVal)? $(this).text() == areaVal : $(this).text() ==  " ";}).prop('selected', true);
		countryInput.filter(function() {return (countryVal)? $(this).text() == countryVal : $(this).text() ==  " ";}).prop('selected', true);
		
		typeInput.show();
		fromInput.show();
		toInput.show();
		addressInput.show();
		districtInput.show();
		areaInput.show();
		countryInput.show();
		
		typeDiv.hide();
		fromDiv.hide();
		toDiv.hide();
		addressDiv.hide();
		districtDiv.hide();
		areaDiv.hide();
		countryDiv.hide();		
		
		row.find('.dateType').attr('placeholder','dd/mm/yyyy');
	}
}

function disableFields(tableId, row) {
	if(tableId == 'addressTable'){
		var primaryInput = row.find('.addrPrimary');	
		var typeInput = row.find('.addrTypeSelect');
	//	var typeInputDiv = row.find('.addrTypeSelectDiv');
		var fromInput = row.find('.fromDate');
		var toInput = row.find('.toDate');
		var addressInput = row.find('.addressText');
		var districtInput = row.find('.districtText');
		var areaInput = row.find('.areaSelect');
	//	var areaInputDiv = row.find('.areaSelectDiv');
		var countryInput = row.find('.countrySelect');	
	//	var countryInputDiv = row.find('.countrySelectDiv');	
		
		var typeDiv = row.find('.addrTypeDiv');
		var fromDiv = row.find('.addrFromDiv');
		var toDiv = row.find('.addrToDiv');
		var addressDiv = row.find('.addrAddressDiv');
		var districtDiv = row.find('.addrDistrictDiv');
		var areaDiv = row.find('.addrAreaDiv');
		var countryDiv = row.find('.addrCountryDiv');
		
		var typeVal = typeInput.children("option").filter(":selected").text();
		var fromVal = fromInput.val();
		var toVal = toInput.val();
		var addressVal = addressInput.val();
		var districtVal = districtInput.val();
		var areaVal = areaInput.children("option").filter(":selected").text();
		var countryVal = countryInput.children("option").filter(":selected").text();
		
		primaryInput.prop('disabled', true);
		typeDiv.text(typeVal);
		fromDiv.text(fromVal);
		toDiv.text(toVal);
		addressDiv.text(addressVal);
		districtDiv.text(districtVal);
		areaDiv.text(areaVal);
		countryDiv.text(countryVal);
		
		typeInput.hide();
		fromInput.hide();
		toInput.hide();
		addressInput.hide();
		districtInput.hide();
		areaInput.hide();
		countryInput.hide();
		
		typeDiv.show();
		fromDiv.show();
		toDiv.show();
		addressDiv.show();
		districtDiv.show();
		areaDiv.show();
		countryDiv.show();
		
	}
}

function disableIcons(tableId){
	$('#' + tableId + ' .forDisabling').css({'pointer-events': 'none', 'cursor': 'default'});
}

//updating a table row line
function updateAddressLine() {
	
	$(event.target).parents('tr').find('.addrPrimary').removeAttr("disabled");
	
	var currentTD = $(event.target).parents('tr').find('.empAdd');
	$.each(currentTD, function () {
		$(this).removeAttr("disabled");
	});
	
	$(event.target).parents('tr').find('.addrTypeSelect').focus();
	$(event.target).parents('tr').find('.dateType').removeAttr("disabled");
	$(event.target).parents('tr').find('.dateType').attr('placeholder','dd/mm/yyyy');
	
}


//inserting a row line or enabling a row for update if no records
function insertAddressLine(tableId, row) {
	
	//if with records already
	if($('#addressTable tr').length > 2 || isNotNullAddress()){
		var newRow = row.cloneNode(true);
		document.getElementById(tableId).appendChild(newRow);
		
	/*	var tbody = document.getElementById("addressTBody");
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
		
		cellInsert.innerHTML = '<form:form class="employeeAddressForm" action="processEmpAddress" method="POST" modelAttribute="contactInfoAddress"> '+
			'<a onclick="insertAddressLine()" href="#"><span class="glyphicon glyphicon-plus centerElement"/></a>';		
		cellUpdate.innerHTML = '<a onclick="updateAddressLine()" class="forDisabling" href="#"><span class="glyphicon glyphicon-edit centerElement"/></a>';
		cellDelete.innerHTML = '<a class="forDisabling deleteRec" href="#"><span class="glyphicon glyphicon-remove centerElement"/></a>';
		cellPrimary.innerHTML = '<center><input type="checkbox" path="addrPrimary" class="addrPrimary"/></center>';
		cellType.innerHTML = '<form:select path="addrType" class="form-control empAdd addrTypeSelect" id="addrType" required="required">'+
								'<form:option value="" label=" " /><form:options items="${addrTypeList}" itemValue="name" itemLabel="desc"/></form:select>';
		cellFrom.innerHTML = '<form:input path="addrFrom" class="form-control empAddDate dateType fromDate" id="addrFrom" required="required" disabled="true"/>';
		cellTo.innerHTML = '<form:input path="addrTo" class="form-control empAddDate dateType toDate" id="addrTo" />';
		cellAddress.innerHTML = '<form:input path="addrAddress"  class="form-control empAdd addressText" id="addrAddress"  maxlength="240" required="required" disabled="true"/>';
		cellDistrict.innerHTML = '<form:input path="addrDistrict" class="form-control empAdd" id="addrDistrict" maxlength="30"/>';
		cellArea.innerHTML = '<form:select path="addrArea" class="form-control empAdd" id="addrArea">'+
			'<form:option value="" label=" " /><form:options items="${addrAreaList}" itemValue="name" itemLabel="desc"/></form:select>';
		cellCountry.innerHTML = '<form:select path="addrCountry" class="form-control empAdd" id="addrCountry">'+
			'<form:option value="" label=" " /><form:options items="${addrCountryList}" itemValue="name" itemLabel="desc"/></form:select>' +
			'</form:form>';
			
		enableFields('addressTable', row);
		
		row.find('.addrPrimary').removeAttr("disabled");
		
		var currentTD = row.find('.empAdd');
		$.each(currentTD, function () {
			$(this).removeAttr("disabled");
		});
		
		row.find('.addrTypeSelect').focus();
		row.find('.dateType').removeAttr("disabled");
		row.find('.dateType').attr('placeholder','dd/mm/yyyy');		
		*/
		
		
		
	//if no records	
	}else if(!isNotNullAddress() && $('#addressTable tr').length < 3){
		enableFields('addressTable', row);
		//updateAddressLine();
		disableIcons('addressTable');
	}
}
function generateError(alertId){
	$.ajax({
		type : "POST",
		url : "./getErrorMessages",
		data : JSON.stringify(errorMessages),
		contentType: 'application/json',
		success: function(messages){
			var errors = "";			
			var counter = 0;
			$('#' + alertId).removeClass("alert-success");
			$('#' + alertId).removeClass("alert-warning");
			$('#' + alertId).addClass("alert-danger");		
	
			for(var i = 0; i < messages.length; i++){
				var err = messages[i];										
				if(err.indexOf('&SEGMENT_NAME') > -1){
					err = messages[i].replace('&SEGMENT_NAME', segment_name[counter]);
					counter++;
				}
				errors += "<strong><span class='glyphicon glyphicon-exclamation-sign'></span> Error!</strong> " + err + "<br>";
			}
			$('#' + alertId).html(errors);					
			$('#' + alertId).show();
			$('html, #errorAlert').animate({ scrollTop: 0 }, 'slow');	
			
			errorMessages.messages = [] ;	
			segment_name = [];	
		}
	});
}

function addErrMsgParams(errorCode, applicationName){
	errorMessages.messages.push({'errorCode' : errorCode, 'applicationName' : applicationName});
}

function focusOnError(fieldWithError){
	fieldWithError.focus();
}