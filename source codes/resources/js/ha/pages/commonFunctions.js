/* --------------------------------------
    File Name commonfunctions.js
    Author Carmen Ng (PCCW)
    Date ‎August ‎29, ‎2017
    Description
    All common functions and element functions used by more than one page.

    ---------- Modification History ----------
	20171215 by Carmen Ng
	Fixed remove attachment not working with filename with spaces
	
	20171213 by Carmen Ng
    Moved toggleCheckValidity  to search js
	20171207 by Carmen Ng
    Moved wrtype dropdown change check to createRequests
	Added vendor name to contract lov search
    
	20171206 by Carmen Ng
	Updated clearAttachmenInfo
	Bolded delete button
	
	20171205 by Carmen Ng
	When select asset number, trigger blur instead of get info so validation is triggered
	Updated the attachments layout to rows
	
	20171129 by Carmen Ng
	Added delCookie function
	Trigger change when hiddenmbody value  is added/removed
	
	20171128 by Carmen Ng
	Added out of focus before toggle to avoid IE bug
	
	20171128 by Carmen Ng
	Fixed issue where cancel attachment was submitting form
	Fixed issue where form was validating at incorrect times
	
	20171127 by Carmen Ng
	Added toggleCheckValidity
	Fixed upload button needing two clicks
	Added revalidate form after attachment upload/delete
	
	20171127 by Carmen Ng
	Fixed layout for attachments
	Added support for delete attachments
	Removed alert in Firefox for lov search ajax abort
	
    20171120 by Carmen Ng
    Fixed attachment description being null
    Removed alerts when lov search modal was closed
    Removed abort when modals close
    
	20171117 by Carmen Ng
    Added onblur check for all LOV search fields		 
	Clear date inputs when date type is not selected
	
	20171116 by Carmen Ng
	Fixed attachment description being null
	Date type not selected disables fate inputs
	Press enter key when in date select, calls search functions
	
    20171110 by Carmen Ng
    Added date limit of 1900 for date fields
    
    20171108 by Carmen Ng
    Removed populateAssetInfo
    
    20171026 by Carmen Ng
    Made changes to resetFileInput function to accommodate for IE and Chrome

    20171025 by Carmen Ng
    Moved functions common to work order from work requests only javascript file

    20171024 by Carmen Ng
    Added all inital functions for upload/download attachments

    20171020 by Carmen Ng
    Added dropdown lists for work order lov pop up search (maintenance contract)

    20170829 by Carmen Ng
    Initial version
   -------------------------------------- -*/
