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
 * Created by gq on 2018/4/29.
 */
@Component
public class FailReCallJob {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CallBackHandler handler;
    @Autowired
    private FailCallbackService failCallbackService;

    @Autowired
    private CallBackService callBackService;

    public void reCall(){
        LOG.info("@Scheduled-------failRecall()--start");
        int pageSize=20;
        Date now =new Date();
        String start=DateUtils.defineDayBefore2Str(now,-1,"yyyy-MM-dd 00:00:00");
        String end= DateUtils.formatDate2Str(now,"yyyy-MM-dd 00:00:00");

        FailCallback fail=new FailCallback();
        fail.setIsRecall(Constants.FAIL_CALL_ISRECALL_NO);
        fail.setIsBalance(Constants.FAIL_CALL_ISBLANCE_YES);

        int count=failCallbackService.countFailCall(fail,start,end);
        if(count<1){
            LOG.info("@Scheduled======failRecall() is empty===end");
            return;
        }

        int i=count/pageSize+1;
        for(int j=0;j<i;j++){
            List<FailCallback> list=failCallbackService.findList(fail,start,end,pageSize);
            if (CollectionUtils.isEmpty(list)) {
                LOG.info("@Scheduled======failRecall() is empty===end");
                return;
            }

            for(FailCallback item:list){
                String result;
                try {
                    result = handler.callBack(item.getCallback());
                }catch (Exception e){
                    LOG.error("二次回调渠道接口异常");
                    result=Constants.CALL_BACK_FAIL;
                }

                if(Constants.CALL_BACK_SUC.equals(result)){
                    CallbackLog param = new CallbackLog();
                    param.setIdfa(item.getIdfa());
                    param.setAppCode(item.getAppCode());
                    param.setIsBalance(Constants.CALL_BACK_ISBLANCE_YES);
                    param.setUpdateTime(new Date());
                    param.setIsCall(Constants.CALL_BACK_ISCALL_YES);
                    param.setIsFinish(Constants.CALL_BACK_ISFINISH_YES);
                    LOG.info("二次回调渠道成功:"+param.toString());
                    try {
                        callBackService.updateStatus(param);
                    }catch (Exception e){
                        LOG.error("===更新二次回调记录状态异常",e);
                    }
                } else {
                    LOG.error("二次回调渠道失败:"+item.toString());
                }

                FailCallback failParam=new FailCallback();
                failParam.setIdfa(item.getIdfa());
                failParam.setAppCode(item.getAppCode());
                failParam.setIsBalance(Constants.FAIL_CALL_ISBLANCE_YES);
                failParam.setIsRecall(Constants.FAIL_CALL_ISRECALL_YES);
                failParam.setUpdateTime(new Date());

                try {
                    failCallbackService.updateStatus(failParam);
                }catch (Exception e){
                    LOG.error("===更新二次回调失败记录状态异常",e);
                    return;
                }

            }
        }



    }

}
