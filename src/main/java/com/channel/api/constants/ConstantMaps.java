package com.channel.api.constants;


import com.channel.api.entity.AdvertInfo;
import com.channel.api.entity.AppInfo;
import com.channel.api.util.ConfigUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by gq on 2018/4/15.
 */
public class ConstantMaps {
    public static Map<String, AppInfo> appCodeMap = new HashMap<>();
    public static Map<String, AppInfo> appIdMap = new HashMap<>();

    public static Set<String> advertSets = new HashSet<>();

    public static Map<String, Integer> balanceMap = new HashMap<>();

    public static Map<String,AdvertInfo> advertInfoMap=new HashMap<>();

    public static Map<String,List<String>> reportTables=new HashMap<>();

    private static List<String> reportTableName=new ArrayList<>();

    public static void setReportTables(Map<String,List<String>> reportTables) {
        ConstantMaps.reportTables = reportTables;
    }

    public static void setAppCodeMap(Map<String, AppInfo> appCodeMap) {
        ConstantMaps.appCodeMap = appCodeMap;
    }

    public static void setAppIdMap(Map<String, AppInfo> appIdMap) {
        ConstantMaps.appIdMap = appIdMap;
    }

    public static void setAdvertSets(Set<String> advertSets) {
        ConstantMaps.advertSets = advertSets;
    }

    public static void setBalanceMap(Map<String, Integer> balanceMap) {
        ConstantMaps.balanceMap = balanceMap;
    }

    public static void setAdvertInfoMap(Map<String, AdvertInfo> advertInfoMap) {
        ConstantMaps.advertInfoMap = advertInfoMap;
    }

    public static AppInfo getAppInfoByCode(String code) {
        if (appCodeMap.containsKey(code)) {
            return appCodeMap.get(code);
        }
        return null;
    }

    public static AppInfo getAppCode(String appId) {
        if (appIdMap.containsKey(appId)) {
            return appIdMap.get(appId);
        }
        return null;
    }

    public static int getBalanceRatio(String appCode, String advertCode) {
        if (StringUtils.isEmpty(appCode) || StringUtils.isEmpty(advertCode)) {
            return Integer.parseInt(ConfigUtils.getValue("default.reduce.per"));
        }

        String key = getBalanceKey(appCode, advertCode);

        Integer balance=balanceMap.get(key);

        return balance!=null?balance:Integer.parseInt(ConfigUtils.getValue("default.reduce.per"));
    }

    public static AdvertInfo getAdvertInfo(String appCode, String advertCode){
        String key = getBalanceKey(appCode, advertCode);
        return advertInfoMap.get(key);
    }


    public static String getBalanceKey(String appCode, String advertCode) {
        return advertCode + "-" + appCode;
    }

    public static String getReportTableName(){
        return reportTableName.get(0);
    }

    public static void setReportTableName(List<String> reportTableNames) {
        ConstantMaps.reportTableName = reportTableNames;
    }

    public static List<String> getReportTableNames(String appCode){
        return reportTables.get(appCode);
    }
}