$(document).ready(function(){
	
	window.location.hash="no-back-button";
	window.location.hash="Again-No-back-button";//again because google chrome don't insert first hash into history
	window.onhashchange=function(){window.location.hash="no-back-button";}
	
	//attachment buttons functions
    $("#addAttachmentBTN").click(function(){
        $( "#addAttachmentBTN" ).hide();
        $( "#attachmentFields" ).show();
        $("#attachmentUpload").val("");
        $("#attachmentTitle").val("");
        $("#attachmentDescription").val("");
        $('#uploadBTN').removeClass("enabled");
        $('#uploadBTN').attr("disabled", true);
        $('#attachmentAlert').hide();
    });
    
    $("#cancelAttachmentBTN").click(function(e){
    	e.preventDefault();
        $( "#addAttachmentBTN" ).show();
        $( "#attachmentFields" ).hide();
        $('#frameWrapper').hide();
    });
            
    //On blur checks for LOV searches
    //Common
	$("#maintenanceVendor.lovFieldSearchBtn, #requestedFor.lovFieldSearchBtn, .checkValidity.lovFieldSearchBtn").on("mousedown", function(e){
	    $(this).data("mouseDown", true);
	});

	$("#maintenanceVendor.lovFieldSearchBtn, #requestedFor.lovFieldSearchBtn, .checkValidity.lovFieldSearchBtn").on("mouseup", function(e){
	    $(this).data("mouseDown", false);
	});
    	
    //LOV Pop Up Functions
    // Objects with necessary information for the LOV textfields
    // FOR GENERIC: Fill in object fields, add object to lovFieldSearchBtn function and columns to searchLovResults table. *refer to LovSearch for query number
    var maintenanceVendorObj = {title: "Maintenance Body", dropdownOptions: ["Vendor Name", "Vendor Number"], data: ["vendor_number", "vendor_name"], query: 0};
    var maintenanceVendorCreateObj = {title: "Maintenance Body", dropdownOptions: ["Vendor Name", "Vendor Number"], data: ["vendor_number", "vendor_name"], query: 9};
    var assetLocationObj = {title: "Location Code", dropdownOptions: "Location Code", data: ["locationcode", "description1", "address"], query: 1};
    var assetOwnerObj = {title: "Asset Owner",  dropdownOptions: "Asset Owner", data: ["assetowner", "description2"], query: 2};
    var owningDeptObj = {title: "Owning Department", dropdownOptions: ["Department Code", "Description"], data: ["department_code", "description3"], query: 3};
    var requestedForObj = {title: "User List", dropdownOptions: ["Employee Name", "User Name"], data: ["user_name", "full_name"], query: 4};
    var requestedForFilteredObj = {title: "User List", dropdownOptions: ["Employee Name", "User Name"], data: ["user_name", "full_name"], query: 7};
    var assetNumberObj = {title: "Asset Number", dropdownOptions: "Asset Number", data: ["assetNumber", "assetDescription"], query: 5};
    var assetNumberFilteredObj = {title: "Asset Number", dropdownOptions: "Asset Number", data: ["assetNumber", "assetDescription"], query: 6};
    var maintenanceContractObj = {title: "Maintenance Contract Number", dropdownOptions: "Maintenance Contract Number", data: ["contract_num", "vendor_name"], query: 8};
    
    var dropdownHTML = "";
    var $resultsTable = $('#searchLovResults');
    var queryNumber = -1;
    var numberOfParameters = -1;
    var inputField = "";
    var inputRow = "";


    // Search button attached to searchable LOVs function
    $('.lovFieldSearchBtn').click(function () {
        $resultsTable.bootstrapTable('destroy');
        $resultsTable.bootstrapTable();
        
        var sPath=window.location.pathname;
        var sPage = sPath.substring(sPath.lastIndexOf('/') + 1);
        
       //Assign the right object accoridng to the name
        var object;
        if(this.id === "maintenanceVendor"){
        	if (sPage === "initCreateWorkOrder"){
        		object = maintenanceVendorCreateObj;
        	}else{
            	object = maintenanceVendorObj;
        	}
        }
        else if(this.id === "assetLocation"){
            object = assetLocationObj;
        }
        else if(this.id === "assetOwner"){
            object = assetOwnerObj;
        }
        else if(this.id === "owningDept"){
            object = owningDeptObj;
        }
        else if(this.id === "requestedFor"){
            object = requestedForObj;
        }
        else if(this.id === "assetNumber"){
        	object = assetNumberObj;
        }        
        else if(this.id === "assetNumberFiltered"){
           object = assetNumberFilteredObj;
        }
        else if(this.id === "requestedForFiltered"){
            object = requestedForFilteredObj;
        }
        else if(this.id === "maintenanceContract"){
        	object = maintenanceContractObj;
        }
        
        //Generate dropdown
        dropdownHTML = "";
        if($.isArray(object.dropdownOptions)){
            numberOfParameters = object.dropdownOptions.length;
            $.each(object.dropdownOptions, function( index, value ){
                index++;
                dropdownHTML += "<option value='" + index + "'>" + value + "</option>";
            });
        }
        else {
            numberOfParameters = 1;
            dropdownHTML = "<option value='1'>" + object.dropdownOptions + "</option>";
        }
        $('#lovSearchType').html(dropdownHTML).selectpicker('refresh');
        
        //Generate Columns
        $resultsTable.bootstrapTable('hideAllColumns');
        $.each(object.data, function( index, value ){
            $resultsTable.bootstrapTable('showColumn', (value));
        });

        //Add information to pop up
        $('#lovSearchTitle').text(object.title);
        $('#lovSearchValue').val($("#" + this.name).val());
        $('#lovSearch').modal('toggle'); 
        
        //Assign variable needed to perform search
        queryNumber = object.query;
        inputField = "#" + $(this).attr('name');
        inputRow = object.data[0];
                        
        //Search if value is entered
        var searchValue = replaceAll($('#lovSearchValue').val().trim(), "%", "");
        if(searchValue !== ""){
            searchLOV(queryNumber, numberOfParameters);
        }
    });

    // Search button in LOV pop up
    $('#lovSearchBtn').click(function () {
    	var searchValue = replaceAll($('#lovSearchValue').val().trim(), "%", "");
        if(searchValue !== ""){
    		 searchLOV(queryNumber, numberOfParameters);
    		 $('#noValueAlert').hide();
    	}
        else {
        	$('#noValueAlert').show();
        }
    });
    
    $('#lovSearchValue').keyup(function(e){
        if(e.keyCode === 13){
        	var searchValue = replaceAll($('#lovSearchValue').val().trim(), "%", "");
        	if(searchValue !== ""){
        		searchLOV(queryNumber, numberOfParameters);
                $('#noValueAlert').hide();
        	}
        	else {
            	$('#noValueAlert').show();
            }
        }
    });

    //Remove alerts in modal on modal close
    $("#lovSearch").on("hide.bs.modal", function () {
        $('#multipleAlert').hide();
        $('#noValueAlert').hide();
    });
    
    //Click row from LOV results, passes value back to textfield
    $('#searchLovResults').on('click-row.bs.table', function (e, row, field, $element) {
    	$('#multipleAlert').hide();
        $('#lovSearchValue').val("");
        $('#lovSearch').modal('toggle'); 
        if(inputRow === "user_name"){
            var displayName = row["full_name"];
            if(displayName == null || displayName == ""){
                displayName = row[inputRow];
            }
            $(inputField).val(displayName);
            $('#hiddenUser').val(row[inputRow]);
            if(typeof(userValidated) !== 'undefined'){
            	userValidated = true;
            }
        }
        else if(inputRow === "vendor_number"){
            $(inputField).val(row["vendor_name"]);
            $('#hiddenMBody').val(row[inputRow]).trigger("change");
        }
        else{
            $(inputField).val(row[inputRow]);
            if(inputRow === "assetNumber"){
				if(typeof getAssetInfo === "function"){
            		$('#assetNumber').trigger("blur");
            	}
            }
        }
    });
    
    $('.sidebarLink').click(function() {
    	setCookie('searchWRValues', "", 3600000);
    	setCookie('searchWOValues', "", 3600000);
    });
        
    //Abort all running ajax calls
    $.xhrPool = [];
    $.xhrPool.abortAll = function() {
        $(this).each(function(i, jqXHR) {   //  cycle through list of recorded connection
            jqXHR.abort();  //  aborts connection
            $.xhrPool.splice(i, 1); //  removes from list by index
        });
    }
    $.ajaxSetup({
        beforeSend: function(jqXHR) { $.xhrPool.push(jqXHR); }, //  annd connection to list
        complete: function(jqXHR) {
            var i = $.xhrPool.indexOf(jqXHR);   //  get index for current connection completed
            if (i > -1) $.xhrPool.splice(i, 1); //  removes from list by index
        }
    });
    
    $(window).on("unload", function(e) {
    	$.xhrPool.abortAll();
	});
    
	}).ajaxError(function(event,xhr,options,exc){
	//alert("System error");
	}).ajaxSend(function(event,xhr,options,exc){
		
 	}).ajaxComplete(function(event,xhr,options,exc){
	var status = xhr.getResponseHeader("sessionstatus");	
	if ( status == "timeout") {
	window.location.href = xhr.getResponseHeader("redirectUrl");
	}
    
 });
  
 //JQuery get URL parameters
 function GetURLParameter(sParam){
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    
    for (var i = 0; i < sURLVariables.length; i++){
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] === sParam){
            return sParameterName[1];
        }
    }
 }
 
