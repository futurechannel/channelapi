package com.channel.api.service;

import cn.hutool.core.util.IdUtil;
import com.channel.api.constants.ConstantMaps;
import com.channel.api.dao.ReportLogDao;
import com.channel.api.entity.ReportLog;
import com.channel.api.service.impl.ReportLogServiceImpl;
import com.channel.api.util.NumUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.URLDecoder;

import static org.junit.Assert.*;

/**
 * Created by gq on 2018/4/13.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ConstantMaps.class)
public class ReportLogServiceTest {
    @InjectMocks
    private ReportLogServiceImpl reportLogService;
    @Mock
    private ReportLogDao reportLogDao;

    @Test
    public void getUniqueId() throws Exception {
        System.out.println(IdUtil.simpleUUID());
        System.out.println(URLDecoder.decode("http%3A%2F%2Fapi.stonggo.com%2Fchannelapi%2Fcallback%2Fapp%3Fidfa%3D000A4778-C96A-409D-AE35-3B4E719F7AB2%26appcode%3Dyk1%26type%3D1","UTF-8"));
    }


    @Test
    public void insert(){
        PowerMockito.mockStatic(ConstantMaps.class);
        PowerMockito.when(ConstantMaps.getReportTableName()).thenReturn("abc");
        ReportLog reportLog =new ReportLog();
        PowerMockito.when(reportLogDao.insert(reportLog,"abc")).thenReturn(5);

        System.out.println(reportLogService.insert(reportLog));

    }

}