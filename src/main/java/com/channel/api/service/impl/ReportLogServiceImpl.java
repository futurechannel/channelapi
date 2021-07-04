package com.channel.api.service.impl;

import cn.hutool.core.util.IdUtil;
import com.channel.api.constants.ConstantMaps;
import com.channel.api.constants.Constants;
import com.channel.api.dao.ReportLogDao;
import com.channel.api.dto.BaseResult;
import com.channel.api.dto.UniqueIdDto;
import com.channel.api.entity.AdvertInfo;
import com.channel.api.entity.AppInfo;
import com.channel.api.entity.ReportLog;
import com.channel.api.enums.ErrorCode;
import com.channel.api.enums.UniqueType;
import com.channel.api.exception.ApiException;
import com.channel.api.form.ReportLogForm;
import com.channel.api.service.ReportLogService;

import com.channel.api.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * Created by gq on 2018/4/13.
 * 上报
 */
@Service
public class ReportLogServiceImpl implements ReportLogService {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private ReportLogDao reportLogDao;

    @Override
    public int insert(ReportLog log) {
        String tableName = ConstantMaps.getReportTableName();

        int count;
        try {
            count = reportLogDao.insert(log, tableName);
        } catch (DuplicateKeyException e) {
            return 0;
        } catch (Exception e) {
            LOG.error("保存失败:" + log.toString(), e);
            return -1;
        }
        return count;
    }

    /**
     * 查最近两天的上报记录
     *
     * @param idfa
     * @param appcode
     * @return
     */
    @Override
    public ReportLog findById(String idfa, String appcode) {

        List<String> tableNames = ConstantMaps.getReportTableNames(appcode);
        ReportLog log = null;

        if (CollectionUtils.isEmpty(tableNames)) {
            return log;
        }

        for (String tableName : tableNames) {
            try {
                log = reportLogDao.findById(idfa, appcode, tableName);
            } catch (Exception e) {
                LOG.error("查询异常table:" + tableName, e);
                continue;
            }
            if (log != null) {
                return log;
            }
        }

        return log;
    }


    @Override
    public UniqueIdDto getUniqueId(ReportLogForm params) {
        UniqueIdDto uniqueIdDto = new UniqueIdDto();
        if (!StringUtils.isEmpty(params.getIdfa())) {
            uniqueIdDto.setUniqueId(params.getIdfa());
            uniqueIdDto.setUniqueType(UniqueType.IDFA);
        } else if (!StringUtils.isEmpty(params.getCaid())) {
            uniqueIdDto.setUniqueId(params.getCaid());
            uniqueIdDto.setUniqueType(UniqueType.CAID);
        } else if (!StringUtils.isEmpty(params.getIp()) && !StringUtils.isEmpty(params.getUserAgent())) {
            uniqueIdDto.setUniqueId(IdUtil.simpleUUID());
            uniqueIdDto.setUniqueType(UniqueType.IP_UA);
        } else {
            throw new ApiException(ErrorCode.E902.getCode() + "");
        }

        return uniqueIdDto;
    }

