package com.channel.api.constants;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by gq on 2018/4/15.
 */
public class ConstantMaps {
    public static Map<String, String> appCodeMap = new HashMap<>();
    public static Map<String, String> appIdMap = new HashMap<>();

    public static Set<String> advertSets=new HashSet<>();

    static {
        appCodeMap.put("manguo","629774477");

        appIdMap.put("629774477","manguo");

        advertSets.add("pp_001");
        advertSets.add("xy_001");
        advertSets.add("haima_001");
        advertSets.add("taken_001");
        advertSets.add("51bizhi_001");
        advertSets.add("aisi_001");
        advertSets.add("mopin_001");
        advertSets.add("23zhushou_001");
    }

    public static String getAppId(String code){
        if(appCodeMap.containsKey(code)){
            return appCodeMap.get(code);
        }
        return null;
    }

    public static String getAppCode(String appId){
        if(appIdMap.containsKey(appId)){
            return appIdMap.get(appId);
        }
        return null;
    }
}
