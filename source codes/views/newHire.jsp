<%-- 
    Document   : newHire
    Created on : Apr 18, 2018 12:23:28 PM
    Author     : Carlo
    Last Update: Apr 18, 2018 12:23:28 PM
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>New Hire</title>
		<script src="<spring:url value="/resources/js/jquery.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/ha/pages/messageUI.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/ha/pages/commonFunctions.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/ha/pages/createOrders.js?v=${initParam.buildTimeStamp}" />"></script>
        <!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
  		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script> -->
        <link rel="stylesheet" href="<spring:url value="/resources/css/bootstrap.css?v=${initParam.buildTimeStamp}" />">
        <link rel="stylesheet" href="<spring:url value="/resources/css/custom.css?v=${initParam.buildTimeStamp}" />">
        <link rel="stylesheet" href="<spring:url value="/resources/css/bootstrapTable.css?v=${initParam.buildTimeStamp}" />">
</head>
<body>
	<jsp:include page="sidebarHeader.jsp" />
        
        	<div id="content"  style="height: 230%">
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
            
            	<div class="container-fluid col-md-12">
				    <div class="col-sm-8">
				    	<button type="button" class="btn">View History</button>
				    </div>
				    <div class="col-sm-2">
				    	<button type="button" class="btn">Add New Change</button>
				    </div>
				    <div class="col-sm-2">
				    	<button type="button" class="btn">Create Employment</button>
				    </div>
  				</div>
  				
  				<div class="container-fluid col-md-12">
  					<div class="col-md-4">
  						<label class='control-label' for="title">Title</label>
  						<button title="-- Select --" class="btn dropdown-toggle bs-placeholder btn-default" 
  						role="button" aria-expanded="false" type="button" data-toggle="dropdown" data-id="title"><span class="filter-option pull-left">-- Select --</span>&nbsp;<span class="bs-caret"><span class="caret"></span></span></button>
  					</div>
  					
  					<div class="col-md-4">
  						<label class='control-label' for="gender">Gender</label>
      				 	<label class="control-label radio-inline"><input type="radio" name="gender">M</label>
					 	<label class="control-label radio-inline"><input type="radio" name="gender">F</label>
  					</div>
  					 
  					 <div class="col-md-4 form-inline">
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
  				
  				<ul class="nav nav-pills">
				    <li class="active"><a data-toggle="pill" href="#personalDetailsDiv">Personal Details</a></li>
				    <li><a data-toggle="pill" href="#menu1">Menu 1</a></li>
				    <li><a data-toggle="pill" href="#menu2">Menu 2</a></li>
				    <li><a data-toggle="pill" href="#menu3">Menu 3</a></li>
				</ul>
				
				<div id="tab-content">
				    <div id="personalDetailsDiv" class="tab-pane fade in active">
						<div class="container-fluid col-md-12">
		  					<div class="col-md-3 form-inline">
			  					<label for="hkid">HKID</label>
			  					<div class="form-group">
			  						<input class="form-control" id="hkid" type="text">
			  					</div>
		  					</div>
		  					
		  					<div class="col-md-3 form-inline">
			  					<label for="dateOfBirth">Date Of Birth</label>
			  					<div class="form-group">
			  						<input class="form-control" id="dateOfBirth" type="text" >
			  					</div>
		  					</div>
		  					
		  					<div class="col-md-3 form-inline">
			  					<label for="age">Age</label>
			  					<div class="form-group">
			  						<input class="form-control" id="age" type="text" disabled>
			  					</div>
		  					</div>
		  					
		  					<div class="col-md-3 form-inline">
			  					<label for="maritalStatus">Marital Status</label>
			  					<div class="form-group">
			  						<button id="maritalStatus" title="-- Select --" class="btn dropdown-toggle bs-placeholder btn-default" 
  									role="button" aria-expanded="false" type="button" data-toggle="dropdown" data-id="title"><span class="filter-option pull-left">-- Select --</span>&nbsp;<span class="bs-caret"><span class="caret"></span></span></button>
			  					</div>
		  					</div>
		  				</div> 
		  				
		  				<div class="container-fluid col-md-12">
		  					<div class="col-md-6 form-inline">
			  					<label for="emailAddress">Email Address</label>
			  					<div class="form-group">
			  						<input class="form-control" id="emailAddress" type="text">
			  					</div>
		  					</div>
		  					
		  					<div class="col-md-6 form-inline">
			  					<label for="preferredName">Preferred Name</label>
			  					<div class="form-group">
			  						<input class="form-control" id="preferredName" type="text">
			  					</div>
		  					</div>
		  				</div>
		  				
		  				<div class="container-fluid col-md-12">
		  					<div class="col-md-3 form-inline">
			  					<label for="countryOfBirth">Country Of Birth</label>
			  					<div class="form-group">
			  						<button id="countryOfBirth" title="-- Select --" class="btn dropdown-toggle bs-placeholder btn-default" 
  									role="button" aria-expanded="false" type="button" data-toggle="dropdown" data-id="title"><span class="filter-option pull-left">-- Select --</span>&nbsp;<span class="bs-caret"><span class="caret"></span></span></button>
			  					</div>
		  					</div>
		  					
		  					<div class="col-md-3 form-inline">
			  					<label for="permanentHkidHolder">Permanent HKID Holder</label>
			  					<div class="form-group">
			  						<button id="permanentHkidHolder" title="-- Select --" class="btn dropdown-toggle bs-placeholder btn-default" 
  									role="button" aria-expanded="false" type="button" data-toggle="dropdown" data-id="title"><span class="filter-option pull-left">-- Select --</span>&nbsp;<span class="bs-caret"><span class="caret"></span></span></button>
			  					</div>
		  					</div>
		  					
		  					<div class="col-md-6 form-inline">
			  					<label for="permanentHkidCardRcvDate">Permanent HKID Card Received Date</label>
			  					<div class="form-group">
			  						<input class="form-control" id="permanentHkidCardRcvDate" type="text">
			  					</div>
		  					</div>
		  				</div>
		  				
		  				<div class="panel panel-primary">
		  					<div class="panel-heading">
		  						<h4 class="panel-title">
		  							<a class="accordion-toggle" id="workPermitDetailsHeader" aria-expanded="true"
		  							href="#workPermitDetailsArea" data-toggle="collapse" data-parent="#accordion">Work Permit Details</a>
		  						</h4>
		  					</div>
		  					
		  					<div class="panel-collapse collapse in" id="workPermitDetailsArea" aria-expanded="true">
                        		<div class="panel-body" style="padding: 8px 0px;">
                            		<div class="container-fluid">
		                                <div class="form-inline col-md-3">
		                                    <label class="control-label" for="passportNumber">Passport Number</label>
		                                    <input name="passportNumber" class="form-control col-md-3" id="passportNumber" type="text" value="">
		                                </div>
                                
		                                <div class="form-inline col-md-3">
		                                    <label class="control-label" for="countryOfIssue">Country Of Issue</label>
											<div class="btn-group bootstrap-select form-control"><button title="-- Select --" class="btn dropdown-toggle bs-placeholder btn-default" role="button" type="button" data-toggle="dropdown" data-id="countryOfIssue"><span class="filter-option pull-left">-- Select --</span>&nbsp;<span class="bs-caret"><span class="caret"></span></span></button><div class="dropdown-menu open" role="combobox"><ul class="dropdown-menu inner" role="listbox" aria-expanded="false"><li class="selected" data-original-index="0"><a tabindex="0" role="option" aria-disabled="false" aria-selected="true" data-tokens="null"><span class="text">-- Select --</span><span class="glyphicon glyphicon-ok check-mark"></span></a></li><li data-original-index="1"><a tabindex="0" role="option" aria-disabled="false" aria-selected="false" data-tokens="null"><span class="text">Corrective</span><span class="glyphicon glyphicon-ok check-mark"></span></a></li><li data-original-index="2"><a tabindex="0" role="option" aria-disabled="false" aria-selected="false" data-tokens="null"><span class="text">Preventive</span><span class="glyphicon glyphicon-ok check-mark"></span></a></li></ul></div><select name="wrType" tabindex="-98" class="form-control selectpicker" id="countryOfIssue">
												<option value="">-- Select --</option>
		                                        <option value="10">Corrective</option><option value="20">Preventive</option>
		                                    </select></div>
		                                </div>

		                                <div class="form-inline col-md-3">
		                                    <label class="control-label" for="workPermitType">Type Of Work Permit</label>
		                                    <input name="workPermitType" class="form-control col-md-3" id="workPermitType" type="text" value="">
		                                </div>
                           			</div>
                           			
                           			<div class="container-fluid">
		                                <div class="form-inline col-md-3">
		                                    <label class="control-label" for="workPermitNumber">Work Permit Number</label>
		                                    <input name="workPermitNumber" class="form-control col-md-3" id="workPermitNumber" type="text" value="">
		                                </div>

		                                <div class="form-inline col-md-3">
		                                    <label class="control-label" for="workPermitExpiryDate">Work Permit Expiry Date</label>
		                                    <input name="workPermitExpiryDate" class="form-control col-md-3" id="workPermitExpiryDate" type="text" value="">
		                                </div>
                           			</div>

                            	</div>
                        	</div>
                    
		  				</div>
		  				
				    </div>
				    
				    
				    
				    <div id="menu1" class="tab-pane fade">
				      <h3>Menu 1</h3>
				      <p>Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</p>
				    </div>
				    <div id="menu2" class="tab-pane fade">
				      <h3>Menu 2</h3>
				      <p>Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam.</p>
				    </div>
				    <div id="menu3" class="tab-pane fade">
				      <h3>Menu 3</h3>
				      <p>Eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.</p>
				    </div>
  				</div>
  				
  				<div class="container-fluid col-md-12">
				    <div class="col-sm-2">
				    	<button type="button" class="btn-primary">SAVE & CONTINUE</button>
				    </div>
  				</div>
  				
            </form:form>
</body>
</html>