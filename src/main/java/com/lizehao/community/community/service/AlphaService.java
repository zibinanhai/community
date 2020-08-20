package com.lizehao.community.community.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class AlphaService {

//    public AlphaService(){
//        System.out.println("构造AlphaService");
//
//    }
//
//    @PostConstruct
//    public void init(){
//        System.out.println("初始化AlphaService");
//    }
//
//    @PreDestroy
//    public void destroy(){
//        System.out.println("销毁AlphaService");
//    }

    @Autowired
    private TransactionTemplate transactionTemplate;

    public Object save() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        
        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {


                //新增用户

                //新增帖子

                return "OK";
            }
        });
    }

}
