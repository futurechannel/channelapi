package com.channel.api.util;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.*;


/**
 * Created by gq on 2018/5/1.
 */
public class StringFormatUtils {

    public static String format(String str, String... obj) {

        String result = MessageFormat.format(str, obj);
        return result;
    }

    public static Map<String, String> string2Map(String str) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isEmpty(str)) {
            return map;
        }

        String[] strArrs = str.split(",");
        for (String strArr : strArrs) {
            String[] items = strArr.split(":");
            if (items.length > 1) {
                map.put(items[0], items[1]);
            }
        }

        return map;
    }

    public static String formatNull(String str) {
        return StringUtils.isEmpty(str) ? "" : str;
    }


    public static void main(String[] args) {
        String[] str = new String[3];
        str[0] = "0";
        str[1] = "";
        str[2] = "2";

        System.out.println(format("sadsa={0}&sadsb={1}&sacs={2}", str));
    }
}
