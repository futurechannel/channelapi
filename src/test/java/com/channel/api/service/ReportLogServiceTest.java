package com.channel.api.service;

import cn.hutool.core.util.IdUtil;
import org.junit.Test;

import java.net.URLDecoder;

import static org.junit.Assert.*;

/**
 * Created by gq on 2018/4/13.
 */
public class ReportLogServiceTest {
    @Test
    public void getUniqueId() throws Exception {
        System.out.println(IdUtil.simpleUUID());
        System.out.println(URLDecoder.decode("http%3A%2F%2Fapi.stonggo.com%2Fchannelapi%2Fcallback%2Fapp%3Fidfa%3D000A4778-C96A-409D-AE35-3B4E719F7AB2%26appcode%3Dyk1%26type%3D1","UTF-8"));
    }

}