package com.channel.api.util;

import com.channel.api.form.ReportLogForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gq on 2018/7/18.
 */
public class BeanUtil {

    protected final static Logger logger= LoggerFactory.getLogger(BeanUtil.class);

    // Bean --> Map 1: 利用Introspector和PropertyDescriptor 将Bean --> Map
    public static Map<String, Object> transBean2Map(Object obj) {

        Map<String, Object> map = new HashMap<>();

        if(obj == null){
            return map;
        }

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);

                    if(value!=null) {
                        map.put(key, value);
                    }
                }

            }
        } catch (Exception e) {
            logger.error("transBean2Map Error",e);
        }

        return map;

    }

    public static void main(String[] args) {
        ReportLogForm form=new ReportLogForm();
        form.setAppid("1213");
        System.out.println(transBean2Map(form));
    }

}
