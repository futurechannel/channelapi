package com.channel.api.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class HttpClientUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class); // 日志记录

    private static RequestConfig requestConfig = null;

    private static CloseableHttpClient client=null;

    static
    {
        // 设置请求和传输超时时间
        requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
        client = HttpClients.createDefault();
    }



    /**
     * 发送get请求
     * @param url 路径
     * @return
     */
    public static String httpGet(String url) {
        String strResult=null;

        // 发送get请求
        HttpGet request = new HttpGet(url);
        request.setConfig(requestConfig);
        try {
            CloseableHttpResponse response = client.execute(request);
            // 请求发送成功，并得到响应
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 读取服务器返回过来的json字符串数据
                HttpEntity entity = response.getEntity();
                strResult = EntityUtils.toString(entity, "utf-8");
            } else {
                logger.error("上报应用异常,code:"+response.getStatusLine().getStatusCode() +"url:" + url);
            }
        }
        catch (IOException e) {
            logger.error("上报应用IO异常:" + url, e);
        }catch (Exception e){
            logger.error("上报应用异常:" + url, e);
        } finally {
            request.releaseConnection();
        }
        return strResult;
    }


}