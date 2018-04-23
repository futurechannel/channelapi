package com.channel.api.web;

import com.channel.api.base.BaseController;
import com.channel.api.constants.ConstantMaps;
import com.channel.api.dto.BaseResult;
import com.channel.api.dto.MangguoDto;
import com.channel.api.entity.AppInfo;
import com.channel.api.entity.ReportLog;
import com.channel.api.enums.ErrorCode;
import com.channel.api.exception.ApiException;
import com.channel.api.form.ReportLogForm;
import com.channel.api.service.ReportLogService;
import com.channel.api.util.GsonUtils;
import com.channel.api.util.HttpClientUtil;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

        AppInfo appInfo= ConstantMaps.getAppCode(form.getAppid());

        if(appInfo==null){
            throw new ApiException(ErrorCode.E601.getCode()+"");
        }
        String appCode=appInfo.getAppCode();
        String from = appInfo.getComeFrom();

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

        long start=new Date().getTime();

        //转发请求给应用
        if ( !StringUtils.isEmpty(idfa) && !StringUtils.isEmpty(from) && !StringUtils.isEmpty(callback) && !StringUtils.isEmpty(appCode)){
            String url = appInfo.getReportUrl()
                    + "idfa=" + idfa + URL_PARAM_SEPARATOR + "from=" + from + URL_PARAM_SEPARATOR + "callback=" + callback + URL_PARAM_SEPARATOR +  "pos=0"
                    + ipParam
                    + uaParam;

            String resStr=HttpClientUtil.httpGet(url);
            if(StringUtils.isEmpty(resStr)){
                logger.error("report error:[" + "idfa:" + idfa + " from:" + from + " callback:" + callback + "]");
                throw new ApiException(ErrorCode.E901.getCode()+"");
            }
            MangguoDto mangguoDto=GsonUtils.jsonToPojo(resStr,MangguoDto.class);
            if(!mangguoDto.getRet().equals("0")){
                logger.error("report error:[" + "idfa:" + idfa + " from:" + from + " callback:" + callback + "]");
                throw new ApiException(ErrorCode.E901.getCode()+"");
            }
            logger.info("Forwarding request:[" + " resStr:" + resStr +"url:"+url+"]");
        }else {
            logger.error("Forwarding request param error:[" + "idfa:" + idfa + " from:" + from + " callback:" + callback + "]");
        }

//        logger.info("report耗时:"+(new Date().getTime()-start)+"ms");

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

        logger.info("总耗时:"+(new Date().getTime()-start)+"ms");
        return new BaseResult(ErrorCode.E200);
    }
}
