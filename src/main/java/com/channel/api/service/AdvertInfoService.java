package com.channel.api.service;

import com.channel.api.entity.AdvertInfo;

public interface AdvertInfoService {

    void checkAdvertCode(String advertCode);

    AdvertInfo getAdvertInfoFromMemCache(String appCode, String advertCode );

    boolean checkCpcCircut(AdvertInfo advertInfo);
}
