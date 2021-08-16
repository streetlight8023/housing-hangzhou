package com.fangtan.hourse.constant;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.HashMap;

public class AreaCode {


    public static HashMap<Integer, ArrayList<Integer>>  getBinjiangCods(){
         HashMap<Integer, ArrayList<Integer>> ZONE_BINJIANG= Maps.newHashMap();
        ArrayList<Integer> list =  Lists.newArrayList();
        list.add(34952527);
        list.add(34569650);
        list.add(34569472);
        list.add(527);
        list.add(48);
        list.add(34569026);
        list.add(34575111);
        list.add(34607104);
        list.add(35367387);
        list.add(34573292);
        list.add(481);
        list.add(26);
        ZONE_BINJIANG.put(330108,list);
        return ZONE_BINJIANG;
    }

    public static HashMap<Integer, ArrayList<Integer>>  getXiaoshan(){
        HashMap<Integer, ArrayList<Integer>> ZONE_BINJIANG= Maps.newHashMap();
        ArrayList<Integer> list =  Lists.newArrayList();
        list.add(2637);
        list.add(34886446);
        list.add(1146);
        list.add(3584);
        list.add(1941);
        list.add(7290);
        list.add(34605524);
        list.add(34644370);
        list.add(6630);
        list.add(2573);
        list.add(34575624);
        list.add(34590899);
        list.add(978);
        list.add(1696);
        list.add(7934);
        ZONE_BINJIANG.put(330109,list);
        return ZONE_BINJIANG;
    }
}
