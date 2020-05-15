package com.fangtan.hourse.web;

import com.fangtan.hourse.service.LianjiaService;
import com.fangtan.hourse.util.OkHttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/hourse")
public class LoveMyHourseController {

    protected static Logger bizlogger = LoggerFactory.getLogger("biz");


    @Autowired
    private OkHttpUtil okHttpUtil;

    @Autowired
    private LianjiaService LianjiaService;


    @GetMapping("/lovemyhourse/list")
    public void redisSaveList() {
    }

    @GetMapping("/getHzHouseGovDate")
    public void getHzHouseGovDate() {
        bizlogger.info("进入");
        okHttpUtil.getHZhourseGovData();
    }



}
