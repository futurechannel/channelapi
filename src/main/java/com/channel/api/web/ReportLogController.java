package com.channel.api.web;

import com.channel.api.base.BaseController;
import com.channel.api.constants.Constants;
import com.channel.api.dto.BaseResult;
import com.channel.api.dto.UniqueIdDto;
import com.channel.api.entity.AdvertInfo;
import com.channel.api.entity.AppInfo;
import com.channel.api.entity.ReportLog;
import com.channel.api.enums.ErrorCode;
import com.channel.api.exception.ApiException;
import com.channel.api.form.ReportLogForm;
import com.channel.api.service.AdvertInfoService;
import com.channel.api.service.AppInfoService;
import com.channel.api.service.CallBackService;
import com.channel.api.service.ReportLogService;
import com.channel.api.util.*;
import com.dianping.cat.Cat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Date;


/**
 * Created by gq on 2018/4/13.
 * 上报
 */
@Controller
@RequestMapping("/click")
public class ReportLogController extends BaseController {

    public static final String OUR_CALL_BACK_POSTFIX = "/channelapi/callback/app?";


    @Autowired
    private ReportLogService logService;
    @Autowired
    private AppInfoService appInfoService;
    @Autowired
    private AdvertInfoService advertInfoService;
    @Autowired
    private CallBackService callBackService;

    @RequestMapping("/idfa")
    @ResponseBody
    public BaseResult upReport(@Valid ReportLogForm form) {
        logger.info("req:[" + GsonUtils.pojoToJson(form) + "]");
        long start = new Date().getTime();

        //1.获取广告主和渠道的相关配置
        AppInfo appInfo = appInfoService.getAppInfoFromMemCache(form.getAppcode(), form.getAppid());
        String advertCode = form.getRef();
        advertInfoService.checkAdvertCode(advertCode);
        String appCode = appInfo.getAppCode();
        AdvertInfo advertInfo = advertInfoService.getAdvertInfoFromMemCache(appCode, advertCode);

        //2.获取唯一uniqueId和Id类型
        UniqueIdDto uniqueIdDto = logService.getUniqueId(form);

        //3.组装本系统自己的回调地址
        String callbackUrl = callBackService.generateCallbackUrl(uniqueIdDto, advertInfo, appInfo);

        //检查必要配置
        String comeFrom = advertInfo.getComeFrom();
        if (StringUtils.isEmpty(comeFrom) || StringUtils.isEmpty(callbackUrl) || StringUtils.isEmpty(appInfo.getReportUrl())) {
            logger.error("Forwarding request param error:[" + " comeFrom:" + comeFrom +
                    " callbackUrl:" + callbackUrl + " reportUrl:" + appInfo.getReportUrl() + "]");
            throw new ApiException(ErrorCode.E902.getCode() + "");
        }

        //4.拼接上报广告主的url
        String url = logService.generateReportAppUrl(form, appInfo, advertInfo, callbackUrl);

        //5.上报记录入库
        ReportLog log = new ReportLog();
        log.setIdfa(uniqueIdDto.getUniqueId());
        log.setIdType(uniqueIdDto.getUniqueType().getType());
        log.setAppCode(appCode);
        log.setAdverterCode(advertCode);
        log.setCallback(form.getCallback());
        log.setIsCpcReport(0);
        log.setReportTime(new Date());

        int i = logService.insert(log);

        long middle = new Date().getTime();

        if (i < appInfo.getIsRepeatable()) {
            throw new ApiException(ErrorCode.E701.getCode() + "");
        }

        //6.转发请求给广告主
        Cat.logEvent("click_idfa", advertCode);
        String resStr = HttpClientUtil.httpGet(url, appCode);
        if (Constants.HTTP_RSP_FAIL.equals(resStr)) {
            logger.error("report error:[" + "url:" + url + "]");
            throw new ApiException(ErrorCode.E901.getCode() + "");
        }

        logger.info("Forwarding request:[" + " resStr:" + resStr + ",url:" + url + "]");
        //7.是否复制流量并产生cpc
        if (advertInfoService.checkCpcCircut(advertInfo)) {
            logService.copyReportReq(appInfo, advertInfo);
        }

        logger.info("appCode:" + appCode + ",总耗时:" + (new Date().getTime() - start) + "ms,入库耗时:" + (middle - start) + "ms");
        return new BaseResult(ErrorCode.E200);
    }
}
