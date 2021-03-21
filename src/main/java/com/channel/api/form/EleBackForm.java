package com.channel.api.form;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by gq on 2018/4/11.
 */
public class EleBackForm {
    @NotEmpty
    private String idfa;
    private String click_id;
    private String user_ip;
    @NotEmpty
    private String appcode;
    private Integer type;

    public String getIdfa() {
        return idfa;
    }

    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }

    public String getClick_id() {
        return click_id;
    }

    public void setClick_id(String click_id) {
        this.click_id = click_id;
    }

    public String getUser_ip() {
        return user_ip;
    }

    public void setUser_ip(String user_ip) {
        this.user_ip = user_ip;
    }

    public String getAppcode() {
        return appcode;
    }

    public void setAppcode(String appcode) {
        this.appcode = appcode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
