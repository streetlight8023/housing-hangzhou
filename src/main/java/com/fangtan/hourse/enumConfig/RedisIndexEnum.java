package com.fangtan.hourse.enumConfig;


/**
 * @desc:  redis的仓库索引枚举,有0-15共16个仓库，使用枚举是为了规范传入的index一定是redix中存在的，而不是一个魔数
 * @date: 2019/1/11 15:40
 * @author: weiqingeng
 */
public enum RedisIndexEnum {

   INDEX_0(0, "0号仓库，默认的仓库"),
   INDEX_1(1, "1号仓库"),
   INDEX_2(2, "2号仓库"),
   INDEX_3(3, "3号仓库"),
   INDEX_4(4, "4号仓库"),
   INDEX_5(5, "5号仓库"),
   INDEX_6(6, "6号仓库"),
   INDEX_7(7, "7号仓库"),
   INDEX_8(8, "8号仓库"),
   INDEX_9(9, "9号仓库"),
   INDEX_10(10, "10号仓库"),
   INDEX_11(11, "11号仓库"),
   INDEX_12(12, "12号仓库"),
   INDEX_13(13, "13号仓库"),
   INDEX_14(14, "14号仓库"),
   INDEX_15(15, "15号仓库");



    private int index;

    private String desc;

    private RedisIndexEnum(int index, String desc){
        this.index = index;
        this.desc = desc;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
