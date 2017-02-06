package com.springmvc.dao;

import com.springmvc.framework.IBaseDao;

public class TestDao extends IBaseDao{

	public String[] dosomething() {
		String[] row = this.baseDao.getRow("select 1 from dual");
		row = this.baseDao.getRow("select * from book where book_id=?",new Object[]{"1"});
		return row;
	}

}
