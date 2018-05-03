package hk.org.ha.eam.dao;

import java.util.List;


import hk.org.ha.eam.model.SearchPeople;
import hk.org.ha.eam.model.People;

public interface PeopleDao {

	public List<People> searchPeople(SearchPeople searchCriteria) throws Exception ;
	
}
