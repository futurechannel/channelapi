package com.channel.api.web;

import com.channel.api.base.BaseController;
import com.channel.api.constants.ConstantMaps;
import com.channel.api.dto.BaseResult;
import com.channel.api.entity.CallbackLog;
import com.channel.api.entity.ReportLog;
import com.channel.api.enums.ErrorCode;
import com.channel.api.exception.ApiException;
import com.channel.api.form.EleBackForm;
import com.channel.api.service.CallBackService;
import com.channel.api.service.ReportLogService;
import com.channel.api.util.ConfigUtils;
import com.channel.api.util.GsonUtils;
import com.channel.api.util.NumUtils;
import com.dianping.cat.Cat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Date;

/**
 * Created by gq on 2018/4/11.
 */
@Controller
@RequestMapping("/callback")
public class CallBackController extends BaseController {

    @Autowired
    private ReportLogService reportLogService;

    @Autowired
    private CallBackService callBackService;

    @RequestMapping(value = "/app")
    @ResponseBody
    public BaseResult callBackEle(@Valid EleBackForm form) {

        logger.info("app req:[" + GsonUtils.pojoToJson(form) + "]");

        String appCode = form.getAppcode();
        String idfa = form.getIdfa();

        if (!ConstantMaps.appCodeMap.keySet().contains(appCode)) {
            logger.error("appCode不存在[" + appCode + "]");
            throw new ApiException(ErrorCode.E801.getCode() + "");
        }

        Cat.logEvent("callback_app", appCode);

        ReportLog log = reportLogService.findById(idfa, appCode);

        CallbackLog callbackLog = new CallbackLog();
        callbackLog.setIdfa(idfa);
        callbackLog.setAppCode(appCode);
        callbackLog.setCreateTime(new Date());
        callbackLog.setIp(form.getUser_ip());

        if (log == null) {
            callbackLog.setIsBalance(0);
            logger.warn("未查到idfa记录[" + idfa + "]");
        } else {
            if (!StringUtils.isEmpty(log.getCallback())) {
                callbackLog.setCallback(log.getCallback());
                callbackLog.setAdverterCode(log.getAdverterCode());
                callbackLog.setIsCall(0);

                //扣量逻辑,产生一个1-100的随机数,<=per表示需要回调
                callbackLog.setIsBalance(NumUtils.randBoolean(1, 100, ConstantMaps.getBalanceRatio(appCode, log.getAdverterCode())));
            } else {
                callbackLog.setIsBalance(0);
                callbackLog.setAdverterCode(log.getAdverterCode());
            }

        }

        int i = callBackService.insertCallBack(callbackLog);

        if (i < 0) {
            throw new ApiException(ErrorCode.E500.getCode() + "");
        }

        return new BaseResult(ErrorCode.E200);
    }

}
