package com.fangtan.hourse.vo;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class HzZoneData {
    private List<HzZoneSeries> series;
    private List<LocalDate> xAxis;
    private List<String> legend;
}
