package com.fangtan.hourse.enumConfig;


/**
 * 公司规模
 */

public enum BossScaleEnum {

    BELOW_TWENTY(301, "0~20人"),
    TWENTY_NINTY(302, "20-99人"),
    NINTY_AND_FIVE_HOUDRED(303, "100-499"),
    IVE_HOUDRED_AND_ONE_THOUSAND(304, "500-999"),
    ONE_THOUSAND_AND_TEN_THOUSAND(305, "1000-9999"),
    BEYOUND__THOUSAND(306, "10000以上");


    private Integer code;

    private String desc;

    private BossScaleEnum(Integer code, String desc) {
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
