package com.channel.api.service.impl;

import com.channel.api.constants.ConstantMaps;
import com.channel.api.entity.AdvertInfo;
import com.channel.api.enums.ErrorCode;
import com.channel.api.exception.ApiException;
import com.channel.api.service.AdvertInfoService;
import org.springframework.stereotype.Service;

@Service
public class AdvertInfoServiceImpl implements AdvertInfoService {

    @Override
    public void checkAdvertCode(String advertCode) {
        if (!ConstantMaps.advertSets.contains(advertCode)) {
            throw new ApiException(ErrorCode.E602.getCode() + "");
        }
    }

    @Override
    public AdvertInfo getAdvertInfoFromMemCache(String appCode, String advertCode) {
        AdvertInfo advertInfo = ConstantMaps.getAdvertInfo(appCode, advertCode);

        if (advertInfo == null) {
            throw new ApiException(ErrorCode.E603.getCode() + "");
        }
        return advertInfo;
    }

    @Override
    public boolean checkCpcCircut(AdvertInfo advertInfo) {
        return advertInfo.getCpcCircut() != null && advertInfo.getCpcCircut() == 1 && advertInfo.getCpcNum() != null;
    }


}
