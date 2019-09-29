package com.channel.api.entity;

/**
 * Created by gq on 2018/4/21.
 */
public class AdvertInfo {

    private String adverterCode;
    private String appCode;
    private String adverterName;
    private int balanceRatio;
    private String comeFrom;
    private Integer cpcNum;
    private Long cpcTime;
    private Integer cpcCircut;

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

    public String getAdverterName() {
        return adverterName;
    }

    public void setAdverterName(String adverterName) {
        this.adverterName = adverterName;
    }

    public int getBalanceRatio() {
        return balanceRatio;
    }

    public void setBalanceRatio(int balanceRatio) {
        this.balanceRatio = balanceRatio;
    }

    public String getComeFrom() {
        return comeFrom;
    }

    public void setComeFrom(String comeFrom) {
        this.comeFrom = comeFrom;
    }

    public Integer getCpcNum() {
        return cpcNum;
    }

    public void setCpcNum(Integer cpcNum) {
        this.cpcNum = cpcNum;
    }

    public Long getCpcTime() {
        return cpcTime;
    }

    public void setCpcTime(Long cpcTime) {
        this.cpcTime = cpcTime;
    }

    public Integer getCpcCircut() {
        return cpcCircut;
    }

    public void setCpcCircut(Integer cpcCircut) {
        this.cpcCircut = cpcCircut;
    }

    @Override
    public String toString() {
        return "AdvertInfo{" +
                "adverterCode='" + adverterCode + '\'' +
                ", appCode='" + appCode + '\'' +
                ", adverterName='" + adverterName + '\'' +
                ", balanceRatio=" + balanceRatio +
                ", comeFrom='" + comeFrom + '\'' +
                ", cpcNum=" + cpcNum +
                ", cpcTime=" + cpcTime +
                ", cpcCircut=" + cpcCircut +
                '}';
    }
}
