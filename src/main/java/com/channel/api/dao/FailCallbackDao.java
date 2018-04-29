package com.channel.api.dao;

import com.channel.api.entity.FailCallback;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by gq on 2018/4/29.
 */
public interface FailCallbackDao {

    int insertFailCallback(FailCallback failCallback);

    int countFailCall(@Param("log") FailCallback params, @Param("startTime") String start, @Param("endTime") String end);

    List<FailCallback> findList(@Param("log") FailCallback params,@Param("startTime")String start,@Param("endTime") String end, @Param("limit") int limit);

    int updateStatus(FailCallback params);

}
