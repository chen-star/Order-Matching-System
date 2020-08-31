package com.alex.order.util;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DbUtil {

    private static DbUtil dbUtil = null;

    private DbUtil(){}

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    private void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    private SqlSessionTemplate getSqlSessionTemplate() {
        return this.sqlSessionTemplate;
    }

    @PostConstruct
    private void init() {
        dbUtil = new DbUtil();
        dbUtil.setSqlSessionTemplate(this.sqlSessionTemplate);
    }

    public static long getId() {
        Long result = dbUtil.getSqlSessionTemplate().selectOne(
                "testMapper.queryBalance"
        );

        if (result == null) {
            return -1;
        }

        return result;
    }
}
