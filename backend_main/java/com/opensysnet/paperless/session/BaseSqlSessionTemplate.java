package com.opensysnet.paperless.session;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;

public class BaseSqlSessionTemplate extends SqlSessionTemplate {

	public BaseSqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType) {
		super(sqlSessionFactory, executorType);
	}

}
