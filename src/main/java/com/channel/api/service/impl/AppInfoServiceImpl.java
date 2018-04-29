package com.channel.api.service.impl;

import com.channel.api.dao.AppInfoDao;
import com.channel.api.entity.AppInfo;
import com.channel.api.service.AppInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by gq on 2018/4/29.
 */
@Service
public class AppInfoServiceImpl implements AppInfoService {
    @Autowired
    private AppInfoDao appInfoDao;

    @Override
    public List<AppInfo> findAll() {
        return appInfoDao.findAll();
    }
}
