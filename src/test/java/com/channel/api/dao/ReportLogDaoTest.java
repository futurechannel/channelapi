package com.channel.api.dao;

import com.channel.api.entity.CallbackLog;
import com.channel.api.entity.ReportLog;
import com.channel.api.util.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

/**
 * Created by gq on 2018/4/11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:config/spring/spring-dao.xml")
public class ReportLogDaoTest {

    @Autowired
    private ReportLogDao reportLogDao;
    @Autowired
    private CallbackDao callbackDao;

    @Test
    public void findList() throws Exception {
        CallbackLog log=new CallbackLog();
        log.setIsBalance(1);
        log.setIsCall(0);

        List<CallbackLog> list=callbackDao.findList(log, DateUtils.dayBefore2Date(-1),new Date(),0, 20);
        for (CallbackLog goods : list) {
            System.out.println(goods);
        }
        System.out.println("--------------------------");
    }

    @Test
    public void update() throws Exception{
        CallbackLog log=new CallbackLog();
        log.setIdfa("1");
        log.setAppCode("1");
        log.setIsCall(1);
        log.setIsFinish(1);
        log.setUpdateTime(new Date());
        callbackDao.updateStatus(log);
    }

    @Test
    public void queryAll() throws Exception {
        List<ReportLog> list=reportLogDao.queryAll(0, 100);
        for (ReportLog goods : list) {
            System.out.println(goods);
        }
        System.out.println("--------------------------");
    }

    @Test
    public void findById() throws Exception{
        ReportLog log=reportLogDao.findById("1dwdw1","zh3","report_log_20210321");
        if(log!=null){
            System.out.println(log);
        }

        System.out.println("--------------------------");
    }

    @Test
    public void insert() throws Exception{
        ReportLog log=new ReportLog();
        log.setIdfa("dasdas");
        log.setAppCode("qdqwd");
        log.setClickId("sdcdsv");
        log.setAdverterCode("dsvdsv");
        log.setReportTime(new Date());
        try {
            int i=reportLogDao.insert(log,"report_log");
            System.out.println("======="+i);
        }catch (DuplicateKeyException e){
            System.out.println("主键冲突");
        }

    }

}