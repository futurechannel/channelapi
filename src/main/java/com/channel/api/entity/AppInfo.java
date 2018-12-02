package com.channel.api.entity;

/**
 * Created by gq on 2018/4/21.
 */
public class AppInfo {
    private String appCode;
    private String appId;
    private String appName;
    private String reportUrl;
    private Integer status;
    private String otherParams;
    private String callbackUrl;
    private int isRepeatable;
    private int queryTableNum;
    private String token;

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }


    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOtherParams() {
        return otherParams;
    }

    public void setOtherParams(String otherParams) {
        this.otherParams = otherParams;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }


    public int getIsRepeatable() {
        return isRepeatable;
    }

    public void setIsRepeatable(int isRepeatable) {
        this.isRepeatable = isRepeatable;
    }

    public int getQueryTableNum() {
        return queryTableNum;
    }

    public void setQueryTableNum(int queryTableNum) {
        this.queryTableNum = queryTableNum;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "appCode='" + appCode + '\'' +
                ", appId='" + appId + '\'' +
                ", appName='" + appName + '\'' +
                ", reportUrl='" + reportUrl + '\'' +
                ", status=" + status +
                ", otherParams='" + otherParams + '\'' +
                ", callbackUrl='" + callbackUrl + '\'' +
                ", isRepeatable=" + isRepeatable +
                ", queryTableNum=" + queryTableNum +
                ", token='" + token + '\'' +
                '}';
    }
}
