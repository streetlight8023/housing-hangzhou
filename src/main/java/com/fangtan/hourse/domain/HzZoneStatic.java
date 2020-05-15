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
}
