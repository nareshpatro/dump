/* --------------------------------------
    File Name messageUI.js
    Author Frankie Lee (PCCW)
    Date January ‎5, ‎2018
    Description
    Define the message used in front-end Javascript
   -------------------------------------- -*/

var ha_messages = {
		//Create/Update Work Request and Order
		'HA_INFO_NOATTACHMENTUPDATED' : 'No attachment was updated.',
		'HA_INFO_INACTIVEASSET' : 'Asset is not in Active status.',
		//Create/Update Work Request
		'HA_ERROR_WR_INACTIVEASSETSTATUS' : 'Please cancel or reject the Work Request for the inactive asset.',
		//Create/Update Work Order
		'HA_ERROR_INACTIVEMAINTENANBODY' : 'Inactive Maintenance Body.',
		'HA_ERROR_FAILURETRIPLET' : 'Please ensure the Failure Cause code, Failure Symptom Code and Repair Resolution Code are inputted together.',
		'HA_ERROR_WO_INACTIVEASSETSTATUS' : 'Please cancel, reject or complete the Work Order for the inactive asset.',
		'HA_ERROR_NONGS1VENDOR' : 'Auto-Send WO to Supplier cannot be “XML or Portal” for Non-GS1 vendor.',
		'HA_ERROR_GS1VENDOR' : 'Auto-Send WO to Supplier should be “XML or Portal” for GS1 vendor.',
		'HA_ERROR_SENTALREADY' : 'Work Order has already been sent, cannot change to XML or Portal.',
		'HA_ERROR_PENDINGREQUEST' : 'There is a pending resend request in process. Please update and/or resend the work order again later.',
		'HA_ERROR_RESENDCONTACTMETHOD' : 'Auto Send to Supplier should be "Email or Fax" for resend.',
		'HA_ERROR_RESENDSTATUS' : 'Work Order Status should be "Released" for resend.',
		//Search Work Request and Work Order
		'HA_ERROR_ENTERDATE' : 'Please select a date type and enter a date range for searching.',
		'HA_ERROR_DATETOMISSING' : 'Date To is missing.',
		'HA_ERROR_DATEFROMMISSING' : 'Date From is missing.',
		'HA_ERROR_DATEFROMLARGER' : 'Date From should not be larger than Date To.',
		'HA_ERROR_DATEOUTOFRANGE' : 'Maximum date range exceeded.',
		'HA_ERROR_LEASTONECRITERIA' : 'Please enter at least one searching criteria.',
		//Search Work Request
		'HA_ERROR_WR_SEARCHWILDCARDERROR' : 'Search criteria Work Request Number should not begin with "%".',
		'HA_ERROR_WR_EXPORTSELECTALL' : 'Please select all to export instead of just select over 100 specific Work Requests for export.',
	    'HA_ERROR_WR_PRINTMAX' : 'Please select max. 10 Work Requests to print.',
		//Search Work Order
		'HA_ERROR_WO_SEARCHWILDCARDERROR' : 'Search criteria Work Order Number or Serial Number should not begin with "%".',
		'HA_ERROR_WO_EXPORTSELECTALL' : 'Please select all to export instead of just select over 100 specific Work Orders for export.',
	    'HA_ERROR_WO_PRINTMAX' : 'Please select max. 10 Work Orders to print.',
		//General
	    'HA_ERROR_SELECTPREFERREDCRITERIA' : 'Please select the preferred criteria.',
	    'HA_ERROR_ORGNEEDED' : 'Please select an EAM Org.'
	};
