package com.fangtan.hourse.enumConfig;


/**
 * @desc:  redis失效时间，使用枚举是为了规范传入的redis中key统一在当前枚举中，而不会出现大量的魔数(本来是可以直接用常量类去替代)
 * @date: 2019/1/11 15:40
 * @author: weiqingeng
 */
public enum RedisKeyEnum {


   KEY_TEST("test:%s", "测试使用的key"),
   KEY_PINGTUAN("", "拼团使用的key"),







    ;







    private String key;

    private String desc;

    private RedisKeyEnum(String key, String desc){
        this.key = key;
        this.desc = desc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
