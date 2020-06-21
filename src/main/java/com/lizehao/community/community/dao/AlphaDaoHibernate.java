package com.lizehao.community.community.dao;

import org.springframework.stereotype.Repository;

@Repository("alphaHibernate")
public class AlphaDaoHibernate implements AlphaDao{

    @Override
    public String select() {
        return "OK";
    }
}
