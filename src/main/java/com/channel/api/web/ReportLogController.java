package com.channel.api.web;

import com.channel.api.base.BaseController;
import com.channel.api.constants.ConstantMaps;
import com.channel.api.constants.Constants;
import com.channel.api.dto.BaseResult;
import com.channel.api.entity.AdvertInfo;
import com.channel.api.entity.AppInfo;
import com.channel.api.entity.ReportLog;
import com.channel.api.enums.ErrorCode;
import com.channel.api.exception.ApiException;
import com.channel.api.form.ReportLogForm;
import com.channel.api.service.ReportLogService;
import com.channel.api.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;


/**
 * Created by gq on 2018/4/13.
 * 上报
 */
@Controller
@RequestMapping("/click")
public class ReportLogController extends BaseController {

    public static final String URL_PARAM_SEPARATOR = "&";


    @Resource
    private ReportLogService logService;

    @RequestMapping("/idfa")
    @ResponseBody
    public BaseResult upReport(@Valid ReportLogForm form) {


        logger.info("req:[" + GsonUtils.pojoToJson(form) + "]");

        String idfa = form.getIdfa();

        AppInfo appInfo = ConstantMaps.getAppCode(form.getAppid());

        String advertCode = form.getRef();

        if (appInfo == null) {
            throw new ApiException(ErrorCode.E601.getCode() + "");
        }

        if (!ConstantMaps.advertSets.contains(advertCode)) {
            throw new ApiException(ErrorCode.E602.getCode() + "");
        }

        String appCode = appInfo.getAppCode();

        AdvertInfo advertInfo = ConstantMaps.getAdvertInfo(appCode, advertCode);

        if (advertInfo == null) {
            throw new ApiException(ErrorCode.E603.getCode() + "");
        }

        String from = advertInfo.getComeFrom();

        String callback;

        try {
            callback = URLEncoder.encode(ConfigUtils.getValue("our.callback.url")
                    + "idfa=" + idfa + URL_PARAM_SEPARATOR + "appcode=" + appCode, "utf-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("encode 转码错误", e);
            throw new ApiException(ErrorCode.E901.getCode() + "");
        }

        long start = new Date().getTime();

        String reportUrl = appInfo.getReportUrl();

        if (StringUtils.isEmpty(idfa) || StringUtils.isEmpty(from) || StringUtils.isEmpty(callback) || StringUtils.isEmpty(appCode) || StringUtils.isEmpty(reportUrl)) {
            logger.error("Forwarding request param error:[" + "idfa:" + idfa + " from:" + from + " callback:" + callback + " reportUrl:" + reportUrl + "]");
            throw new ApiException(ErrorCode.E902.getCode() + "");
        }

        //拼接url
        String url = StringFormatUtils.format(reportUrl, idfa, from, callback);

        String otherParams=appInfo.getOtherParams();

        if(!StringUtils.isEmpty(otherParams)){
            Map<String,Object> logFormMap= BeanUtil.transBean2Map(form);
            Map<String,String> otherParamMap=StringFormatUtils.string2Map(otherParams);
            StringBuilder sb = new StringBuilder();
            for(String key:otherParamMap.keySet()){
                sb.append("&").append(key).append("=");
                Object obj=logFormMap.get(otherParamMap.get(key));
                if(!StringUtils.isEmpty(obj)){
                    sb.append(obj);
                } else {
                    logger.error("req error:[ref:"+advertCode+",appCode:"+appCode+","+otherParamMap.get(key)+" is null]");
                    throw new ApiException(ErrorCode.E902.getCode()+"");
                }
            }

            url = url+sb.toString();
        }

        String token=appInfo.getToken();
        if (!StringUtils.isEmpty(token)) {
            url=url+"&"+token+"="+Md5.Md5(from+idfa+ Constants.JZFENHUO_GAMEID+Constants.JZFENHUO_SIGNKEY).toUpperCase();
        }

        //上报记录入库
        ReportLog log = new ReportLog();
        log.setIdfa(idfa);
        log.setAppCode(appCode);

        log.setAdverterCode(advertCode);
        log.setCallback(form.getCallback());
        log.setReportTime(new Date());

        int i = logService.insert(log);

        long middle = new Date().getTime();

        if (i < appInfo.getIsRepeatable()) {
            throw new ApiException(ErrorCode.E701.getCode() + "");
        }

        //转发请求给应用

        String resStr = HttpClientUtil.httpGet(url);
        if (StringUtils.isEmpty(resStr)) {
            logger.error("report error:[" + "url:" + url + "]");
            throw new ApiException(ErrorCode.E901.getCode() + "");
        }

        logger.info("Forwarding request:[" + " resStr:" + resStr + "url:" + url + "]");


        logger.info("appCode:"+appCode+",总耗时:" + (new Date().getTime() - start) + "ms,入库耗时:"+(middle - start)+"ms");
        return new BaseResult(ErrorCode.E200);
    }
}