function loadCommon(icon, title, subtitle){
    //Load the title and subtitle of each page
    $('#content').prepend(
    "<div class='container-fluid' id='pageTitle'>\
        <h4 style='margin-top: 10px; margin-bottom: 15px;'>\
            <span class='glyphicon " + icon + "'></span> " + title + "\
            <small style='color:#00051' id='pageSubtitle'><i>" + subtitle + "</i></small>\
        </h4>\
    </div>");
    
    //Initialise datepickers
    $('.datepicker').datetimepicker({
        format: 'DD/MM/YYYY',
        minDate: '1900-01-01'
    });
    $('.dateTimePicker').datetimepicker({
        format: 'DD/MM/YYYY HH:mm',
        minDate: '1900-01-01 00:01',
        useCurrent: false
    }).on("dp.change", function (e) {
    	var d = new Date(e.date);
    	var od = new Date(e.oldDate);
    	d.setHours(0,0,0,0);
    	od.setHours(0,0,0,0);
    	
    	// When date different, hide directly.
    	// If same date, maybe updating time, keep it
    	if(d.toString() !== od.toString()) {
    		$(this).datetimepicker('hide');
    	}
    });
    $('.noPastDate').datetimepicker({
        format: 'DD/MM/YYYY HH:mm',
        minDate: moment(),
        useCurrent: false
    }).on("dp.change", function (e) {
    	var d = new Date(e.date);
    	var od = new Date(e.oldDate);
    	d.setHours(0,0,0,0);
    	od.setHours(0,0,0,0);
    	
    	// When date different, hide directly.
    	// If same date, maybe updating time, keep it
    	if(d.toString() !== od.toString()) {
    		$(this).datetimepicker('hide');
    	}
    });
    $('.noFutureDate').datetimepicker({
        format: 'DD/MM/YYYY HH:mm',
        maxDate: 'now',
        minDate: '1900-01-01 00:01',
        useCurrent: false
    }).on("dp.change", function (e) {
    	var d = new Date(e.date);
    	var od = new Date(e.oldDate);
    	d.setHours(0,0,0,0);
    	od.setHours(0,0,0,0);
    	
    	// When date different, hide directly.
    	// If same date, maybe updating time, keep it
    	if(d.toString() !== od.toString()) {
    		$(this).datetimepicker('hide');
    	}
    });
    $('.dateFrom').datetimepicker({
        keyBinds: {
            enter: function(){
			if(typeof submitSearch === "function"){
            		submitSearch();
            	}
            	else if (typeof applyFilters === "function"){
            		applyFilters();
            	}
            }
        },
        format: 'DD/MM/YYYY',
        useCurrent: false,
        minDate: '1900-01-01'
    });
    $('.dateTo').datetimepicker({
        keyBinds: {
            enter: function(){
			  	if(typeof submitSearch === "function"){
            		submitSearch();
            	}
            	else if (typeof applyFilters === "function"){
            		applyFilters();
            	}
            }
        },
        format: 'DD/MM/YYYY',
        useCurrent: false,
        minDate: '1900-01-01'
    });
}

