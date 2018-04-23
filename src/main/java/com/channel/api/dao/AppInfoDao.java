package com.channel.api.dao;

import com.channel.api.entity.AppInfo;

import java.util.List;

/**
 * Created by gq on 2018/4/21.
 */
public interface AppInfoDao {

    List<AppInfo> findAll();
}
