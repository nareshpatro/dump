<%-- 
    Document   : newHire
    Created on : Apr 18, 2018 12:23:28 PM
    Author     : Carlo
    Last Update: Apr 18, 2018 12:23:28 PM
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
	<title>New Hire</title>
        <script src="<spring:url value="/resources/js/moment.js?v=${initParam.buildTimeStamp}" /> " type="text/javascript"></script>
        <script src="<spring:url value="/resources/js/jquery.js?v=${initParam.buildTimeStamp}" />"></script>        
        <script src="<spring:url value="/resources/js/datatables/datatables.min.js?v=${initParam.buildTimeStamp}" />" type="text/javascript"></script>
        <script src="<spring:url value="/resources/js/datatables/DataTables-1.10.15/js/dataTables.bootstrap.js?v=${initParam.buildTimeStamp}" />" type="text/javascript"></script>
        <script src="<spring:url value="/resources/js/datatables/colResize.js?v=${initParam.buildTimeStamp}" />" type="text/javascript"></script>
        <script src="<spring:url value="/resources/js/ha/pages/messageUI.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/ha/pages/commonFunctions.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/ha/pages/searchRequests.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/datatables/tableProcessing.js?v=${initParam.buildTimeStamp}" />"></script>
        <link rel="stylesheet" href="<spring:url value="/resources/css/bootstrap.css?v=${initParam.buildTimeStamp}" />">
        <link rel="stylesheet" href="<spring:url value="/resources/css/custom.css?v=${initParam.buildTimeStamp}" />">
        <link rel="stylesheet" href="<spring:url value="/resources/css/bootstrapTable.css?v=${initParam.buildTimeStamp}" />">
        <link rel="stylesheet" href="<spring:url value="/resources/js/datatables/Select-1.2.2/css/select.bootstrap.css?v=${initParam.buildTimeStamp}" />">
        <link href="<spring:url value="/resources/js/datatables/DataTables-1.10.15/css/dataTables.bootstrap.css?v=${initParam.buildTimeStamp}" />" rel="stylesheet" type="text/css"/>
        
        <script>
        <!-- Modal for leaving page-->
        $(document).ready(function() {
            formmodified=0;
            $('form *').change(function(){
                formmodified=1;
            });
            window.onbeforeunload = confirmExit;
            function confirmExit() {
                if (formmodified == 1) {
                    return "New information not saved. Do you wish to leave the page?";
                }
            }
            $("input[name='saveAndContinueBtn']").click(function() {
                formmodified = 0;
            });
        });
        
        /* Save and Continue */
        $(document).ready(function(){
            $("#saveAndContinueBtn").click(function(){
                alert("The Save and Continue button was clicked.");
                var hkidName = "HKID NAME: " + $("#hkidName").val();
                alert(hkidName);
                
                var formData = {};
                formData["title"] = $("#title").val();
                formData["gender"] = $("#gender").val();
                formData["empnum"] = $("#empnum").val();
                formData["hkidName"] = $("#hkidName").val();
                formData["chineseName"] = $("#chineseName").val();
                formData["peopleType"] = $("#peopleType").val();
                formData["lastName"] = $("#lastName").val();
                formData["firstName"] = $("#firstName").val();
                formData["latestHireDate"] = $("#latestHireDate").val();
    			
                $.ajax({
    				type : "POST",
    				contentType : "application/json",
    				url : "saveAndContinue",
    				data : JSON.stringify(formData),
    				dataType : 'json',				
    				success : function(data) {
    					alert("Success!!");
    					
    					var output = JSON.stringify(data);
    					var obj = JSON.parse(output);
    					alert("NEW HKID NAME: " + obj.hkidName);

    					$("#viewHistoryBtn").removeAttr("disabled");
    					$("#addNewChangeBtn").removeAttr("disabled");
    					$("#createEmploymentBtn").removeAttr("disabled");
    					
    					$('.nav li.disabled').find('a').attr("data-toggle","pill");
     					$('.nav li.disabled').removeClass('disabled');
    				},
    				error: function () {
                        alert("An error has occured!!!");
                    }
    			});
            });
        });
        
        </script>
