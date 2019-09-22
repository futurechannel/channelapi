package com.channel.api.util;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import com.google.common.io.Resources;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IpUtils {
    private static final int IP_LENGTH = 3;
    private static final String IP_RANGE_SEP = "-";
    private static final Logger logger = LoggerFactory.getLogger(IpUtils.class);

    //ip字符串转long
    public static long ipToLong(String ipStr) {
        String[] ipArray = ipStr.split("\\.");
        long ipLong = 0;
        for (int i = IP_LENGTH; i >= 0; i--) {
            long number = Long.parseLong(ipArray[3 - i]);
            // 192 << 24  | 168 << 16  | 1 << 8  | 2 << 0 等价于 192 * 256^3 + 168 * 256^2  + 1 * 256^1  + 2 * 256^0
            ipLong |= number << (i * 8);
        }
        return ipLong;
    }

    //long转ip字符串
    public static String longToIp(long ipLong) {
        String ipStr = ((ipLong >> 24) & 0xFF) + "."
                + ((ipLong >> 16) & 0xFF) + "."
                + ((ipLong >> 8) & 0xFF) + "."
                + (ipLong & 0xFF);
        return ipStr;
    }


    /**
     * 判断某个ip是否在ip数组之内
     * @param ip 需要检查ip
     * @param ipArray ip数组，可以是单个ip或者ip段
     * @param ipRangeSep ip段分隔符（默认为~）
     * @return
     */
    public static boolean isExistInIps(String ip, JSONArray ipArray, String ipRangeSep) {
        boolean isExist = false;
        ip = ip.trim();
        ipRangeSep = StringUtils.isEmpty(ipRangeSep) ? "~" : ipRangeSep;
        for (int i = 0; i < ipArray.size(); i++){
            String ipStr = ipArray.getString(i);
            if (ipStr.contains(ipRangeSep)){
                String[] ipRanges = ipStr.split(ipRangeSep);
                if (ipToLong(ip) >= ipToLong(ipRanges[0].trim()) && ipToLong(ip) <= ipToLong(ipRanges[1].trim())){
                    isExist = true;
                    break;
                }
            } else if (ip.equals(ipStr.trim())){
                isExist = true;
                break;
            }
        }
        return isExist;
    }

    /**
     * 根据白名单和黑名单随机生成符合条件ip
     * @param size
     * @return
     */
    public static List<String> getValidIps(int size){
        List<String> ipList = new ArrayList<>();
        File blackFile = new File(Resources.getResource("iplist/black.txt").getPath());
        File whiteFile = new File(Resources.getResource("iplist/white.txt").getPath());
        try (FileReader blackFr = new FileReader(blackFile); BufferedReader blackBf = new BufferedReader(blackFr);
             FileReader whiteFr = new FileReader(whiteFile); BufferedReader whiteBf = new BufferedReader(whiteFr)){
            String blackStr;
            String whiteStr;
            RangeSet<Long> whiteRangeSet = TreeRangeSet.create();
            while ((whiteStr = whiteBf.readLine()) != null) {
                Long start = ipToLong(whiteStr.split(IP_RANGE_SEP)[0]);
                Long end = ipToLong(whiteStr.split(IP_RANGE_SEP)[1]);
                whiteRangeSet.add(Range.closed(start, end));
            }
            RangeSet<Long> blackRangeSet = TreeRangeSet.create();
            int limitIndex = 0;
            while ((blackStr = blackBf.readLine()) != null) {
                limitIndex += 1;
                Long start = ipToLong(blackStr.split(IP_RANGE_SEP)[0]);
                Long end = ipToLong(blackStr.split(IP_RANGE_SEP)[1]);
                blackRangeSet.add(Range.closed(start, end));
                if (limitIndex >= 10000) {
                    whiteRangeSet.removeAll(blackRangeSet);
                    blackRangeSet = TreeRangeSet.create();
                    limitIndex = 0;
                }
            }
            whiteRangeSet.removeAll(blackRangeSet);
            System.out.println(whiteRangeSet);
            List<Range<Long>> rangeList = new ArrayList<>(whiteRangeSet.asRanges());
            List<Range<Long>> finalRangeList = rangeList.stream().filter(longRange -> !(longRange.upperEndpoint() - longRange.lowerEndpoint() <= 1 &&
                    longRange.lowerBoundType() == BoundType.OPEN &&
                    longRange.upperBoundType() == BoundType.OPEN)).collect(Collectors.toList());
            int finalRangeSize = finalRangeList.size();
            for (int i = 0; i < size; i++) {
                int index = RandomUtils.nextInt(0, finalRangeSize);
                Range<Long> range = finalRangeList.get(index);
                Long lower = range.lowerEndpoint();
                if (range.lowerBoundType() == BoundType.OPEN) {
                    lower = lower + 1;
                }
                Long upper = range.upperEndpoint();
                if (range.lowerBoundType() == BoundType.OPEN) {
                    upper = upper - 1;
                }
                long ipLong = RandomUtils.nextLong(lower, upper + 1);
                ipList.add(longToIp(ipLong));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getValidIps error, ", e);
        }
        return ipList;
    }

    public static void main(String[] args) {
        System.out.println(getValidIps(60));
    }
}
