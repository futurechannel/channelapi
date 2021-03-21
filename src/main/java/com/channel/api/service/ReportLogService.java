package com.channel.api.service;

import com.channel.api.dto.UniqueIdDto;
import com.channel.api.entity.AdvertInfo;
import com.channel.api.entity.AppInfo;
import com.channel.api.entity.ReportLog;
import com.channel.api.form.ReportLogForm;

/**
 * Created by gq on 2018/4/13.
 */

public interface ReportLogService {

    int insert(ReportLog log);

    ReportLog findById(String idfa,String appcode);

    UniqueIdDto getUniqueId(ReportLogForm params);

    String generateReportAppUrl(ReportLogForm params, AppInfo appInfo, AdvertInfo advertInfo,String callbackUrl);

    void copyReportReq(AppInfo appInfo, AdvertInfo advertInfo);

}
