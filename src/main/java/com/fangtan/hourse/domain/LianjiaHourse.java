package com.fangtan.hourse.domain;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class LianjiaHourse {

    /**
     * 房源编号
     */
    private String housecode;

    /**
     * 区域
     */

    private String zone;

    /**
     * 社区
     */

    private String subZone;


    /**
     * 房源检验唯一编码
     */
    private String houseCheckcode;

    /**
     * 房源标题
     */
    private String hourseTitle;

    /**
     * 小区名称
     */
    private String communityName;


    /**
     * 房间结构
     */
    private String roomType;


    /**
     * 第一次总价
     */
    private Integer totalPrice= 0;

    /**
     * 当前爬取的总价
     */
    private Integer nowTotalPrice= 0;


    /**
     * 房屋状态1:正常可买，0：不可以买
     */
    private Integer houseStatus= 1;

    /**
     * 单价
     */
    private Integer avgPrice= 0;

    /**
     * 建筑面积
     */
    private BigDecimal buildSize= new BigDecimal(0);

    /**
     * 套内面积
     */
    private BigDecimal roomSize = new BigDecimal(0);

    /**
     * 得房率
     */
    private BigDecimal hourseRate = new BigDecimal(0);

    /**
     * 客厅大小
     */
    private BigDecimal livingRoomSize = new BigDecimal(0);

    /**
     *卧室数目
     */
    private Integer sleepingRoomCount = 0;

    /**
     * 洗手间数量
     */
    private Integer watchRoomCount = 0;

    /**
     * 阳台数量
     */
    private Integer balconyCount= 0 ;

    /**
     * 厨房大小
     */
    private BigDecimal kitchenSize = new BigDecimal(0);

    /**
     * 住宅或者公寓
     */
    private String useAge;






}
