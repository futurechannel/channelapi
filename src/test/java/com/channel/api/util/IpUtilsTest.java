package com.channel.api.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:config/spring/spring.xml")
public class IpUtilsTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void getValidIps() {
        System.out.println(IpUtils.getValidIps(10000));
    }
}