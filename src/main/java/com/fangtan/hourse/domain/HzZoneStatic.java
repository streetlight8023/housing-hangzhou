package com.fangtan.hourse.domain;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Data
public class HzZoneStatic {

    protected static Logger bizlogger = LoggerFactory.getLogger("biz");


    private Integer id;
    private String zone;
    private Integer count;
    private String dealType;
    private LocalDateTime createTime;

    @Override
    public int hashCode() {
        return zone.hashCode()+count;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;

        if (this.getClass() != obj.getClass())
            return false;

        HzZoneStatic point = (HzZoneStatic) obj;
        if (this.zone.equals(point.zone) && this.count.intValue() == point.count.intValue()){
            bizlogger.info("对比成功 当前zone 当前count 对比zone 对比count",this.zone,this.count,point.zone,point.count);
            return true;
        }else{
            bizlogger.info("对比失败 当前zone 当前count 对比zone 对比count",this.zone,this.count,point.zone,point.count);
            return false;
        }
    }
}
