package hk.org.ha.eam.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import hk.org.ha.eam.util.ConnectionProvider;
import hk.org.ha.ebs.common.EBSUtil;
import hk.org.ha.eam.common.util.DateUtil;

import hk.org.ha.eam.model.SearchPeople;
import hk.org.ha.eam.model.SearchWorkRequest;
import hk.org.ha.eam.model.WorkRequest;
import hk.org.ha.eam.model.People;

@Repository
public class PeopleDaoImpl implements PeopleDao{
	
	private static final Logger logger = Logger.getLogger(PeopleDaoImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;   
	private JdbcTemplate jdbcTemplate;
	
	DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
	
	@Autowired
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	@Autowired
	DateUtil dateUtil;
	
	@Autowired
	private MessageSource messageSource;
	
    public PeopleDaoImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
    
    @Override
    @Transactional(rollbackFor=Exception.class)	
    public List<People> searchPeople(SearchPeople searchCriteria) throws Exception {
   
    	String empNumber = searchCriteria.getEmpNumber();
    	String effDate = searchCriteria.getEffDate();
    	String hkid = searchCriteria.getHkid();
    	String hkidName = searchCriteria.getHkidName();
    	String chnName = searchCriteria.getChnName();
    	
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        
        StringBuffer sqlBuffer = new StringBuffer();
        
        sqlBuffer.append("SELECT PERSON_ID, EFFECTIVE_START_DATE effStartDate, EFFECTIVE_End_DATE effEndDate, \n");
        sqlBuffer.append("PERSON_TYPE_ID, LAST_NAME lastName, DATE_OF_BIRTH, EMPLOYEE_NUMBER empNumber, \n");
        sqlBuffer.append("FIRST_NAME firstName, FULL_NAME fullName, MARITAL_STATUS, NATIONAL_IDENTIFIER hkid , SEX, TITLE \n");
        sqlBuffer.append(" FROM per_people_x where \n");
        
        if (empNumber != null && !"".equals(empNumber)) {
        	sqlBuffer.append("employee_number = :empNumber ");
        	paramMap.addValue("empNumber", empNumber);
        }
        
        List<People> listPeople = namedParameterJdbcTemplate.query(sqlBuffer.toString(), paramMap , new RowMapper<People>(){
            @Override
            public People mapRow(ResultSet rs, int rowNum) throws SQLException {
            	People aPeople = new People();
            	Date aDate = new Date();

            	aPeople.setEmpNumber(rs.getString("empNumber"));
            	aPeople.setHkid(rs.getString("hkid"));
            	aPeople.setLastName(rs.getString("lastName"));
            	aPeople.setFirstName(rs.getString("firstName"));
            	aPeople.setFullName(rs.getString("fullName"));
            	aPeople.setMaritalStatus(rs.getString("MARITAL_STATUS"));
            	aPeople.setSex(rs.getString("SEX"));
            	            	
            	logger.debug("Retrieved Employee Number " + aPeople.getEmpNumber());
                
                return aPeople;
            }
        });
        
        logger.debug("No of rec="+listPeople.size());
        logger.debug("Limit="+searchCriteria.getQueryLimit());
        
        return listPeople;
    }
	
}
