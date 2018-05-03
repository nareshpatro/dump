<%-- 
    Document   : Personal Details
    Created on : Apr 24, 2018 12:23:28 PM
    Author     : Carlo
    Last Update: Apr 24, 2018 12:23:28 PM
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Personal Details</title>
	<script src="<spring:url value="/resources/js/moment.js?v=${initParam.buildTimeStamp}" /> " type="text/javascript"></script>
    <script src="<spring:url value="/resources/js/jquery.js?v=${initParam.buildTimeStamp}" />"></script>        
    <script src="<spring:url value="/resources/js/ha/pages/messageUI.js?v=${initParam.buildTimeStamp}" />"></script>
    <script src="<spring:url value="/resources/js/ha/pages/commonFunctions.js?v=${initParam.buildTimeStamp}" />"></script>
    <link rel="stylesheet" href="<spring:url value="/resources/css/bootstrap.css?v=${initParam.buildTimeStamp}" />">
    <link rel="stylesheet" href="<spring:url value="/resources/css/custom.css?v=${initParam.buildTimeStamp}" />">
    <link rel="stylesheet" href="<spring:url value="/resources/css/bootstrapTable.css?v=${initParam.buildTimeStamp}" />">
    <link rel="stylesheet" href="<spring:url value="/resources/js/datatables/Select-1.2.2/css/select.bootstrap.css?v=${initParam.buildTimeStamp}" />">
        
</head>
<body>
	<div class="container">
		<div class="container-fluid col-md-12">
			<div class="col-md-3 form-inline">				
				<div class="form-group">
				<label for="hkid">HKID</label>
					<input class="form-control" id="hkid" type="text">
				</div>
			</div>
				  					
			<div class="col-md-3 form-inline">
				<div class="form-group">
					<label for="dateOfBirth">Date Of Birth</label>
					<input class="form-control" id="dateOfBirth" type="text" >
				</div>
			</div>
			  					
			<div class="col-md-2 form-inline">
				<div class="form-group">
					<label for="age">Age</label>
					<input class="form-control" id="age" type="text" style="width: 60px;" disabled>
				</div>
			</div>
			  					
			<div class="col-md-3 form-inline">
				<div class="form-group">
					<label for="maritalStatus">Marital Status</label>
					<button id="maritalStatus" title="-- Select --" class="btn dropdown-toggle bs-placeholder btn-default" 
		  				role="button" aria-expanded="false" type="button" data-toggle="dropdown" data-id="title"><span class="filter-option pull-left">-- Select --</span>&nbsp;<span class="bs-caret"><span class="caret"></span></span></button>
				</div>
			</div>
		</div> 
			  				
		<div class="container-fluid col-md-12" style="margin-top: 15px">
			<div class="col-md-6 form-inline">
				<div class="form-group">
					<label for="emailAddress">Email Address</label>
					<input class="form-control" id="emailAddress" type="text">
				</div>
			</div>
				  					
			<div class="col-md-6 form-inline">
				<div class="form-group">
					<label for="preferredName">Preferred Name</label>
					<input class="form-control" id="preferredName" type="text">
				</div>
			</div>
		</div>
			  				
		<div class="container-fluid col-md-12" style="margin-top: 15px; margin-bottom: 15px;">
			<div class="col-md-3 form-inline">
				<div class="form-group">
					<label for="countryOfBirth">Country Of Birth</label>
					<button id="countryOfBirth" title="-- Select --" class="btn dropdown-toggle bs-placeholder btn-default" 
		  				role="button" aria-expanded="false" type="button" data-toggle="dropdown" data-id="title"><span class="filter-option pull-left">-- Select --</span>&nbsp;<span class="bs-caret"><span class="caret"></span></span></button>
				</div>
			</div>
			  					
			<div class="col-md-3 form-inline">
				<div class="form-group">
					<label for="permanentHkidHolder">Permanent HKID Holder</label>
					<button id="permanentHkidHolder" title="-- Select --" class="btn dropdown-toggle bs-placeholder btn-default" 
		  				role="button" aria-expanded="false" type="button" data-toggle="dropdown" data-id="title"><span class="filter-option pull-left">-- Select --</span>&nbsp;<span class="bs-caret"><span class="caret"></span></span></button>
				</div>
			</div>
				  					
			<div class="col-md-6 form-inline">
				<div class="form-group">
					<label for="permanentHkidCardRcvDate">Permanent HKID Card Received Date</label>
					<input class="form-control" id="permanentHkidCardRcvDate" type="text">
				</div>
			</div>
		</div>
	</div>

	<div class="panel panel-primary" style="width: 91%">
		<div class="panel-heading">
			<h4 class="panel-title">
				<a class="accordion-toggle" id="workPermitDetailsHeader" href="#workPermitDetailsArea" data-toggle="collapse" data-parent="#accordion">Work Permit Details</a>
			</h4>
		</div>
			  					
		<div class="container">
			<div class="panel-collapse collapse in" id="workPermitDetailsArea">
		    	<div class="panel-body" style="padding: 8px 0px;">
		        	<div class="container-fluid col-md-12">
				    	<div class="form-inline col-md-4" style="text-align: right;padding-right: 81px">
				        	<label class="control-label" for="passportNumber">Passport Number</label>
				            <input name="passportNumber" class="form-control" id="passportNumber" type="text" value="">
				        </div>
		                                
				        <div class="form-inline col-md-3">
				       		<label class="control-label" for="countryOfIssue">Country Of Issue</label>
				            <button id="countryOfIssue" style="width: 147px;" class="btn dropdown-toggle bs-placeholder btn-default" 
						  		role="button" aria-expanded="false" type="button" data-toggle="dropdown" data-id="title">
						  		<span class="filter-option pull-left">-- Select --</span>&nbsp;
							  	<span class="bs-caret pull-right">
							  		<span class="caret"></span>
							  	</span>
						  	</button>
				        </div>
		
				        <div class="form-inline col-md-4">
				           <label class="control-label" for="workPermitType">Type Of Work Permit</label>
				           <input name="workPermitType" class="form-control" id="workPermitType" type="text" value="">
				        </div>
		           	</div>
		                           			
		            <div class="container-fluid col-md-12" style="margin-top: 15px;">
				    	<div class="form-inline col-md-4" style="text-align: right;padding-right: 81px">
				        	<label class="control-label" for="workPermitNumber">Work Permit Number</label>
				            <input name="workPermitNumber" class="form-control" id="workPermitNumber" type="text" value="">
				        </div>
		
				        <div class="form-inline col-md-4">
				        	<label class="control-label" for="workPermitExpiryDate">Work Permit Expiry Date</label>
				            <input name="workPermitExpiryDate" class="form-control" id="workPermitExpiryDate" type="text" value="">
				        </div>
		            </div>
		
		        </div>
		    </div>
	    </div>
	                    
	</div>

</body>
</html>