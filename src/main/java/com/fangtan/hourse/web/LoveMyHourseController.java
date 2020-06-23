package com.fangtan.hourse.web;

import com.fangtan.hourse.config.CustomParam;
import com.fangtan.hourse.domain.UserRegForm;
import com.fangtan.hourse.service.LianjiaService;
import com.fangtan.hourse.util.OkHttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public void getHzHouseGovDate(HttpServletRequest request) {
        bizlogger.info("开始统计销量 远程ip为 （）",getIp(request));
        okHttpUtil.getHZhourseGovData();
    }


    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


}
