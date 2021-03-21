package com.channel.api.dto;

import com.channel.api.enums.UniqueType;

public class UniqueIdDto {

    private String uniqueId;
    private UniqueType uniqueType;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public UniqueType getUniqueType() {
        return uniqueType;
    }

    public void setUniqueType(UniqueType uniqueType) {
        this.uniqueType = uniqueType;
    }
}
