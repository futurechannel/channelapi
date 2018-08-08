package com.channel.api.service.impl;

import com.channel.api.constants.ConstantMaps;
import com.channel.api.dao.ReportLogDao;
import com.channel.api.entity.ReportLog;
import com.channel.api.service.ReportLogService;

import com.channel.api.util.ConfigUtils;
import com.channel.api.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by gq on 2018/4/13.
 * 上报
 */
@Service
public class ReportLogServiceImpl implements ReportLogService {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private ReportLogDao reportLogDao;

    @Override
    public int insert(ReportLog log) {
        String tableName = ConstantMaps.getReportTableName();

        int count;
        try {
            count = reportLogDao.insert(log, tableName);
        } catch (DuplicateKeyException e) {
            return 0;
        } catch (Exception e) {
            LOG.error("保存失败:" + log.toString(), e);
            return -1;
        }
        return count;
    }

    /**
     * 查最近两天的上报记录
     *
     * @param idfa
     * @param appcode
     * @return
     */
    @Override
    public ReportLog findById(String idfa, String appcode) {

        List<String> tableNames = ConstantMaps.getReportTableNames(appcode);
        ReportLog log = null;

        if(CollectionUtils.isEmpty(tableNames)){
            return log;
        }

        for (String tableName : tableNames) {
            try {
                log = reportLogDao.findById(idfa, appcode, tableName);
            } catch (Exception e) {
                LOG.error("查询异常table:" + tableName, e);
                continue;
            }
            if (log != null) {
                return log;
            }
        }

        return log;
    }


}
