package com.channel.api.job;

import com.channel.api.constants.ConstantMaps;
import com.channel.api.dao.AdvertInfoDao;
import com.channel.api.dao.AppInfoDao;
import com.channel.api.entity.AdvertInfo;
import com.channel.api.entity.AppInfo;
import com.channel.api.util.ConfigUtils;
import com.channel.api.util.DateUtils;
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
    public void initConfig(){
        refreshConfig();
    }

    /**
     * 每隔5分钟更新一次配置
     */
    @Scheduled(cron = "0 0/5 * * * ? ")
    public void refreshConfig() {
        LOG.info("@Scheduled-----refreshConfig()");
        Map<String,AppInfo> tmpAppIdMap=new HashMap<>();
        Map<String,String> tmpAppCodeMap=new HashMap<>();

        Set<String> tmpAdvertSets=new HashSet<>();
        Map<String,Integer> tmpBalanceMap=new HashMap<>();
        Map<String,AdvertInfo> tmpAdvertInfoMap=new HashMap<>();


        List<String> tmpReportTables=new ArrayList<>();
        Date date=new Date();
        tmpReportTables.add(ConfigUtils.getValue("report.table.prefix")+ DateUtils.formatDate2Str(date,DateUtils.C_DATE_PATTON_YYYYMMDD));
        tmpReportTables.add(ConfigUtils.getValue("report.table.prefix")+DateUtils.defineDayBefore2Str(date,-1,DateUtils.C_DATE_PATTON_YYYYMMDD));
        ConstantMaps.setReportTables(tmpReportTables);


        List<AppInfo> appInfos=appInfoDao.findAll();
        List<AdvertInfo> advertInfos=advertInfoDao.findAll();

        for(AppInfo appInfo:appInfos){
            tmpAppCodeMap.put(appInfo.getAppCode(),appInfo.getAppId());
            tmpAppIdMap.put(appInfo.getAppId(),appInfo);
        }

        for(AdvertInfo advertInfo:advertInfos){
            tmpAdvertSets.add(advertInfo.getAdverterCode());
            tmpBalanceMap.put(ConstantMaps.getBalanceKey(advertInfo.getAppCode(),advertInfo.getAdverterCode()),advertInfo.getBalanceRatio());
            tmpAdvertInfoMap.put(ConstantMaps.getBalanceKey(advertInfo.getAppCode(),advertInfo.getAdverterCode()),advertInfo);
        }

        if(!CollectionUtils.isEmpty(tmpAppCodeMap)&&!CollectionUtils.isEmpty(tmpAppIdMap)){
            ConstantMaps.setAppCodeMap(tmpAppCodeMap);
            ConstantMaps.setAppIdMap(tmpAppIdMap);
        }

        if(!CollectionUtils.isEmpty(tmpAdvertSets)&&!CollectionUtils.isEmpty(tmpBalanceMap)){
            ConstantMaps.setAdvertSets(tmpAdvertSets);
            ConstantMaps.setBalanceMap(tmpBalanceMap);
            ConstantMaps.setAdvertInfoMap(tmpAdvertInfoMap);
        }

    }
}
