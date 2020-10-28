package com.fangtan.hourse.enumConfig;


import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public enum LianjiaZoneSearchEnum {

    SHANG_CHENG("shangcheng", "上城"),
    XIA_CHENG("xiacheng", "下城"),
    XI_HU("xihu", "西湖"),
    BIN_JIANG("binjiang", "滨江"),
    GONG_SHU("gongshu", "拱墅"),
    QIAN_JIANG("qiantangxinqu", "钱江新区"),
    DA_JIANG_DONG("dajiangdong1", "大江东"),
    XIAO_SHAN("xiaoshan", "萧山"),
    YU_HANG("yuhang", "余杭"),
    MORE_500w("jianggan", "江干");


    private String code;

    private String desc;

    private LianjiaZoneSearchEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public static String getNameByCode(String name) {
        return Arrays.stream(LianjiaZoneSearchEnum.values()).filter(x -> x.getCode().equals(name)).findFirst()
                .map(LianjiaZoneSearchEnum::getDesc).orElse(StringUtils.EMPTY);
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
