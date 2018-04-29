package com.channel.api.service.impl;

import com.channel.api.dao.CallbackDao;
import com.channel.api.entity.CallbackLog;
import com.channel.api.service.CallBackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by gq on 2018/4/15.
 */
@Service
public class CallBackServiceImpl implements CallBackService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CallbackDao callbackDao;

    @Override
    public List<CallbackLog> findList(CallbackLog log, Date startTime, Date endTime, int offset, int limit) {
        return callbackDao.findList(log,startTime,endTime,offset,limit);
    }

    @Override
    public int updateStatus(CallbackLog callbackLog) {
        return callbackDao.updateStatus(callbackLog);
    }

    @Override
    public int insertCallBack(CallbackLog callbackLog) {
        int count;
        try {
            count=callbackDao.insertCallBack(callbackLog);
        }catch(DuplicateKeyException e){
            logger.info("app主键重复:"+callbackLog.toString());
            return 0;
        } catch (Exception e){
            logger.error("保存失败:"+callbackLog.toString(),e);
            return -1;
        }
        return count;
    }
}
