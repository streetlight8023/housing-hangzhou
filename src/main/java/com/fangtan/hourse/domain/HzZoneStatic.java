package com.fangtan.hourse.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HzZoneStatic {

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
        if (this.zone.equals(point.zone) && this.count == point.count)
            return true;
        else
            return false;

    }
}
