package com.channel.api.entity;

import java.util.Date;

/**
 * Created by gq on 2018/4/29.
 */
public class FailCallback {
    private String idfa;
    private String adverterCode;
    private String appCode;
    private String callback;
    private Integer isRecall;
    private Integer isBalance;
    private Date createTime;
    private Date updateTime;
    private Integer idType;

    public String getIdfa() {
        return idfa;
    }

    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }

    public String getAdverterCode() {
        return adverterCode;
    }

    public void setAdverterCode(String adverterCode) {
        this.adverterCode = adverterCode;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public Integer getIsRecall() {
        return isRecall;
    }

    public void setIsRecall(Integer isRecall) {
        this.isRecall = isRecall;
    }

    public Integer getIsBalance() {
        return isBalance;
    }

    public void setIsBalance(Integer isBalance) {
        this.isBalance = isBalance;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIdType() {
        return idType;
    }

    public void setIdType(Integer idType) {
        this.idType = idType;
    }

    @Override
    public String toString() {
        return "FailCallback{" +
                "idfa='" + idfa + '\'' +
                ", adverterCode='" + adverterCode + '\'' +
                ", appCode='" + appCode + '\'' +
                ", callback='" + callback + '\'' +
                ", isRecall=" + isRecall +
                ", isBalance=" + isBalance +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", idType=" + idType +
                '}';
    }
}
