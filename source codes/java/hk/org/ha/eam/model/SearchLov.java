/* --------------------------------------
    File Name: SearchLov.java
    Author: Kin Shum (PCCW)
    Date: 1-Sep-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - SearchLov Bean

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20170901	Kin Shum	Initial version
   -------------------------------------- */

package hk.org.ha.eam.model;

public class SearchLov {

	int query;
	int parameters;
	int type;
	String value;

	public int getQuery() {
		return query;
	}

	public void setQuery(int query) {
		this.query = query;
	}

	public int getParameters() {
		return parameters;
	}

	public void setParameters(int parameters) {
		this.parameters = parameters;
	}

	public int getType() {
		return type;
	}

	public void setType( int type) {
		this.type = type;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "SearchWorkRequest [query=" + query + ", parameters=" + parameters + ", type=" + type  + ", value=" + value + "]";
	}

	
}
