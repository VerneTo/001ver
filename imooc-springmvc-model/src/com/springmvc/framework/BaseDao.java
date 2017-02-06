package com.springmvc.framework;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@SuppressWarnings("unchecked")
public class BaseDao{
	private JdbcTemplate jdbcTemplate;
	private TransactionTemplate transactionTemplate;
	private Logger log = Logger.getLogger(this.getClass().getName());

	public List<String[]> getRows(String sql) {
		log.info(sql);
		List<String[]> strs = (List<String[]>) this.jdbcTemplate.query(sql,
				new ResultSetExtractor<Object>() {
					public Object extractData(ResultSet rs)throws SQLException, DataAccessException{
						ResultSetMetaData rsd = rs.getMetaData();
						int num = rsd.getColumnCount();
						List<String[]> list = new ArrayList<String[]>();
						while (rs.next()) {
							String[] strs = new String[num];
							for (int i = 0; i < num; i++) {
								strs[i] = rs.getString(i + 1);
							}
							list.add(strs);
						}
						return list;
					}

				});
		return strs;
	}

	public List<String[]> getRows(String sql, Object[] params) {
		log.info(sql+" params:"+Arrays.toString(params));
		List<String[]> strs = (List<String[]>) this.jdbcTemplate.query(sql,
				params, new ResultSetExtractor<Object>() {
					public Object extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						ResultSetMetaData rsd = rs.getMetaData();
						int num = rsd.getColumnCount();
						List<String[]> list = new ArrayList<String[]>();
						while (rs.next()) {
							String[] strs = new String[num];
							for (int i = 0; i < num; i++) {
								strs[i] = rs.getString(i + 1);
							}
							list.add(strs);
						}
						return list;
					}

				});
		return strs;
	}

	public synchronized String[] getRow(String sql) {
		List<String[]> list = this.getRows(sql);
		if (list.size() == 0)
			return null;
		else
			return list.get(0);
	}

	public synchronized String[] getRow(String sql, Object[] params) {
		List<String[]> list = this.getRows(sql, params);
		if (list.size() == 0)
			return null;
		else
			return list.get(0);
	}

	public int execute(String sql) {
		log.info(sql);
		int r = this.jdbcTemplate.update(sql);
		return r;
	}

	public int execute(String sql, Object[] params) {
		log.info(sql+" params:"+Arrays.toString(params));
		int r = this.jdbcTemplate.update(sql, params);
		return r;
	}

	public void executeBatch(List<String> sql) {
		log.info(sql);
		String[] ss = new String[sql.size()];
		for(int is=0 ; is<sql.size() ; is++){
			ss[is] = sql.get(is);
		}
		final String[] sqls = ss;
		this.transactionTemplate.execute(new TransactionCallbackWithoutResult(){
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				try{
					jdbcTemplate.batchUpdate(sqls);
				}catch(Exception e){
					status.setRollbackOnly();
				}
			}
		});
	}
	
	public synchronized void executeBatch(String sql, final List<Object[]> params) {
		log.info(sql);
		this.jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			public int getBatchSize() {
				return params.size();
			}

			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				Object[] o = params.get(i);
				for (int y = 0; y < o.length; y++) {
					ps.setObject(y + 1, o[y]);
				}
				log.info("params:"+Arrays.toString(o));
			}

		});
	}
	
	public synchronized void call(String prc) throws SQLException {
		log.info("call:"+prc);
		this.getJdbcTemplate().execute("{CALL "+prc+"}", new CallableStatementCallback<Object>(){
			public Object doInCallableStatement(CallableStatement cs)
					throws SQLException, DataAccessException {
				cs.execute();
				return null;
			}
		});
		log.info("end call:"+prc);
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
}
