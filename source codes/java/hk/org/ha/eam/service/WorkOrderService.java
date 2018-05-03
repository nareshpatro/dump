package hk.org.ha.eam.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import hk.org.ha.eam.model.SearchWorkOrder;
import hk.org.ha.eam.model.WorkOrder;

public interface WorkOrderService {
    
	public String newWorkOrder(WorkOrder workOrder, HttpServletRequest req) throws Exception;
    
    public List<WorkOrder> viewWO (SearchWorkOrder searchCriteria) throws Exception;
    
    public String resendWorkOrder(WorkOrder workOrder) throws Exception;
    
    public String chkWorkOrder(WorkOrder workOrder) throws Exception;
}
