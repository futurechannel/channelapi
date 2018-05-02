package com.channel.api.web;

import com.channel.api.base.BaseController;
import com.channel.api.constants.ConstantMaps;
import com.channel.api.dto.BaseResult;
import com.channel.api.entity.AdvertInfo;
import com.channel.api.entity.AppInfo;
import com.channel.api.entity.ReportLog;
import com.channel.api.enums.ErrorCode;
import com.channel.api.exception.ApiException;
import com.channel.api.form.ReportLogForm;
import com.channel.api.service.ReportLogService;
import com.channel.api.util.ConfigUtils;
import com.channel.api.util.GsonUtils;
import com.channel.api.util.HttpClientUtil;
import com.channel.api.util.StringFormatUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;
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

        AppInfo appInfo= ConstantMaps.getAppCode(form.getAppid());

        String advertCode=form.getRef();

        if(appInfo==null){
            throw new ApiException(ErrorCode.E601.getCode()+"");
        }

        if(!ConstantMaps.advertSets.contains(advertCode)){
            throw new ApiException(ErrorCode.E602.getCode()+"");
        }

        String appCode=appInfo.getAppCode();

        AdvertInfo advertInfo=ConstantMaps.getAdvertInfo(appCode,advertCode);

        if(advertInfo==null){
            throw new ApiException(ErrorCode.E603.getCode()+"");
        }

        String from = advertInfo.getComeFrom();

        String callback ;

        try {
            callback = URLEncoder.encode(ConfigUtils.getValue("our.callback.url")
                    + "idfa=" + idfa + URL_PARAM_SEPARATOR + "appcode="+ appCode , "utf-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("encode 转码错误",e);
            throw new ApiException(ErrorCode.E901.getCode()+"");
        }

        long start=new Date().getTime();

        String reportUrl=appInfo.getReportUrl();

        //转发请求给应用
        if ( !StringUtils.isEmpty(idfa) && !StringUtils.isEmpty(from) && !StringUtils.isEmpty(callback) && !StringUtils.isEmpty(appCode)&&!StringUtils.isEmpty(reportUrl)){
//            String url = appInfo.getReportUrl()
//                    + "idfa=" + idfa + URL_PARAM_SEPARATOR + "from=" + from + URL_PARAM_SEPARATOR + "callback=" + callback + URL_PARAM_SEPARATOR +  "pos=0";
            String url= StringFormatUtils.format(reportUrl,idfa,from,callback);

            String resStr=HttpClientUtil.httpGet(url);
            if(StringUtils.isEmpty(resStr)){
                logger.error("report error:[" + "url:" + url + "]");
                throw new ApiException(ErrorCode.E901.getCode()+"");
            }
//            MangguoDto mangguoDto=GsonUtils.jsonToPojo(resStr,MangguoDto.class);
//            if(!mangguoDto.getRet().equals("0")){
//                logger.error("report error:[" + "url:" + url + "]");
//                throw new ApiException(ErrorCode.E901.getCode()+"");
//            }
            logger.info("Forwarding request:[" + " resStr:" + resStr +"url:"+url+"]");
        }else {
            logger.error("Forwarding request param error:[" + "idfa:" + idfa + " from:" + from + " callback:" + callback + " reportUrl:" + reportUrl +"]");
            throw new ApiException(ErrorCode.E902.getCode()+"");
        }


        //上报记录入库
        ReportLog log=new ReportLog();
        log.setIdfa(idfa);
        log.setAppCode(appCode);

        log.setAdverterCode(advertCode);
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
