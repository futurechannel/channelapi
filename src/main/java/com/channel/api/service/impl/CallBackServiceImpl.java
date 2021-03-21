package com.channel.api.service.impl;

import com.channel.api.constants.Constants;
import com.channel.api.dao.CallbackDao;
import com.channel.api.dto.UniqueIdDto;
import com.channel.api.entity.AdvertInfo;
import com.channel.api.entity.AppInfo;
import com.channel.api.entity.CallbackLog;
import com.channel.api.enums.ErrorCode;
import com.channel.api.exception.ApiException;
import com.channel.api.service.CallBackService;
import com.channel.api.util.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
        return callbackDao.findList(log, startTime, endTime, offset, limit);
    }

    @Override
    public int updateStatus(CallbackLog callbackLog) {
        return callbackDao.updateStatus(callbackLog);
    }

    @Override
    public int insertCallBack(CallbackLog callbackLog) {
        int count;
        try {
            count = callbackDao.insertCallBack(callbackLog);
        } catch (DuplicateKeyException e) {
            logger.info("app主键重复:" + callbackLog.toString());
            return 0;
        } catch (Exception e) {
            logger.error("保存失败:" + callbackLog.toString(), e);
            return -1;
        }
        return count;
    }

    @Override
    public String generateCallbackUrl(UniqueIdDto uniqueIdDto, AdvertInfo advertInfo, AppInfo appInfo) {
        String callback;
        try {
            if (StringUtils.isEmpty(advertInfo.getOurCallBackUrl())) {
                callback = URLEncoder.encode(ConfigUtils.getValue("our.callback.url")
                        + "idfa=" + uniqueIdDto.getUniqueId() + Constants.URL_PARAM_SEPARATOR
                        + "appcode=" + appInfo.getAppCode() + Constants.URL_PARAM_SEPARATOR
                        + "type=" + uniqueIdDto.getUniqueType().getType(), "utf-8");
            } else {
                callback = URLEncoder.encode(advertInfo.getOurCallBackUrl() + Constants.OUR_CALL_BACK_POSTFIX
                        + "idfa=" + uniqueIdDto.getUniqueId() + Constants.URL_PARAM_SEPARATOR
                        + "appcode=" + appInfo.getAppCode() + Constants.URL_PARAM_SEPARATOR
                        + "type=" + uniqueIdDto.getUniqueType().getType(), "utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("encode 转码错误", e);
            throw new ApiException(ErrorCode.E901.getCode() + "");
        }
        return callback;
    }
}
