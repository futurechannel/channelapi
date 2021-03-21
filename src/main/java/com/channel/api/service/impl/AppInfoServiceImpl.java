package com.channel.api.service.impl;

import com.channel.api.constants.ConstantMaps;
import com.channel.api.dao.AppInfoDao;
import com.channel.api.entity.AppInfo;
import com.channel.api.enums.ErrorCode;
import com.channel.api.exception.ApiException;
import com.channel.api.service.AppInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    @Override
    public AppInfo getAppInfoFromMemCache(String appcode, String appid) {
        AppInfo appInfo;
        if (!StringUtils.isEmpty(appcode)) {
            appInfo = ConstantMaps.getAppInfoByCode(appcode);
        } else {
            appInfo = ConstantMaps.getAppCode(appid);
        }

        if (appInfo == null) {
            throw new ApiException(ErrorCode.E601.getCode() + "");
        }
        return appInfo;
    }
}