// Run search and call LovSearch.java function for LOV pop up
function searchLOV(query, params){
	$('#lovSearchBtn').val("Searching..");
	$('#lovSearchBtn').attr("disabled", true);
	var searchlov = {}
	searchlov["query"] = query;
	searchlov["parameters"] = params;
	searchlov["type"] = $('#lovSearchType').find("option:selected").val();
	searchlov["value"] = $('#lovSearchValue').val().trim();
	
//	alert(JSON.stringify(searchlov));
    $.ajax({
    	type: 'POST',
    	contentType: 'application/json',
    	data : JSON.stringify(searchlov),   
        url : './LovSearch',                        
        dataType:'json',
        success : function(data1) {
        	if($('#lovSearch').is(':visible')){
        		$('#searchLovResults').bootstrapTable('load', data1);   
                $('#lovSearchBtn').val("Search");
                $('#lovSearchBtn').attr("disabled", false);
        	}
        	else {
        		return;
        	}
        },

        error : function(xhr, ajaxOptions, thrownError) {
        	$('#lovSearchBtn').val("Search");
            $('#lovSearchBtn').attr("disabled", false);
            if (xhr.statusText =='abort' || thrownError == "") {
                return;
            }
        }

    });
	
/*    $('#searchLovResults').bootstrapTable('refreshOptions', {
        search: true,
        url: 'LovSearch',
        queryParams: {
            query: query,
            parameters: params,
            type: $('#lovSearchType').find("option:selected").val(),
            value: $('#lovSearchValue').val()
        }
    });*/
}

