package com.channel.api.util;


import com.channel.api.enums.ErrorCode;
import com.channel.api.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

public class YouKuParamsUtil {

    protected static final Logger logger = LoggerFactory.getLogger(YouKuParamsUtil.class);

    /**
     * @param params 需加密的参数，TreeMap保证参数按升序排序，
     *               非Java语言需要先按参数名进行排序，系统参数与业务参数相同的情况下，系统参数在前
     * @return 返回请求openapi所需参数，
     * 1、GET请求，直接遍历Map，拼接k-v即可；
     * 2、POST请求，迭代Map，封装为NameValuePair即可
     * @brief 签名
     * @author luhanlin
     * @date 2016-05-12
     */
    public static String get_sign(TreeMap<String, Object> params,String secret) {


        params.put("timestamp", System.currentTimeMillis() / 1000);
        params.put("version", "3.0");

        StringBuffer signString = new StringBuffer();
        try {
            /**
             * 生成签名字符串
             */
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                signString.append(entry.getKey());
                signString.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            throw new ApiException(ErrorCode.E902.getCode()+"");
        }
        String sign = "";
        signString.append(secret);
        logger.info("before sign str:{}",signString.toString());
        try {
            sign = md5Sign(signString.toString());
        } catch (Exception e) {
            throw new ApiException(ErrorCode.E902.getCode()+"");
        }

        return sign;
    }

    /**
     * 拼接请求参数，返回Map方便post请求封装请求参数
     *
     * @param params 所有参数
     * @param sign   加密字符串
     * @return
     */
    public static String opensysparams(TreeMap params, String sign) {
        params.put("sign", sign);
        return JsonUtils.toJson(params);
    }

    private static String md5Sign(String signString) {
        String sign = Md5.Md5(signString);
        return sign;
    }

    public static void main(String[] args) throws Exception {
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("idfa", "5A58EF1E-EEF2-478D-94EE-709B98407589");
        params.put("ip", "192.168.1.107");
        params.put("company_name","76baf031bd3f9ba1");
        params.put("appid","336141475");
        params.put("callbackurl","htt://www.baidu.com.cn");
        params.put("client_id", "e4OFL9l6Tposocm0");
        params.put("action", "youku.api.idfa.notification.click");

        String sign = get_sign(params,"knlklmkjcilmepgaidhmpfcfjdppinlj");
        System.out.println(opensysparams(params, sign));
    }


}
