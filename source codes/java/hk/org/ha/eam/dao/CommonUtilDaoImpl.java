/* --------------------------------------
    File Name: CommonUtilDao.java
    Author: Fanny Hung (PCCW)
    Date: 9-Sep-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - EBS initialized connection Implementation

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20170909	Fanny Hung	Initial version
   -------------------------------------- */

package hk.org.ha.eam.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public class CommonUtilDaoImpl implements CommonUtilDao {

	private static final Logger logger = Logger.getLogger(CommonUtilDaoImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public CommonUtilDaoImpl(DataSource dataSource) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void ebsAppInit(String userID, String respID, String appID) throws Exception {
		
		logger.info("Processing ebsAppInit="+userID + "**" + respID + "**" + appID);
		
		MapSqlParameterSource paramMap = new MapSqlParameterSource();		
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("begin fnd_global.apps_initialize(");
		sqlBuffer.append(userID);
		sqlBuffer.append(",");
		sqlBuffer.append(respID);
		sqlBuffer.append(",");
		sqlBuffer.append(appID);
		sqlBuffer.append("); end;");
		
		logger.info("DB Init SQL="+sqlBuffer);
		
		namedParameterJdbcTemplate.execute(sqlBuffer.toString(), 
				new PreparedStatementCallback<Boolean>(){			 
            		@Override
            		public Boolean doInPreparedStatement(PreparedStatement ps)
            				throws SQLException, DataAccessException {
            				return ps.execute();
            		}
        		});
	}
	
}