//functions used for attachment upload
function resetFileInput(){
	var iframeContentLength = $("#frameWrapper").find("iframe").contents().find("body").contents().length;
	var iframeContent = $("#frameWrapper").find("iframe").contents().find("body").find("pre").html();
	//IE vs Chrome issue check (Chrome has pre tag, IE does not)
	if(iframeContent == null){
		iframeContent = $("#frameWrapper").find("iframe").contents().find("body").html();
	}
	
	if(iframeContentLength > 0) {
    	if(iframeContent.substring(0, 5) == "ERROR"){
    		$('#attachmentAlert').show();
    		$('#attachmentAlert').removeClass("alert-success");
    		$('#attachmentAlert').addClass("alert-danger");
    		$('#attachmentAlert').html("<a href='#' class='close' data-hide='alert' aria-label='close'></a><strong><span class='glyphicon glyphicon-ok-sign'></span> Error!</strong> Attachment uploaded failed.");
            $( "#addAttachmentBTN" ).show();
            $( "#attachmentFields" ).hide();
    	} else {
    		if($('#newDocumentIds').val() == ""){
        		$('#newDocumentIds').val(iframeContent);
        	}
        	else{
        		$('#newDocumentIds').val($('#newDocumentIds').val() + "," + iframeContent);
        	}
        	recordAttachment($('#attachmentTitle').val(), $('#attachmentDescription').val(), $('#attachmentUpload').val().replace(/^.*[\\\/]/, ''), iframeContent);
        	$('#frameWrapper').show();
    		$('#attachmentAlert').show();
    		$('#attachmentAlert').addClass("alert-success");
    		$('#attachmentAlert').removeClass("alert-danger");
    		$('#attachmentAlert').html("<a href='#' class='close' data-hide='alert' aria-label='close'></a><strong><span class='glyphicon glyphicon-ok-sign'></span> Success!</strong> Attachment uploaded successfully.");
			$('#uploadBTN').val("Upload");
    		$('#uploadBTN').prop("disabled", false);
    		$('#uploadBTN').addClass("enabled");
    		$('#uploadBTN').removeClass("disabled");
            $( "#addAttachmentBTN" ).show();
            $( "#attachmentFields" ).hide();
			if($('#wrForm').length) {
        		$('#wrForm').validator('validate');
        	}
        	else if($('#woForm').length){
        		$('#woForm').validator('validate');
        	}
    	}
    }
	else{
		setTimeout(resetFileInput, 300);
	}
}

//disables upload button till uploaded
function disableUploadBTN(){
	$('#uploadBTN').val("Uploading...");
	$('#uploadBTN').prop("disabled", true);
	$('#uploadBTN').removeClass("enabled");
	$('#uploadBTN').addClass("disabled");
}
function recordAttachment(title, desc, filename, docid){
	if(desc == "null"){
		desc = "";
	}
	$('#addedAttachmentsWrapper').show();
	$('#addedAttachmentsWrapper').append(
	"<div class='row'>" +
		"<div class='col-xs-3 attachmentInfo'><span class='" + docid + "' id='" + docid + "Title'><br>" + title + "</span></div>" +
		"<div class='col-xs-4 attachmentInfo'><span class='" + docid + "'><br>" + desc + "</span></div>" +
		"<div class='col-xs-3 attachmentInfo'><span class='" + docid + "'><br><a class='text-link' href='#' onclick=downloadAttachment(" + docid + ") id='" + docid + "Filename'>" + filename + "</a></span></div>" +
		'<div class="col-xs-2 attachmentInfo"><span><br><b><a class="text-danger deleteAttachment ' + docid + '" href="#" onclick=removeAttachment(' + docid + ')>Delete</a></b></span></div>' +
	"</div>"
	);
}

