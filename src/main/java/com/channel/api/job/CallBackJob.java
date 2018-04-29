package com.channel.api.job;

import com.channel.api.constants.Constants;
import com.channel.api.entity.CallbackLog;
import com.channel.api.entity.FailCallback;
import com.channel.api.handler.CallBackHandler;
import com.channel.api.service.CallBackService;
import com.channel.api.service.FailCallbackService;
import com.channel.api.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by gq on 2018/4/15.
 */
@Component
public class CallBackJob {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CallBackHandler handler;
    @Autowired
    private CallBackService callBackService;
    @Autowired
    private FailCallbackService failCallbackService;


    public void callback() {
        LOG.info("@Scheduled-------callback()--start");
        Date start = DateUtils.dayBefore2Date(-1);
        Date end = new Date();
        CallbackLog log = new CallbackLog();
        log.setIsBalance(Constants.CALL_BACK_ISBLANCE_YES);
        log.setIsCall(Constants.CALL_BACK_ISCALL_NO);

        //一次任务最多取二十页
        for (int i=0;i<30;i++) {

            List<CallbackLog> list = callBackService.findList(log, start, end, 0, 20);
            if (CollectionUtils.isEmpty(list)) {
                LOG.info("@Scheduled======callback is empty===end");
                return;
            }

            for (CallbackLog item : list) {
                String result;
                try {
                    result = handler.callBack(item.getCallback());
                }catch (Exception e){
                    LOG.error("回调渠道接口异常");
                    result=Constants.CALL_BACK_FAIL;
                }

                CallbackLog param = new CallbackLog();
                param.setIdfa(item.getIdfa());
                param.setAppCode(item.getAppCode());
                param.setIsBalance(Constants.CALL_BACK_ISBLANCE_YES);
                param.setUpdateTime(new Date());
                param.setIsCall(Constants.CALL_BACK_ISCALL_YES);

                if(Constants.CALL_BACK_SUC.equals(result)) {
                    param.setIsFinish(Constants.CALL_BACK_ISFINISH_YES);
                    LOG.info("回调渠道成功:"+param.toString());
                }else {
                    //回调失败，进入回调失败表
                    param.setIsFinish(Constants.CALL_BACK_ISFINISH_NO);

                    FailCallback failCall=new FailCallback();
                    failCall.setIdfa(item.getIdfa());
                    failCall.setAdverterCode(item.getAdverterCode());
                    failCall.setAppCode(item.getAppCode());
                    failCall.setCallback(item.getCallback());
                    failCall.setCreateTime(new Date());
                    failCall.setIsRecall(Constants.FAIL_CALL_ISRECALL_NO);
                    failCall.setIsBalance(item.getIsBalance());
                    LOG.error("回调失败，存入回调失败表:"+failCall.toString());

                    failCallbackService.insertFailCallback(failCall);
                }

                try {
                    callBackService.updateStatus(param);
                }catch (Exception e){
                    LOG.error("===更新回调记录状态异常",e);
                    return;
                }
            }
        }

    }

}
