package com.channel.api.service.impl;

import com.channel.api.dao.FailCallbackDao;
import com.channel.api.entity.FailCallback;
import com.channel.api.service.FailCallbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by gq on 2018/4/29.
 */
@Service
public class FailCallbackServiceImpl implements FailCallbackService {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FailCallbackDao failCallbackDao;

    @Override
    public int insertFailCallback(FailCallback failCallback) {
        int count;
        try {
            count=failCallbackDao.insertFailCallback(failCallback);
        } catch(DuplicateKeyException e){
            LOG.error("主键重复,保存回调记录失败:"+failCallback.toString());
            return 0;
        } catch (Exception e){
            LOG.error("保存回调记录失败:"+failCallback.toString(),e);
            return -1;
        }
        return count;
    }

    @Override
    public int countFailCall(FailCallback params, String start, String end) {

        return failCallbackDao.countFailCall(params,start,end);
    }

    @Override
    public List<FailCallback> findList(FailCallback params, String start, String end, int limit) {
        return failCallbackDao.findList(params,start,end,limit);
    }

    @Override
    public int updateStatus(FailCallback params) {
        return failCallbackDao.updateStatus(params);
    }
}