//function to download attachment, attached to the download button generated in recordAttachment function
function downloadAttachment(docId){
	$('#docId').val(docId);
	$('#downloadForm').submit();
}

//function to remove attachment from UI and add to delete list. Triggered by delete attachment button
function removeAttachment(docid){
	$('#attachmentAlert').hide();
	$('.' + docid).hide();
	$('#' + docid + "Title").html("<br>'" + $('#' + docid + "Filename").text() + "' has been removed.");	$('#' + docid + "Title").show();
	if($('#delDocumentIds').val() == ""){
		$('#delDocumentIds').val(docid);
	}
	else{
		$('#delDocumentIds').val($('#delDocumentIds').val() + "," + docid);
	}
	if($('#wrForm').length) {
		$('#wrForm').validator('validate');
	}
	else if($('#woForm').length){
		$('#woForm').validator('validate');
	}						  
	 $("html, body").animate({ scrollTop: $(document).height() }, "slow");
}

//Used to clear attachment info on create work request/order
function clearAttachmenInfo(){
	$('#addedAttachmentsWrapper').html(
		"<div class='row'>" +
			"<div class='col-xs-3'><label class='control-label'>Title</label></div>" +
			"<div class='col-xs-4'><label class='control-label'>Description</label></div>" +
			"<div class='col-xs-3'><label class='control-label'>File Name</label></div>" +
			"<div class='col-xs-2' style='padding-top: 7px'></div>" +
		"</div>"
	);
	$('#addedAttachmentsWrapper').hide();
	$('#uploadBTN').val("Upload");
}
//functions to set and get cookies
function setCookie(key, value, expiration) {
    var expires = new Date();
    expires.setTime(expires.getTime() + expiration);
    document.cookie = key + '=' + value + ';expires=' + expires.toUTCString();
}

function getCookie(key) {
    var keyValue = document.cookie.match('(^|;) ?' + key + '=([^;]*)(;|$)');
    return keyValue ? keyValue[2] : null;
}

function delCookie(name) {
    document.cookie = name + '=;expires=Thu, 01 Jan 1970 00:00:01 GMT;';
};

//Replace all function
function replaceAll(str, find, replace) {
    return str.replace(new RegExp(find, 'g'), replace);
}

//Show hide loading screen and message
function loadingScreen(show, message){
	if(show){
		$('#loading').show();
		$('#loadingMessage').html(message + "...");
	}
	else{
		$('#loading').hide();
		$('#loadingMessage').html(message);
	}
}

//alertId = id of the alert box on the page| type = error, note, success| message = alert message
function alertMessage(alertId, input, type){
	var message = input;
	
	if(input in ha_messages){
		message = ha_messages[input];
		if(input.startsWith('HA_ERROR')){
			type = "error";
		}
		else if(input.startsWith('HA_SUCCESS')){
			type = "success";
		}
		else{
			type = "note";
		}
	}
	
	if(type == "error"){
		$('#' + alertId).removeClass("alert-success");
		$('#' + alertId).removeClass("alert-warning");
	    $('#' + alertId).addClass("alert-danger");
	    $('#' + alertId).html("<strong><span class='glyphicon glyphicon-exclamation-sign'></span> Error!</strong> " + message);
	}
	else if(type == "success"){
		$('#' + alertId).addClass("alert-success");
		$('#' + alertId).removeClass("alert-warning");
	    $('#' + alertId).removeClass("alert-danger");
	    $('#' + alertId).html("<strong><span class='glyphicon glyphicon-ok-sign'></span> Success!</strong> " + message);
	}
	else {
		$('#' + alertId).removeClass("alert-success");
		$('#' + alertId).addClass("alert-warning");
	    $('#' + alertId).removeClass("alert-danger");
	    $('#' + alertId).html("<strong><span class='glyphicon glyphicon-exclamation-sign'></span> Note!</strong> " + message);
	}
	$('#' + alertId).show();
    $('html,body').animate({ scrollTop: 0 }, 'slow');
}