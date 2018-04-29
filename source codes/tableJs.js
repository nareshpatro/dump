/* --------------------------------------
    File Name searchPeople.js
    Author Carmen Ng (PCCW)
    Date April 19, 2018
    Description
    All functions used uniquely in the create work requests page.
    
    ---------- Modification History ----------

   -------------------------------------- -*/

$(document).ready(function() {

  if (!isValidAddress() && $('#addressTable tr').length < 3) {
    disableIcons();
  }
  $('#addrTo').on("change paste keyup", function() {
    if ($('#addrFrom').val() !== "" && $('#addrTo').val() !== "") {
	var from = $('#addrFrom').val().split('/');
	var to = $('#addrTo').val().split('/');
	var fromDate = from[1] + '/' + from[0] + '/' + from[2];
	var toDate = to[1] + '/' + to[0] + '/'  + to[2];
      	if (new Date(fromDate).getTime() > new Date(toDate).getTime()) {
        	alert('from is greater than to');
     	 }
    	}
  });


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


});


function submit(table) {
  if ($(table).is(":focus")) {
    return true
  }
}

function isValidAddress() {
  var addrType = $('#addrType').val() !== "";
  var addrFrom = false; // $('#addrFrom').text().match();
  var addrAddress = $('#addrAddress').text() !== null;
  return addrType && addrFrom && addrAddress;
}

//for enabling/disabling update and delete icons
function disableIcons() {
  $('#addressTable .forDisabling').css({
    'pointer-events': 'none',
    'cursor': 'default'
  });
}

//removing a table row line
function removeLine() {
  $(event.target).closest("tr").remove();
}

//updating a table row line
function updateAddressLine() {

  $(event.target).parents('tr').find('#empAddPrimaryChkbx').removeAttr("disabled");
  $(event.target).parents('tr').find('#addrType').removeAttr("disabled");
  $(event.target).parents('tr').find('#addrType').focus();
  $(event.target).parents('tr').find('.dateType').removeAttr("disabled");

  $(event.target).parents('tr').find('.dateType').attr('placeholder', 'dd/mm/yyyy');

  var currentTD = $(event.target).parents('tr').find('.empAdd');
  $.each(currentTD, function() {
    $(this).removeAttr("disabled");
  });

}

//inserting a row line or enabling a row for update if no records
function insertAddressLine() {

  //if with records already
  if ($('#addressTable tr').length > 2 || isValidAddress()) {
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

    cellInsert.innerHTML = '<form:form class="employeeAddressForm" action="processEmpAddress" method="POST" modelAttribute="contactInfoAddress">  ' +
      '<center><a onclick="insertAddressLine()" href="#"><span class="glyphicon glyphicon-plus" style="color:#337ab7;"/></a></center>';
    cellUpdate.innerHTML = '<center><a onclick="updateAddressLine()" class="forDisabling" href="#"><span class="glyphicon glyphicon-edit" style="color:#337ab7;"/></a></center>';
    cellDelete.innerHTML = '<center><a onclick="removeLine()" class="forDisabling" href="#"><span class="glyphicon glyphicon-remove" style="color:#337ab7;"/></a></center>';
    cellPrimary.innerHTML = '<center><form:checkbox path="addrPrimary" id="empAddPrimaryChkbx" style="background-color:white; border: none" disabled="true"/></center>';
    cellType.innerHTML = '<form:select path="addrType" class="form-control" id="addrType" style="background-color: white; border:none; width: 100%; 	height: 100%;" disabled="true" required="required">' +
      '<form:option value="" label=" " /><form:options items="${addrTypeList}" itemValue="name" itemLabel="desc"/></form:select>';
    cellFrom.innerHTML = '<form:input path="addrFrom"  class="form-control empAddDate dateType fromDate" style="background-color: white; border:none; width: 100%; height: 100%;" id="addrFrom" required="required" disabled="true"/>';
    cellTo.innerHTML = '<form:input path="addrTo" class="form-control empAddDate dateType toDate" style="background-color: white; border:none; width: 100%; height: 100%;"  id="addrTo" disabled="true"/>';
    cellAddress.innerHTML = '<form:input path="addrAddress"  class="form-control empAdd" style="border:none; width: 100%; height: 100%;" id="addrAddress" type="text" maxlength="240" required="required" disabled="true"/>';
    cellDistrict.innerHTML = '<form:input path="addrDistrict" class="form-control empAdd" style="border:none; width: 100%; height: 100%;" id="addrDistrict" maxlength="30" disabled="true"/>';
    cellArea.innerHTML = '<form:input path="addrArea" class="form-control empAdd" style="border:none; width: 100%; height: 100%;" id="addrArea" disabled="true"/>';
    cellCountry.innerHTML = '<form:input path="addrCountry"  class="form-control empAdd" style="border:none; width: 100%; height: 100%;" disabled="true"/>' +
      '</form:form>';

    //if no records	
  } else if (!isValidAddress() && $('#addressTable tr').length < 3) {
    updateAddressLine();
    disableIcons();
  }
}