    @Override
    public String generateReportAppUrl(ReportLogForm logForm, AppInfo appInfo, AdvertInfo advertInfo, String callbackUrl) {
        String idfa = logForm.getIdfa();
        String[] values = new String[10];
        values[0] = StringFormatUtils.formatNull(idfa);
        values[1] = StringFormatUtils.formatNull(advertInfo.getComeFrom());
        values[2] = StringFormatUtils.formatNull(callbackUrl);
        values[3] = StringFormatUtils.formatNull(logForm.getCaid());
        try {
            values[4] = URLEncoder.encode(StringFormatUtils.formatNull(logForm.getUserAgent()), "utf-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("encode 转码错误", e);
            throw new ApiException(ErrorCode.E906.getCode() + "");
        }
        values[5] = StringFormatUtils.formatNull(logForm.getIp());
        values[6] = StringFormatUtils.formatNull(logForm.getModel());
        values[7] = StringFormatUtils.formatNull(logForm.getClick_time());
        values[8] = StringFormatUtils.formatNull(logForm.getMac());

        String url = StringFormatUtils.format(appInfo.getReportUrl(), values);

        String otherParams = appInfo.getOtherParams();

        if (!StringUtils.isEmpty(otherParams)) {
            Map<String, Object> logFormMap = BeanUtil.transBean2Map(logForm);
            Map<String, String> otherParamMap = StringFormatUtils.string2Map(otherParams);
            StringBuilder sb = new StringBuilder();
            for (String key : otherParamMap.keySet()) {
                sb.append("&").append(key).append("=");
                Object obj = logFormMap.get(otherParamMap.get(key));
                if (!StringUtils.isEmpty(obj)) {
                    sb.append(obj);
                } else {
                    LOG.error("req error:[ref:" + advertInfo.getAdverterCode() + ",appCode:" + appInfo.getAppCode() + ","
                            + otherParamMap.get(key) + " is null]");
                    throw new ApiException(ErrorCode.E902.getCode() + "");
                }
            }

            url = url + sb.toString();
        }

        if (StringUtils.isEmpty(idfa)) {
            return url;
        }

        String token = appInfo.getToken();
        if ("sign".equals(token)) {
            url = url + "&" + token + "=" + Md5.Md5(advertInfo.getComeFrom() + idfa + Constants.JZFENHUO_GAMEID + Constants.JZFENHUO_SIGNKEY).toUpperCase();
        } else if ("accessToken".equals(token)) {
            url = url + "&" + token + "=" + Md5.Md5(advertInfo.getComeFrom() + idfa + "pa20191113");
        } else if ("opensysparams".equals(token)) {
            TreeMap<String, Object> params = new TreeMap<>();
            String opensysparams;
            try {
                params.put("idfa", idfa);
                params.put("ip", logForm.getIp());
                params.put("company_name", advertInfo.getComeFrom());
                params.put("appid", appInfo.getAppId());
                params.put("callbackurl", URLDecoder.decode(callbackUrl, "utf-8"));
                params.put("client_id", "e4OFL9l6Tposocm0");
                params.put("action", "youku.api.idfa.notification.click");
                String sign = YouKuParamsUtil.get_sign(params, "knlklmkjcilmepgaidhmpfcfjdppinlj");
                opensysparams = URLEncoder.encode(YouKuParamsUtil.opensysparams(params, sign), "utf-8");
            } catch (Exception e) {
                LOG.error("encode error", e);
                throw new ApiException(ErrorCode.E902.getCode() + "");
            }

            url = url + "&opensysparams=" + opensysparams;
        }
        return url;
    }

    @Override
    public void copyReportReq(AppInfo appInfo, AdvertInfo advertInfo) {
        try {
            String cpcUrl = ConfigUtils.getValue("cpc.report.url") + "appCode=" + appInfo.getAppCode()
                    + "&advertCode=" + advertInfo.getAdverterCode() + "&cpcNum=" +
                    advertInfo.getCpcNum() + "&reportUrl=" + URLEncoder.encode(appInfo.getReportUrl(), "utf-8") +
                    "&from=" + advertInfo.getComeFrom();

            if (!StringUtils.isEmpty(appInfo.getOtherParams())) {
                cpcUrl = cpcUrl + "&otherParams=" + URLEncoder.encode(appInfo.getOtherParams(), "utf-8");
            }

            if (!StringUtils.isEmpty(appInfo.getToken())) {
                cpcUrl = cpcUrl + "&token=" + appInfo.getToken();
            }

            if (!StringUtils.isEmpty(advertInfo.getOurCallBackUrl())) {
                cpcUrl = cpcUrl + "&ourCallBackUrl=" + URLEncoder.encode(advertInfo.getOurCallBackUrl()
                        + Constants.OUR_CALL_BACK_POSTFIX, "utf-8");
            }

            String cpcResStr = HttpClientUtil.httpGet(cpcUrl);
            if (!StringUtils.isEmpty(cpcResStr) && !Constants.HTTP_RSP_FAIL.equals(cpcResStr)) {
                BaseResult baseResult = GsonUtils.jsonToPojo(cpcResStr, BaseResult.class);
                if (baseResult.getCode() == 200) {
                    LOG.info("cpc report success,cpcUrl:{},res:{}", cpcUrl, cpcResStr);
                } else {
                    LOG.error("cpc report error,cpcUrl:{},res:{}", cpcUrl, cpcResStr);
                }
            } else {
                LOG.error("report error:[" + "cpcUrl:" + cpcUrl + "]");
            }

        } catch (Exception e) {
            LOG.error("send cpc fail", e);
        }
    }


}
