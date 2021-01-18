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
import com.dianping.cat.Cat;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;


/**
 * Created by gq on 2018/4/13.
 * 上报
 */
@Controller
@RequestMapping("/click")
public class ReportLogController extends BaseController {

    public static final String URL_PARAM_SEPARATOR = "&";
    public static final String OUR_CALL_BACK_POSTFIX = "/channelapi/callback/app?";


    @Resource
    private ReportLogService logService;

    @RequestMapping("/idfa")
    @ResponseBody
    public BaseResult upReport(@Valid ReportLogForm form) {


        logger.info("req:[" + GsonUtils.pojoToJson(form) + "]");

        String idfa = form.getIdfa();

        AppInfo appInfo;

        if (!StringUtils.isEmpty(form.getAppcode())) {
            appInfo = ConstantMaps.getAppInfoByCode(form.getAppcode());
        } else {
            appInfo = ConstantMaps.getAppCode(form.getAppid());
        }


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
        String ourCallBackUrl = advertInfo.getOurCallBackUrl();

        String callback;

        try {
            if (StringUtils.isEmpty(ourCallBackUrl)) {
                callback = URLEncoder.encode(ConfigUtils.getValue("our.callback.url")
                        + "idfa=" + idfa + URL_PARAM_SEPARATOR + "appcode=" + appCode, "utf-8");
            } else {
                callback = URLEncoder.encode(ourCallBackUrl + OUR_CALL_BACK_POSTFIX
                        + "idfa=" + idfa + URL_PARAM_SEPARATOR + "appcode=" + appCode, "utf-8");
            }
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

        String otherParams = appInfo.getOtherParams();

        if (!StringUtils.isEmpty(otherParams)) {
            Map<String, Object> logFormMap = BeanUtil.transBean2Map(form);
            Map<String, String> otherParamMap = StringFormatUtils.string2Map(otherParams);
            StringBuilder sb = new StringBuilder();
            for (String key : otherParamMap.keySet()) {
                sb.append("&").append(key).append("=");
                Object obj = logFormMap.get(otherParamMap.get(key));
                if (!StringUtils.isEmpty(obj)) {
                    sb.append(obj);
                } else {
                    logger.error("req error:[ref:" + advertCode + ",appCode:" + appCode + "," + otherParamMap.get(key) + " is null]");
                    throw new ApiException(ErrorCode.E902.getCode() + "");
                }
            }

            url = url + sb.toString();
        }

        String token = appInfo.getToken();
        if ("sign".equals(token)) {
            url = url + "&" + token + "=" + Md5.Md5(from + idfa + Constants.JZFENHUO_GAMEID + Constants.JZFENHUO_SIGNKEY).toUpperCase();
        } else if ("accessToken".equals(token)) {
            url = url + "&" + token + "=" + Md5.Md5(from + idfa + "pa20191113");
        } else if ("opensysparams".equals(token)) {
            TreeMap<String, Object> params = new TreeMap<>();
            String opensysparams;
            try {
                params.put("idfa", idfa);
                params.put("ip", form.getIp());
                params.put("company_name", from);
                params.put("appid", appInfo.getAppId());
                params.put("callbackurl", URLDecoder.decode(callback, "utf-8"));
                params.put("client_id", "e4OFL9l6Tposocm0");
                params.put("action", "youku.api.idfa.notification.click");
                String sign = YouKuParamsUtil.get_sign(params, "knlklmkjcilmepgaidhmpfcfjdppinlj");
                opensysparams = URLEncoder.encode(YouKuParamsUtil.opensysparams(params, sign), "utf-8");
            } catch (Exception e) {
                logger.error("encode error", e);
                throw new ApiException(ErrorCode.E902.getCode() + "");
            }

            url = url + "&opensysparams=" + opensysparams;
        }

        //上报记录入库
        ReportLog log = new ReportLog();
        log.setIdfa(idfa);
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

        //转发请求给应用

        Cat.logEvent("click_idfa", advertCode);
        String resStr = HttpClientUtil.httpGet(url, appCode);
        if (Constants.HTTP_RSP_FAIL.equals(resStr)) {
            logger.error("report error:[" + "url:" + url + "]");
            throw new ApiException(ErrorCode.E901.getCode() + "");
        }

        logger.info("Forwarding request:[" + " resStr:" + resStr + ",url:" + url + "]");

        if (advertInfo.getCpcCircut() != null && advertInfo.getCpcCircut() == 1 && advertInfo.getCpcNum() != null) {
            try {
                String cpcUrl = ConfigUtils.getValue("cpc.report.url") + "appCode=" + appCode
                        + "&advertCode=" + advertCode + "&cpcNum=" +
                        advertInfo.getCpcNum() + "&reportUrl=" + URLEncoder.encode(reportUrl, "utf-8") +
                        "&from=" + advertInfo.getComeFrom();

                if (!StringUtils.isEmpty(otherParams)) {
                    cpcUrl = cpcUrl + "&otherParams=" + URLEncoder.encode(otherParams, "utf-8");
                }

                if (!StringUtils.isEmpty(token)) {
                    cpcUrl = cpcUrl + "&token=" + token;
                }

                if (!StringUtils.isEmpty(ourCallBackUrl)) {
                    cpcUrl = cpcUrl + "&ourCallBackUrl=" + URLEncoder.encode(ourCallBackUrl + OUR_CALL_BACK_POSTFIX, "utf-8");
                }

                String cpcResStr = HttpClientUtil.httpGet(cpcUrl);
                if (!StringUtils.isEmpty(cpcResStr) && !Constants.HTTP_RSP_FAIL.equals(cpcResStr)) {
                    BaseResult baseResult = GsonUtils.jsonToPojo(cpcResStr, BaseResult.class);
                    if (baseResult.getCode() == 200) {
                        logger.info("cpc report success,cpcUrl:{},res:{}", cpcUrl, cpcResStr);
                    } else {
                        logger.error("cpc report error,cpcUrl:{},res:{}", cpcUrl, cpcResStr);
                    }
                } else {
                    logger.error("report error:[" + "cpcUrl:" + cpcUrl + "]");
                }

            } catch (Exception e) {
                logger.error("send cpc fail", e);
            }

        }


        logger.info("appCode:" + appCode + ",总耗时:" + (new Date().getTime() - start) + "ms,入库耗时:" + (middle - start) + "ms");
        return new BaseResult(ErrorCode.E200);
    }
}
