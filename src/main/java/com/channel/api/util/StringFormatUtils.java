package com.channel.api.util;

import java.text.MessageFormat;


/**
 * Created by gq on 2018/5/1.
 */
public class StringFormatUtils {

    public static String format(String str,String ... obj){

        String result=MessageFormat.format(str,obj);
        return result;
    }

    public static void main(String[] args) {

        System.out.println(format("sadsa={0}&sacs={1}", "wre","cdg"));
    }
}
