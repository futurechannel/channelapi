package com.channel.api.util;

import com.channel.api.constants.Constants;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
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
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;


public class HttpClientUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class); // 日志记录

    private static RequestConfig requestConfig = null;

    private static CloseableHttpClient client = null;

    static {
        // 设置请求和传输超时时间
        requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
        client = HttpClients.createDefault();
    }

    public static String httpGet(String url) {
        return httpGet(url, null);
    }


    /**
     * 发送get请求
     *
     * @param url 路径
     * @return
     */
    public static String httpGet(String url, String appCode) {
        String strResult = null;

        // 发送get请求
        HttpGet request = new HttpGet(url);
        request.setConfig(requestConfig);
        String catName = StringUtils.isEmpty(appCode) ? "sysGet" : appCode;
        Transaction transaction = Cat.newTransaction("httpClientGet", catName);
        Cat.logEvent("httpClientGet", catName, Message.SUCCESS, url);
        try {
            CloseableHttpResponse response = client.execute(request);
            // 请求发送成功，并得到响应
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 读取服务器返回过来的json字符串数据
                HttpEntity entity = response.getEntity();
                strResult = EntityUtils.toString(entity, "utf-8");
                if (StringUtils.isEmpty(strResult)) {
                    strResult = "";
                }
                transaction.setStatus(Message.SUCCESS);

            } else {
                logger.error("上报应用异常,code:" + response.getStatusLine().getStatusCode() + "url:" + url);
                transaction.setStatus("rspCode_" + response.getStatusLine().getStatusCode());

                strResult = Constants.HTTP_RSP_FAIL;
            }

        } catch (IOException e) {
            transaction.setStatus(e);
            logger.error("上报应用IO异常:" + url, e);
            strResult = Constants.HTTP_RSP_FAIL;
        } catch (Exception e) {
            transaction.setStatus(e);
            logger.error("上报应用异常:" + url, e);
            strResult = Constants.HTTP_RSP_FAIL;
        } finally {
            transaction.complete();
            request.releaseConnection();
        }
        return strResult;
    }

    public static void main(String[] args) throws Exception {
        String url = "http://iphone.v0.mgtv.com/ggt.php?idfa=&from=xinguo&callback=http%3A%2F%2Fapi.appleadx.com%2Fchannelapi%" +
                "2Fcallback%2Fapp%3Fidfa%3D0293668275884d778717b1602c0c1080%26appcode%3Dzmangguo%26type%3D3&caid=&" +
                "userAgent=" + URLEncoder.encode("Mozilla/5.0 (iPhone; CPU iPhone OS 14_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148", "utf-8") +
                "&ip=183.15.177.182&model=iPhone13,1&pos=0";
        System.out.println("url:" + url);
        System.out.println();
        System.out.println(URLEncoder.encode("","utf-8"));
        String resStr = HttpClientUtil.httpGet(url, "zmangguo");
        System.out.println(resStr);
    }


}