package com.fangtan.hourse.util;

import com.oracle.tools.packager.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    static ThreadLocal<String> t = new ThreadLocal<>();

    static ThreadLocal<SimpleDateFormat> dateFormatThreadLocal = new ThreadLocal<>();


    private static SimpleDateFormat getSimpleDateFormat() {
        SimpleDateFormat simpleDateFormat = dateFormatThreadLocal.get();
        if (simpleDateFormat == null) {
            SimpleDateFormat sd = new SimpleDateFormat(DATE_FORMAT);
            dateFormatThreadLocal.set(sd);
            return sd;
        }
        return dateFormatThreadLocal.get();

    }

    public static void main(String[] args) throws ParseException {
//        testStr();
        shareTest();
    }


    private static void shareTest(){
         ThreadLocal<String> t = new ThreadLocal<>();
         t.set("parent thread ");
         new Thread(new Runnable() {
             @Override
             public void run() {
                 System.out.println("子线程 "+t.get());
             }
         }).run();


    }


    private static void testStr() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                t.set("thread - 1 这行不会打印");
                t.set("thread - 1 这行会打印");
                System.out.println("current " + t.get());
            }
        }).run();

        new Thread(new Runnable() {
            @Override
            public void run() {
                t.set("thread - 2 test ");
                System.out.println("current " + t.get());
            }
        }).run();
    }

}

