package com.fangtan.hourse.web;

import com.fangtan.hourse.enumConfig.LianjiaZoneSearchEnum;
import com.fangtan.hourse.service.HzZoneStaticService;
import com.fangtan.hourse.service.LianjiaService;
import com.fangtan.hourse.util.OkHttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hourse")
public class LianjiaHourseController {

    protected static Logger bizlogger = LoggerFactory.getLogger("biz");


    @Autowired
    private LianjiaService lianjiaService;

    @Autowired
    private HzZoneStaticService hzZoneStaticService;

    @GetMapping("/alive")
    public String alive() {
        bizlogger.info("进入");
        return "i am alive";
    }

    @GetMapping("/test")
    public void parseLianjiaDateForTest() {
        bizlogger.info("进入");
        lianjiaService.parseTest();
    }

    @GetMapping("/lianjia/get/")
    public void parseLianjiaDate(@RequestParam("zone")String zone) throws InterruptedException {
        bizlogger.info("进入");
        lianjiaService.parseLianjiaDate(zone);
    }

    @GetMapping("/dingding")
    public void dingdingTest() {
        bizlogger.info("进入");
        hzZoneStaticService.hasData();
    }

    @GetMapping("/allZone")
    public void allZone() throws InterruptedException {
        bizlogger.info("执行 校验数据 是否存入 数据库 定时任务");
        LianjiaZoneSearchEnum[] values = LianjiaZoneSearchEnum.values();
        for (LianjiaZoneSearchEnum value : values) {
//            lianjiaService.deleteZoneCache(value.getCode());
            lianjiaService.parseLianjiaDate(value.getCode());
//            lianjiaService.parseLianjiaDate("gongshu");
        }
    }


    @GetMapping("/allZoneFile")
    public void allZoneFile() throws InterruptedException {
        LianjiaZoneSearchEnum[] values = LianjiaZoneSearchEnum.values();
        for (LianjiaZoneSearchEnum value : values) {
            bizlogger.info("失败重新查询");
            lianjiaService.getAllLianjiaHousecode(value.getCode());
        }
    }




}
