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
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
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
		
	
</head>
<body>
	<jsp:include page="sidebarHeader.jsp" />
        
        	<div id="content">
           <!-- Loading div for retrieving data waiting time -->
            <div class="container-fluid col-md-12">
	            <div id="loading" style="display:none">
	            	<img src="<spring:url value="/resources/images/spinner.gif" />" alt="Loading" height="100" width="100">
	            	<h5 id="loadingMessage" class="text-primary"></h5>
	            </div>
            </div>
            
            <!-- Div to show successful creation and update -->
            <div class="container-fluid col-md-12">
                <div class="alert alert-danger lovSearchAlert" role="alert" id="postResultsBox" style="display: none;"></div>
            </div>
  			 
			 
			 <form:form class="form-horizontal" id="woForm" role="form" method="POST" modelAttribute="workOrder" data-toggle="validator" data-disable="false" data-focus="false">	            
           
            <!-- First Row: View History, Add New Change, Create Employment buttons-->
			<div class="container-fluid col-md-12">
				<div class="row">
					<div class="col-sm-8">
						<button type="button" class="btn" style="border: 2px solid #555555">View History</button>
					</div>
					<div class="col-sm-2">
						<button type="button" class="btn" style="border: 2px solid #555555">Add New Change</button>
					</div>
					<div class="col-sm-2">
						<button type="button" class="btn" style="border: 2px solid #555555">Create Employment</button>
					</div>
				</div>
			</div>			
			
			 
            <!-- Second Row: Title, Gender, Employee Number-->
			<div class="container-fluid col-md-12">
  				<div class="row" style="border-color:red;border-width:thin;padding-top:3px;border-style:solid;padding-left:10px;padding-right:10px;">
					<div class="form-group col-md-4">
						<label class='control-label col-md-4' for="title">Title</label>                 
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
  					
  					<div class="col-md-3 form-inline">
	  					<label for="endDate">End Date</label>
	  					<div class="form-group">
	  						<input class="form-control" id="endDate" type="text" disabled>
	  					</div>
  					</div>
  				</div>
  				
  				<div class="container-fluid col-md-12">
  					<div class="col-md-6 form-inline">
	  					<label for="hkidName">HKID Name</label>
	  					<div class="form-group">
	  						<input class="form-control" id="hkidName" type="text">
	  					</div>
  					</div>
  					
  					<div class="col-md-3 form-inline">
	  					<label for="chineseName">Chinese Name</label>
	  					<div class="form-group">
	  						<input class="form-control" id="chineseName" type="text">
	  					</div>
  					</div>
  					
  					<div class="col-md-3 form-inline">
	  					<label for="peopleType">People Type</label>
	  					<div class="form-group">
	  						<button id="peopleType" class="btn dropdown-toggle bs-placeholder btn-default" 
  							role="button" aria-expanded="false" type="button" data-toggle="dropdown" data-id="title"><span class="filter-option pull-left">Employee</span>&nbsp;<span class="bs-caret"><span class="caret"></span></span></button>
	  					</div>
  					</div>
  				</div>
  				
  				<div class="container-fluid col-md-12">
  					<div class="col-md-6 form-inline">
	  					<label for="lastName">Last Name</label>
	  					<div class="form-group">
	  						<input class="form-control" id="lastName" type="text" disabled>
	  					</div>
  					</div>
  					
  					<div class="col-md-3 form-inline">
	  					<label for="firstName">First Name</label>
	  					<div class="form-group">
	  						<input class="form-control" id="firstName" type="text" disabled>
	  					</div>
  					</div>
  					
  					<div class="col-md-3 form-inline">
	  					<label for="latestHireDate">Latest Hire Date</label>
	  					<div class="form-group">
	  						<input class="form-control" id="latestHireDate" type="text">
	  					</div>
  					</div>
  				</div>
  			</div>
				<!--Tabs-->
				<div class="container-fluid col-md-12">
					<div>
						<ul class="nav nav-pills">
							<li class="nav-item"><a class="nav-link active" href="#personalDetailsDiv" data-toggle="pill">Personal Details</a></li>
							<li class="nav-item"><a class="nav-link" href="#contactInfoTab" data-toggle="pill">Contact Info</a></li>
							<li class="nav-item"><a class="nav-link" href="#qualificationTab" data-toggle="pill">Qualification</a></li>
							<li class="nav-item"><a class="nav-link" href="#otherPersonalInfoTab" data-toggle="pill">Other Personal Info</a></li>
							<li class="nav-item"><a class="nav-link" href="#contractsTab" data-toggle="pill">Contracts</a></li>
							<li class="nav-item"><a class="nav-link" href="#assignmentsTab" data-toggle="pill">Assignments</a></li>
							<li class="nav-item"><a class="nav-link" href="#salaryInfoTab" data-toggle="pill">Salary Info</a></li>
							<li class="nav-item"><a class="nav-link" href="#payMethodTab" data-toggle="pill">Pay Method</a></li>
							<li class="nav-item"><a class="nav-link" href="#otherAssignmentInfoTab" data-toggle="pill">Other Assignment Info</a></li>
						</ul>
						
						<div class="tab-content">					
							<div id="personalDetailsDiv" class="container tab-pane active"> 
								<div class="row">
									<div class="col-md-8">
									<h1>Personal Details</h1>
									</div>
								</div>
							</div>
							
							
							<div id="contactInfoTab" class="container tab-pane fade"> 
								<div class="row">
									<div class="col-md-12">
										<jsp:include page="contactInfo.jsp"></jsp:include>
									</div>
								</div>
							</div>
							
							<div id="qualificationTab" class="container tab-pane fade">
								<div class="row">
									<div class="col-md-8">
										<h1>Qualification</h1>
									</div>
								</div>
							</div>
							<div id="otherPersonalInfoTab" class="container tab-pane fade">  
								<div class="row">
									<div class="col-md-8">
										<h1>Other Personal Info</h1>
									</div>
								</div>
							</div>
							
							<div id="contractsTab" class="container tab-pane fade">  
								<div class="row">
									<div class="col-md-8">
										<h1>Contracts</h1>
									</div>
								</div>
							</div>
							
							<div id="assignmentsTab" class="container tab-pane fade">
								<div class="row">
									<div class="col-md-8">
										<h1>Assignments</h1>
									</div>
								</div>
							</div>
							<div id="salaryInfoTab" class="container tab-pane fade">  
								<div class="row">
									<div class="col-md-8">
										<h1>Salary Info</h1>
									</div>
								</div>
							</div>
							
							<div id="payMethodTab" class="container tab-pane fade"> 
								<div class="row">
									<div class="col-md-8">
										<h1>Pay Method</h1>
									</div>
								</div>
							</div>
							<div id="otherAssignmentInfoTab" class="container tab-pane fade">  
								<div class="row">
									<div class="col-md-8">
										<h1>Other Assignment Info</h1>
									</div>
								</div>
							</div>
														
						</div>
						
					</div>
				</div>
  				
  				<div class="container-fluid col-md-12">
				    <div class="col-sm-2">
				    	<button type="button" class="btn-primary">SAVE & CONTINUE</button>
				    </div>
  				</div>
  				
  			</div>
        </form:form>
</body>
</html>