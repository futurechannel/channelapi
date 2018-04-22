package com.channel.api.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

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

    @Test
    public void findAll() throws Exception {
        System.out.println(appInfoDao.findAll());
        System.out.println("======");
    }

    @Test
    public void findAllAdv() throws Exception {
        System.out.println(advertInfoDao.findAll());
        System.out.println("======");
    }

}