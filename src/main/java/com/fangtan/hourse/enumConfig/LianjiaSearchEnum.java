package com.fangtan.hourse.enumConfig;


public enum LianjiaSearchEnum {

    LESS_100W("p1", "低于100w"),
    B_100W_150W("p2", "100W到150W"),
    B_150W_200W("p3", "150W到200W"),
    B_200W_300W("p4", "200W到300W"),
    B_300W_500W("p5", "300W到500W"),
    MORE_500w("p6", "500W以上");


    private String code;

    private String desc;

    private LianjiaSearchEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setIndex(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
