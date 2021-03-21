package com.channel.api.service;

import com.channel.api.entity.AppInfo;

import java.util.List;

/**
 * Created by gq on 2018/4/16.
 */
public interface AppInfoService {

    List<AppInfo> findAll();

    AppInfo  getAppInfoFromMemCache(String appcode,String appid);
}
