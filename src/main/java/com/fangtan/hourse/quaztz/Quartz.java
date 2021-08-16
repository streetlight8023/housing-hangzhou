package com.fangtan.hourse.quaztz;

import com.fangtan.hourse.enumConfig.LianjiaZoneSearchEnum;
import com.fangtan.hourse.service.HzZoneStaticService;
import com.fangtan.hourse.service.LianjiaService;
import com.fangtan.hourse.util.OkHttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Quartz {

    protected static Logger bizlogger = LoggerFactory.getLogger("biz");


    @Autowired
    private OkHttpUtil okHttpUtil;

    @Autowired
    private HzZoneStaticService hzZoneStaticService;

    @Autowired
    private LianjiaService lianjiaService;

//    @Scheduled(cron = "10 * * * * ?")
    @Scheduled(cron = "0 55 23 * * ?")
    public void perDay(){
        bizlogger.info("执行定时任务");
    okHttpUtil.getHZhourseGovData();
    }

    /**
     * 每天10点通知
     */
    @Scheduled(cron = "0 0 10 * * ?")
    public void alertMsg(){
        bizlogger.info("执行 校验数据 是否存入 数据库 定时任务");
        hzZoneStaticService.hasData();
    }

    /**
     * 每周六扫描数据
     */
    @Scheduled(cron = "0 15 05 ? * SUN")
    public void scan() throws InterruptedException {
        bizlogger.info("执行 校验数据 是否存入 数据库 定时任务");
        LianjiaZoneSearchEnum[] values = LianjiaZoneSearchEnum.values();
        for (LianjiaZoneSearchEnum value : values) {
            lianjiaService.deleteZoneCache(value.getCode());
            lianjiaService.getAllLianjiaHousecode(value.getCode());
        }
    }
}
