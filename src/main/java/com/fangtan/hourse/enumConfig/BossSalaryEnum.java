package com.fangtan.hourse.enumConfig;


public enum BossSalaryEnum {

    TEN_AND_TWENTY(405, "10-20K"),
    TWENTY_AND_FIFTY(406, "20-50K");


    private Integer code;

    private String desc;

    private BossSalaryEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setIndex(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
