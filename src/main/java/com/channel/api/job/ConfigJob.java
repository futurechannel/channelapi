package com.channel.api.job;

import com.channel.api.constants.ConstantMaps;
import com.channel.api.dao.AdvertInfoDao;
import com.channel.api.dao.AppInfoDao;
import com.channel.api.dto.BaseResult;
import com.channel.api.entity.AdvertInfo;
import com.channel.api.entity.AppInfo;
import com.channel.api.enums.ErrorCode;
import com.channel.api.util.ConfigUtils;
import com.channel.api.util.DateUtils;
import com.channel.api.util.GsonUtils;
import com.channel.api.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by gq on 2018/4/21.
 */
@Component
public class ConfigJob {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AppInfoDao appInfoDao;
    @Autowired
    private AdvertInfoDao advertInfoDao;

    @PostConstruct
    public void initConfig() {
        refreshConfig();
        refreshCpc();

    }

    /**
     * 每隔5分钟更新一次配置
     */
    @Scheduled(cron = "0 0/5 * * * ? ")
    public void refreshConfig() {
        LOG.info("@Scheduled-----refreshConfig()");
        Map<String, AppInfo> tmpAppIdMap = new HashMap<>();
        Map<String, AppInfo> tmpAppCodeMap = new HashMap<>();

        Set<String> tmpAdvertSets = new HashSet<>();
        Map<String, Integer> tmpBalanceMap = new HashMap<>();
        Map<String, AdvertInfo> tmpAdvertInfoMap = new HashMap<>();


        Map<String, List<String>> tmpReportTables = new HashMap<>();
        Date date = new Date();
        List<String> tmpTables = new ArrayList<>();
        String tmpTable = ConfigUtils.getValue("report.table.prefix") + DateUtils.formatDate2Str(date, DateUtils.C_DATE_PATTON_YYYYMMDD);
        tmpTables.add(tmpTable);

        List<AppInfo> appInfos = appInfoDao.findAll();
        List<AdvertInfo> advertInfos = advertInfoDao.findAll();

        for (AppInfo appInfo : appInfos) {
            tmpAppCodeMap.put(appInfo.getAppCode(), appInfo);
            tmpAppIdMap.put(appInfo.getAppId(), appInfo);
            List<String> reports = new ArrayList<>();
            //最多查七张表
            int tableNums = appInfo.getQueryTableNum() <= 7 ? appInfo.getQueryTableNum() : 7;
            for (int i = 0; i < tableNums; i++) {
                if (i == 0) {
                    reports.add(tmpTable);
                } else {
                    reports.add(ConfigUtils.getValue("report.table.prefix") + DateUtils.defineDayBefore2Str(date, -i, DateUtils.C_DATE_PATTON_YYYYMMDD));
                }
            }

            tmpReportTables.put(appInfo.getAppCode(), reports);
        }

        for (AdvertInfo advertInfo : advertInfos) {
            tmpAdvertSets.add(advertInfo.getAdverterCode());
            tmpBalanceMap.put(ConstantMaps.getBalanceKey(advertInfo.getAppCode(), advertInfo.getAdverterCode()), advertInfo.getBalanceRatio());
            tmpAdvertInfoMap.put(ConstantMaps.getBalanceKey(advertInfo.getAppCode(), advertInfo.getAdverterCode()), advertInfo);
        }

        if (!CollectionUtils.isEmpty(tmpAppCodeMap) && !CollectionUtils.isEmpty(tmpAppIdMap)) {
            ConstantMaps.setAppCodeMap(tmpAppCodeMap);
            ConstantMaps.setAppIdMap(tmpAppIdMap);
            ConstantMaps.setReportTables(tmpReportTables);
        }
        ConstantMaps.setReportTableName(tmpTables);


        if (!CollectionUtils.isEmpty(tmpAdvertSets) && !CollectionUtils.isEmpty(tmpBalanceMap)) {
            ConstantMaps.setAdvertSets(tmpAdvertSets);
            ConstantMaps.setBalanceMap(tmpBalanceMap);
            ConstantMaps.setAdvertInfoMap(tmpAdvertInfoMap);
        }

    }


    /**
     * 每隔5分钟更新一次配置
     */
    @Scheduled(cron = "0 0/5 * * * ? ")
    public void refreshCpc() {
        LOG.info("@Scheduled-----refreshCpc() start");

        List<AdvertInfo> advertInfos = advertInfoDao.findAll();
        for (AdvertInfo advertInfo : advertInfos) {

            if (advertInfo.getCpcNum() == null || advertInfo.getCpcTime() == null || advertInfo.getCpcCircut() == null || advertInfo.getCpcCircut() != 1) {
                String url = ConfigUtils.getValue("stop.cpc.url")+"appCode="+advertInfo.getAppCode()
                        +"&advertCode="+advertInfo.getAdverterCode();
                try {
                    String resStr = HttpClientUtil.httpGet(url);
                    BaseResult baseResult=GsonUtils.jsonToPojo(resStr, BaseResult.class);
                    if( ErrorCode.E905.getCode() != baseResult.getCode() ) {
                        LOG.info("call stop cpc res:{}", resStr);
                    }
                } catch (Exception e) {
                    LOG.error("call stop cpc fail", e);
                }
            } else {
                String url = ConfigUtils.getValue("start.cpc.url")+"appCode="+advertInfo.getAppCode()
                        +"&advertCode="+advertInfo.getAdverterCode()+"&cpcNum="+advertInfo.getCpcNum()+"&cpcTime="+advertInfo.getCpcTime();
                try {
                    String resStr = HttpClientUtil.httpGet(url);
                    BaseResult baseResult=GsonUtils.jsonToPojo(resStr, BaseResult.class);
                    if(ErrorCode.E903.getCode()!=baseResult.getCode()) {
                        LOG.info("call start cpc res:{}", resStr);
                    }
                } catch (Exception e) {
                    LOG.error("call start cpc fail", e);
                }
            }
        }


        LOG.info("@Scheduled-----refreshCpc() end");

    }


}
