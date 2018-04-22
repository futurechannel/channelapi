package com.channel.api.web;

import com.channel.api.base.BaseController;
import com.channel.api.constants.ConstantMaps;
import com.channel.api.dto.BaseResult;
import com.channel.api.entity.ReportLog;
import com.channel.api.enums.ErrorCode;
import com.channel.api.exception.ApiException;
import com.channel.api.form.ReportLogForm;
import com.channel.api.handler.CallBackHandler;
import com.channel.api.service.ReportLogService;
import com.channel.api.util.ConfigUtils;
import com.channel.api.util.GsonUtils;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;


/**
 * Created by gq on 2018/4/13.
 *
 */
@Controller
@RequestMapping("/click")
public class ReportLogController extends BaseController{

    public static final String URL_PARAM_SEPARATOR="&";

    @Autowired
    private CallBackHandler handler;

    @Autowired
    private RestTemplate template;


    @Resource
    private ReportLogService logService;

    @RequestMapping("/idfa")
    @ResponseBody
    public BaseResult upReport(@Valid ReportLogForm form){

        logger.info("req:["+GsonUtils.pojoToJson(form)+"]");

        String idfa = form.getIdfa();
        String pos = form.getClickid();
        String ip = form.getIp();
        String ua = form.getUserAgent();
        String appCode= ConstantMaps.getAppCode(form.getAppid());
        if(StringUtils.isEmpty(appCode)){
            throw new ApiException(ErrorCode.E601.getCode()+"");
        }
        String from = ConfigUtils.getValueNe("from");
        String callback = null;

        String posParam = "";
        String ipParam = "";
        String uaParam = "";
        if (!StringUtils.isEmpty(pos)){
            posParam = URL_PARAM_SEPARATOR + "pos=" + pos;
        }
        if (!StringUtils.isEmpty(ip)){
            ipParam = URL_PARAM_SEPARATOR + "ip=" + ip;
        }
        if (!StringUtils.isEmpty(ua)){
            uaParam = URL_PARAM_SEPARATOR + "ua=" + ua;
        }
        try {
            callback = URLEncoder.encode("http://60.205.231.1:8080/channelapi/callback/app?"
                    + "idfa=" + idfa + URL_PARAM_SEPARATOR + "appcode="+ appCode + posParam, "utf-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("encode 转码错误",e);
        }

        //转发请求给应用
        if ( !StringUtils.isEmpty(idfa) && !StringUtils.isEmpty(from) && !StringUtils.isEmpty(callback)){
            String url = ConfigUtils.getValueNe(appCode)
                    + "idfa=" + idfa + URL_PARAM_SEPARATOR + "from=" + from + URL_PARAM_SEPARATOR + "callback=" + callback
                    + posParam
                    + ipParam
                    + uaParam;

            logger.info("Forwarding request url:" + url);

            HttpClient httpClient = new HttpClient();
            GetMethod method = new GetMethod(url);
            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 5);

            int resCode=-1;
            String resStr=null;
            try {
                 resCode = httpClient.executeMethod(method);
                 resStr = method.getResponseBodyAsString();
                logger.info("Forwarding request:[" + "resCode:"+ resCode + " resStr:" + resStr +"]");
            } catch (IOException e) {
                logger.error("上报应用异常,resCode:"+resCode+"resStr:"+resStr,e);
            }
        }else {
            logger.info("Forwarding request param error:[" + "idfa:" + idfa + " from:" + from + " callback:" + callback + "]");
        }

        //上报记录入库
        ReportLog log=new ReportLog();
        log.setIdfa(idfa);
        log.setAppCode(appCode);
        if(!ConstantMaps.advertSets.contains(form.getRef())){
            throw new ApiException(ErrorCode.E602.getCode()+"");
        }
        log.setAdverterCode(form.getRef());
        log.setCallback(form.getCallback());
        log.setReportTime(new Date());

        int i=logService.insert(log);

        if(i<0){
            throw new ApiException(ErrorCode.E500.getCode()+"");
        }

        return new BaseResult(ErrorCode.E200);
    }
}
