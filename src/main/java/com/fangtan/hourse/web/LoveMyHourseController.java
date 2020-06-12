package com.fangtan.hourse.web;

import com.fangtan.hourse.config.CustomParam;
import com.fangtan.hourse.domain.UserRegForm;
import com.fangtan.hourse.service.LianjiaService;
import com.fangtan.hourse.util.OkHttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/hourse")
public class LoveMyHourseController {

    protected static Logger bizlogger = LoggerFactory.getLogger("biz");


    @Autowired
    private OkHttpUtil okHttpUtil;

    @Autowired
    private LianjiaService LianjiaService;




    @PostMapping("/test/param")
    public void getUserRegForm(@RequestBody @Valid  UserRegForm userRegForm) {
        System.out.println(userRegForm.getEmail());
        System.out.println(userRegForm.getGender());
        System.out.println(userRegForm.getNickname());

    }


    @GetMapping("/lovemyhourse/list")
    public void redisSaveList() {
    }

    @GetMapping("/getHzHouseGovDate")
    public void getHzHouseGovDate() {
        bizlogger.info("进入");
        okHttpUtil.getHZhourseGovData();
    }



}
