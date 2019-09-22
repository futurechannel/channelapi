package com.channel.api.util;

import com.mifmif.common.regex.Generex;

public class IdfaUtils {

    public static String generateIdfa(){
        Generex generex = new Generex("([0-9A-F]{8})([-][0-9A-F]{4})([-][4][0-9A-F]{3})([-][0-9A-F]{4})([-][0-9A-F]{12})");
        return generex.random();
    }

    public static void main(String[] args) {
        System.out.println(generateIdfa());
    }
}
