package com.fangtan.hourse.vo;

import lombok.Data;

import java.util.List;


@Data
public class HzZoneSeries {
    private String name;
    private String type;
    private String stack;
    private List<Integer> data;
}
