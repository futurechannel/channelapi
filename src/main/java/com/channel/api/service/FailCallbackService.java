package com.channel.api.service;

import com.channel.api.entity.FailCallback;

import java.util.List;

/**
 * Created by gq on 2018/4/29.
 */
public interface FailCallbackService {
    int insertFailCallback(FailCallback failCallback);

    int countFailCall(FailCallback params,String start,String end);

    List<FailCallback> findList(FailCallback params,String start,String end,int limit);

    int updateStatus(FailCallback params);
}
