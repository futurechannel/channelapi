package com.channel.api.enums;

public enum UniqueType {
    UNKNOWN(0, "UNKNOWN"),
    IDFA(1, "IDFA"),
    CAID(2, "CAID"),
    IP_UA(3, "IP_UA");

    private int type;
    private String name;


    UniqueType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static UniqueType get(int type) {
        for (UniqueType uniqueType : UniqueType.values()) {
            if (uniqueType.getType() == type)
                return uniqueType;
        }
        return UNKNOWN;
    }
}
