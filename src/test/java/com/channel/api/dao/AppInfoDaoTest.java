package com.channel.api.dao;

import com.channel.api.entity.FailCallback;
import com.channel.api.util.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * Created by gq on 2018/4/22.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:config/spring/spring-dao.xml")
public class AppInfoDaoTest {
    @Autowired
    private AppInfoDao appInfoDao;
    @Autowired
    private AdvertInfoDao advertInfoDao;
    @Autowired
    private FailCallbackDao failCallbackDao;

    @Test
    public void findAll() throws Exception {
        long start=new Date().getTime();
        System.out.println(appInfoDao.findAll());
        System.out.println("======"+(new Date().getTime()-start)/1000d);
    }

    @Test
    public void findAllAdv() throws Exception {
        System.out.println(advertInfoDao.findAll());
        System.out.println("======");
    }

    @Test
    public void countFailCall() throws Exception {
        Date now =new Date();
        String start= DateUtils.defineDayBefore2Str(now,-1,"yyyy-MM-dd 00:00:00");
        String end= DateUtils.formatDate2Str(now,"yyyy-MM-dd 00:00:00");
        FailCallback failCallback=new FailCallback();
        failCallback.setIsBalance(1);
        failCallback.setIsRecall(0);
        System.out.println(failCallbackDao.countFailCall(failCallback,start,end));
        System.out.println("======");
    }
}