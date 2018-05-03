/* --------------------------------------
    File Name searchPeople.js
    Author Carmen Ng (PCCW)
    Date April 19, ‎2018
    Description
    All functions used uniquely in the create work requests page.
    
    ---------- Modification History ----------

   -------------------------------------- -*/

	var mBody = "";
	var user = "";
	var toggleOff = true;
	
	var exportParameters;
	
	var exportObject;
	var tableObject;
	
$(document).ready(function(){
    loadCommon("glyphicon-search", "Search", "Search People");
    page = 'sidebarSearchWr';
    toggleOff = false;
    toggleCheckValidity(true);
    exportParameters = {};
    
    exportObject = {};
	tableObject = {};
	
    //Press enter to submit search form, runs same click search button checks
    $('#searchCriteria').keypress(function(e) {
        if(e.which == 13) {
        	$('#searchAlert').hide();
        	
        	toggleCheckValidity(false);
            submitSearch();
        }
    });
    
    
    $('#clearPplSearchBTN').click(function () {
        $('#effDate').val("");
        $('#hkid').val("");
        $('#empNumber').val("");
        $('#chnName').val("");

    });
	
	
});

function submitSearch(){
	
	//Check if there are any values entered
	if($('#effDate').val() == "" 
		&& $('#hkid').val() == "" 
		&& $('#empNumber').val()==""
		&& $('hkidNam').val()==""
		&& $('chnName').val()==""){
			alertMessage("searchAlert", "HA_ERROR_LEASTONECRITERIA");
			return;
	}
	
    $('#searchCriteria').collapse('hide');
    $('#searchAlert').hide();
	$('#pplSearchBtn').button('loading');
    $('#pplSearchBtn').attr("disabled", true);
    var table = $('#wrSearchResults').DataTable();
    table.processing( true );
    searchPeople();
    
    
}

function searchPeople(){
    var search = {}
    search["effDate"] = $("#effDate").val();
    search["hkid"] = $("#hkid").val().trim();
    search["empNumber"] = $("#empNumber").val();
    search["hkidName"] = $("#hkidName").val();
    search["chnName"] = $("#chnName").val();
    
    alert('search["empNumber"]:'+search["empNumber"]);
    
	exportParameters["effDate"] = $("#effDate").val();
	exportParameters["hkid"] = $("#hkid").val();
    exportParameters["empNumber"] = $("#empNumber").val();
    exportParameters["hkidName"] = $("#hkidName").val();
    exportParameters["chnName"] = $("#chnName").val();
        
    //Set cookies for if the user returns to search page from view work request,  expires in one hour
    setCookie('searchPplValues', JSON.stringify(search), 3600000);
    setCookie('lastSearch', "People", 3600000);
    
    var table = $('#pplSearchResults').DataTable();
        $.ajax({
            type: 'POST',
            contentType: 'application/json',
            data : JSON.stringify(search),   
            url : './processPeople',                        
            dataType:'json',
            success : function(data1) {            	
	        	//if(Object.keys(data1).length  == 1 && data1[0].description !== null){
            	if(Object.keys(data1).length  == 1 && data1[0].maintFax !== null){
	        		//alertMessage("searchAlert", "Total " + data1[0].description + " records returned and hit the limit to display and export. Please refine your searching criteria.", "error");
            		alertMessage("searchAlert", "Total " + data1[0].maintFax + " records returned and hit the limit to display and export. Please refine your searching criteria.", "error");
	        		$('#pplSearchBtn').attr("disabled", false);
		        	$('#pplSearchBtn').button('reset');
		        	table.clear().draw();
		        	table.button( 4 ).disable();
		        	table.processing( false );
		        	//toggleCheckValidity(true);	
		        	return;
	        	}
	        	
	        	exportObject = data1.concat();
	        	tableObject = data1.concat();
	        	
	        	exportLength = Object.keys(exportObject).length;
	        	tableLength = Object.keys(tableObject).length;
	        	
	        	if(tableLength > tableMax){
	        		tableObject.splice(tableMax, tableObject.length - tableMax);
	        		alertMessage("searchAlert", "Total " + tableLength + " records returned. Only max. " + tableMax + " records will be displayed in the result table. Please export the full result set via Excel export.", "note");
	        	}
	        		        		           
	        	table.clear().rows.add(tableObject).draw();
	        	$('#pplSearchBtn').attr("disabled", false);
	        	$('#pplSearchBtn').button('reset');
	        	table.processing( false );
	        	var rowCount = table.rows().count();
	        	table.button( 4 ).enable(rowCount > 0);
	        	toggleCheckValidity(true);	
            },

            error : function(xhr, ajaxOptions, thrownError) {
            	if (xhr.statusText =='abort' || thrownError == "") {
                    return;
                }
                
            }
        })
}

