package com.fangtan.hourse.service;

import com.fangtan.hourse.dao.HzZoneStaticMapper;
import com.fangtan.hourse.domain.HzZoneStatic;
import com.fangtan.hourse.util.DingTalk;
import com.fangtan.hourse.vo.HzZoneData;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Component
public class HzZoneStaticService {
    @Autowired
    private HzZoneStaticMapper hzZoneStaticMapper;

    public HzZoneData getHzZoneEcharDate(){
        List<HzZoneStatic> list = hzZoneStaticMapper.findAll();
        return null;
    }

    public void hasData(){
        List<HzZoneStatic> list = hzZoneStaticMapper.findAll();
        if(!CollectionUtils.isEmpty(list)){
            list.sort(Comparator.comparing(HzZoneStatic::getCreateTime).reversed());
            HzZoneStatic hzZoneStatic = list.get(0);
            LocalDate staticTime = hzZoneStatic.getCreateTime().toLocalDate().plusDays(1);
            if(!staticTime.isEqual(LocalDate.now())){
                DingTalk.push("昨天的二手房销量情况没有统计到");
            }
        }
    }
}