</head>
<body>
	<jsp:include page="sidebarHeader.jsp" />
        
        <div id="content">            
        	<%-- <form:form class="form-horizontal" id="newHireForm" role="form" method="POST" modelAttribute="newHire" data-toggle="validator" data-disable="false" data-focus="false">	 --%>
        	<form:form class="form-horizontal" id="newHireForm">
  				<!-- First Row: View History, Add New Change, Create Employment buttons-->
				<div class="container-fluid" style="padding: 10px 25px 12px 25px;">
					<div class="row">
						<div class="col-sm-8">
							<button id="viewHistoryBtn" type="button" class="btn" style="border: 2px solid #555555" disabled>View History</button>
						</div>
						<div class="col-sm-2">
							<button id="addNewChangeBtn" type="button" class="btn" style="border: 2px solid #555555" disabled>Add New Change</button>
						</div>
						<div class="col-sm-2">
							<button id="createEmploymentBtn" type="button" class="btn" style="border: 2px solid #555555" disabled>Create Employment</button>
						</div>
					</div>
				</div>	
  				
  				<div class="row" style="padding-top:5px;padding-left:50px;padding-right:150px;">
					<div class="row" style="padding-top: 8px;margin-bottom: 10px;border: thin solid red">
						<div class="form-group col-md-4 form-inline">
							<label class='control-label col-md-2' for="title">Title</label>                 
	  						<button title="-- Select --" class="btn dropdown-toggle bs-placeholder btn-default" 
	  						role="button" aria-expanded="false" type="button" data-toggle="dropdown" data-id="title"><span class="filter-option pull-left">-- Select --</span>&nbsp;<span class="bs-caret"><span class="caret"></span></span></button>
						</div>
					
						<div class="form-group col-md-4">
							<label class="control-label col-md-3" for="title">Gender</label>	                      	
							<label class="radio-inline">
								<input type="radio" name="gender" style="margin-top:3px;">M
							</label>
							<label class="radio-inline">
								<input type="radio" name="gender" style="margin-top:3px;">F
							</label>
						</div>
						 
						 <div class="form-group col-md-4 form-inline">
							<label class='control-label' for="empnum">Employee Number</label>
							<input class="form-control" id="empnum" type="text" disabled>
						 </div>	
					</div>	 
  				</div>
  				
  				<div class="container-fluid col-md-12">
  					<div class="col-md-3">
  						<label>Record Effective From</label>
  					</div>
  					
  					<div class="col-md-3 form-inline">
	  					<label for="startDate">Start Date</label>
	  					<div class="form-group">
	  						<input class="form-control" id="startDate" type="text">
	  					</div>
  					</div>
  					
  					<div class="col-md-3 form-inline" style="text-align: right;">
	  					<label for="endDate">End Date</label>
	  					<div class="form-group">
	  						<input class="form-control" id="endDate" type="text" disabled>
	  					</div>
  					</div>
  					<input type="hidden" name="changeDate" id="changeDate">
  				</div>
  				
  				<div class="container-fluid col-md-12" style="margin-top: 15px; text-align: right;">
  					<div class="col-md-3 form-inline">
	  					<div class="form-group">
	  						<label for="hkidName">HKID Name</label>
	  						<input class="form-control" id="hkidName" type="text">
	  					</div>
  					</div>
  					
  					<div class="col-md-4 form-inline">
	  					<div class="form-group">
	  						<label for="chineseName">Chinese Name</label>
	  						<input class="form-control" id="chineseName" type="text">
	  					</div>
  					</div>
  					
  					<div class="col-md-4 form-inline">
	  					<div class="form-group">
	  						<label for="peopleType">People Type</label>
		  						<button id="peopleType" style="width: 150px;" class="btn dropdown-toggle bs-placeholder btn-default" 
	  								role="button" aria-expanded="false" type="button" data-toggle="dropdown" data-id="title">
	  								<span class="filter-option pull-left">Employee</span>&nbsp;
		  								<span class="bs-caret pull-right">
		  									<span class="caret"></span>
		  								</span>
	  							</button>
	  					</div>
  					</div>
  				</div>
  				
  				<div class="container-fluid col-md-12" style="margin-top: 15px; margin-bottom: 20px;text-align: right;">
  					<div class="col-md-3 form-inline">
	  					<div class="form-group">
	  						<label for="lastName">Last Name</label>
	  						<input class="form-control" id="lastName" type="text" disabled>
	  					</div>
  					</div>
  					
  					<div class="col-md-4 form-inline">
	  					<div class="form-group">
	  						<label for="firstName">First Name</label>
	  						<input class="form-control" id="firstName" type="text" disabled>
	  					</div>
  					</div>
  					
  					<div class="col-md-4 form-inline">
	  					<div class="form-group">
	  						<label for="latestHireDate">Latest Hire Date</label>
	  						<input class="form-control" id="latestHireDate" type="text">
	  					</div>
  					</div>
  				</div>
  				
  				<!--Tabs-->
  				<div class="container-fluid">
  					<ul class="nav nav-pills">
					    <li class="active"><a data-toggle="pill" href="#personalDetailsTab">Personal Details</a></li>
					    <li class="disabled"><a href="#contactInfoTab">Contact Info</a></li>
					    <li class="disabled"><a href="#qualificationTab">Qualification</a></li>
					    <li class="disabled"><a href="#othPersonalInfoTab">Other Personal Info</a></li>
					    <li class="disabled"><a href="#contractsTab">Contracts</a></li>
					    <li class="disabled"><a href="#assignmentsTab">Assignments</a></li>
					    <li class="disabled"><a href="#salaryInfoTab">Salary Info</a></li>
					    <li class="disabled"><a href="#payMethodTab">Pay Method</a></li>
					    <li class="disabled"><a href="#othAssignmentInfoTab">Other Assignment Info</a></li>		    
					</ul>
				
					<div class="tab-content">
					    <div id="personalDetailsTab" class="container tab-pane fade in active">
							<jsp:include page="personalDetails.jsp"/>
					    </div>
					    
					    <div id="contactInfoTab" class="tab-pane fade">
					      <h3>Contact Info</h3>
					      <p>Ongoing Development</p>
					    </div>
					    
					    <div id="qualificationTab" class="tab-pane fade">
					      <h3>Qualification</h3>
					      <p>Ongoing Development</p>
					    </div>
					    
					    <div id="othPersonalInfoTab" class="tab-pane fade">
					      <h3>Other Personal Info</h3>
					      <p>Ongoing Development</p>
					    </div>
					    
					    <div id="contractsTab" class="tab-pane fade">
						  <h3>Contracts</h3>
						  <p>Ongoing Development</p>
						</div>
					    
						<div id="assignmentsTab" class="tab-pane fade">
						  <h3>Assignments</h3>
						  <p>Ongoing Development</p>
						</div>
						
						<div id="salaryInfoTab" class="tab-pane fade">
						  <h3>Salary Info</h3>
						  <p>Ongoing Development</p>
						</div>
					
						<div id="payMethodTab" class="tab-pane fade">
						  <h3>Pay Method</h3>
						  <p>Ongoing Development</p>
						</div>
						
						<div id="othAssignmentInfoTab" class="tab-pane fade">
						  <h3>Other Assignment Info</h3>
						  <p>Ongoing Development</p>
						</div>
						
	  				</div> <!--  end tab content -->

  				</div> <!-- end tabs -->
  				
  				<div class="container-fluid col-md-12" style="margin-top: 15px">
				    <div class="col-sm-2 pull-right">
				    	<input id="saveAndContinueBtn" name="saveAndContinueBtn" type="button" class="btn-primary" value="SAVE & CONTINUE">
				    </div>
  				</div>  
  			</form:form>          
         </div>
</body>
</html>