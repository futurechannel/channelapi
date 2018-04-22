package com.channel.api.constants;


import com.channel.api.entity.AppInfo;
import com.channel.api.util.ConfigUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by gq on 2018/4/15.
 */
public class ConstantMaps {
    public static Map<String, String> appCodeMap = new HashMap<>();
    public static Map<String, AppInfo> appIdMap = new HashMap<>();

    public static Set<String> advertSets = new HashSet<>();

    public static Map<String, Integer> balanceMap = new HashMap<>();


    public static void setAppCodeMap(Map<String, String> appCodeMap) {
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

    public static String getAppId(String code) {
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


    public static String getBalanceKey(String appCode, String advertCode) {
        return advertCode + "-" + appCode;
    }
}
