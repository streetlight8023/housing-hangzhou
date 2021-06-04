package com.fangtan.hourse.util;

import com.alibaba.fastjson.JSON;
import com.fangtan.hourse.config.MathAlgorithm;
import com.fangtan.hourse.dao.HzZoneStaticMapper;
import com.fangtan.hourse.domain.HzZoneStatic;
import lombok.Data;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by qhong on 2018/7/3 16:55
 **/
@Component
public class OkHttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(OkHttpUtil.class);

    protected static Logger bizlogger = LoggerFactory.getLogger("biz");

    @Autowired
    private HzZoneStaticMapper hzZoneStaticMapper;

    public static void main(String[] args) throws Exception {
        parseDate("西湖");
//        getHZhourseGovData();
    }

    public static Integer[] toArray(List<Integer> list) {
        Integer[] array = new Integer[list.size()];

        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }


    /**
     * 单价和总价最低
     *
     * @param list
     */
    public static void getMinAvgPriceAndTotalPrice(String zone,List<HourseInfo> list) {
        list.stream().collect(Collectors.groupingBy(p -> p, Collectors.counting()));

        Map<String, List<HourseInfo>> map = list.stream().collect(Collectors.groupingBy(HourseInfo::get小区));
        Iterator<Map.Entry<String, List<HourseInfo>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<HourseInfo>> next = iterator.next();
            List<HourseInfo> hourses = next.getValue();
            List<HourseInfo> minAvgPrice = getMinAvgPrice(hourses);
            List<HourseInfo> mintoTalPrice = getMinTotalPrice(hourses);
            Set<HourseInfo> set = new HashSet<>();
            set.addAll(copyList(minAvgPrice));
            set.addAll(copyList(mintoTalPrice));
            List<HourseInfo> arrayList = new ArrayList<>();
            arrayList.addAll(set);
            arrayList.sort(Comparator.comparing(HourseInfo::get总价));
            priceResult(zone,arrayList);

        }
    }

    public static  List<HourseInfo> copyList(List<HourseInfo> oldList){
        List<HourseInfo> newList = new ArrayList<>();
        for (HourseInfo oldHourseInfo : oldList) {
            HourseInfo newHourseInfo = new HourseInfo();
            BeanUtils.copyProperties(oldHourseInfo,newHourseInfo);
            newList.add(newHourseInfo);
        }
        return newList;
    }

    public static  List<HourseInfo> copyList(List<HourseInfo> oldList,Integer size){
        List<HourseInfo> newList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            HourseInfo newHourseInfo = new HourseInfo();
            BeanUtils.copyProperties(oldList.get(i),newHourseInfo);
            newList.add(newHourseInfo);

        }
        return newList;
    }


    public static List<HourseInfo> getMinAvgPrice(List<HourseInfo> list) {
        list.sort(Comparator.comparing(HourseInfo::get均价));
        if (list.size() > 5) {
            return copyList(list,5);
        } else {
            return list;
        }
    }

    public static  List<HourseInfo> getMinTotalPrice(List<HourseInfo> list) {
        list.sort(Comparator.comparing(HourseInfo::get总价));
        if (list.size() > 5) {
            return copyList(list,5);
        } else {
            return list;
        }
    }

    public static void priceResult(String zone,List<HourseInfo> list){
        int totalVisitCount = list.stream().mapToInt(HourseInfo::get带看次数).sum();

        List<Integer> totalPrices = list.stream().map((HourseInfo::get总价)).collect(Collectors.toList());
        List<Integer> avePrices = list.stream().map((HourseInfo::get均价)).collect(Collectors.toList());

        Integer totalMedian = MathAlgorithm.median(toArray(totalPrices));
        Integer avgMedian = MathAlgorithm.median(toArray(avePrices));
        List<Integer> modalNums = MathAlgorithm.getModalNums(toArray(totalPrices));


        if (list.size() > 10) {
            List<HourseInfo> hourseInfos = list.subList(0, 10);
            for (HourseInfo hourseInfo : hourseInfos) {
                System.out.println(JSON.toJSON(hourseInfo));
            }
        } else {
            for (HourseInfo hourseInfo : list) {
                System.out.println(JSON.toJSON(hourseInfo));
            }
        }
        String 小区名称 = list.size()>0?list.get(0).get小区():zone;
        System.out.println("区名称 = [" + zone + "] 小区 =["+小区名称 +"]  = 房源数目 [" + list.size() + "] 总共带看次数[" + totalVisitCount + "]  平均带看次数 [" + totalVisitCount / list.size() + "] 总价中位数 【" + totalMedian + "  ] + 总价众数 [" + JSON.toJSONString(modalNums) + " ]  小区中位均价 [" + avgMedian + "]");
    }


    /**
     * 带看量排序
     *
     * @param list
     */
    public static void getMaxQuantity(String zone, List<HourseInfo> list) {
        list.sort(Comparator.comparing(HourseInfo::get带看次数).reversed());
        priceResult(zone,list);
    }

    public static void parseDate(String zone) throws InterruptedException {
        Integer totalCount = getTotalCount(zone);
        Integer pageCount = new BigDecimal(totalCount / 30).setScale(5, BigDecimal.ROUND_UP).intValue();
        List<HourseInfo> list = new ArrayList<>();
        for (int i = 1; i < pageCount + 2; i++) {
            list.addAll(get5a5Data(zone, i));
            Thread.sleep(1000L);
        }
//        getMaxQuantity(zone,list);
        getMinAvgPriceAndTotalPrice(zone,list);

    }

    public static Integer getTotalCount(String zone) {
        Integer totalCount = 0;
        Document a5jData = get5a5jData(zone);
        Elements elements = a5jData.select("div[class=total-box fl]");
        if (elements.size() > 0) {
            Element element = elements.get(0);
            Elements spans = element.getElementsByTag("span");
            if (spans.size() > 0) {
                for (Element span : spans) {
                    String countStr = span.text();
                    totalCount = Integer.valueOf(countStr);
                    break;
                }
            }
        }
        return totalCount;
    }



    public static List<HourseInfo> getLianjiaData(String zone, Integer pageNum) {
        return null;
    }

        public static List<HourseInfo> get5a5Data(String zone, Integer pageNum) {
        String url = "https://hz.5i5j.com/ershoufang/n" + pageNum + "/_" + zone + "/";
        Document doc = request5a5j(url);
        Elements elements = doc.select("div[class=listX]");
        int hourseCount = 0;
        int hoursVisitTime = 0;
        List<HourseInfo> list = new ArrayList<>();
        for (Element element : elements) {
            Elements ps = element.getElementsByTag("p");
            HourseInfo hourseInfo = new HourseInfo();
            for (int i = 0; i < ps.size(); i++) {
                try {
                    if (i == 0) {
                        String hourseInfoStr = ps.get(i).text();
                        String[] strArr = hourseInfoStr.split("·");
                        if (strArr.length > 1) {
                            hourseInfo.set户型(strArr[0]);
                            hourseInfo.set面积(strArr[1]);
                        } else {
                            System.out.println("异常信息  " + hourseInfoStr);
                        }

                    }
                    if (i == 1) {
                        Elements as = ps.get(i).getElementsByTag("a");

                        if (as.size() > 1) {
                            hourseInfo.set小区(as.get(0).text());
                        } else {
                            System.out.println("获取小区名称失败  ");
                        }

                    }
                    if (i == 2) {
                        hourseCount++;
                        String text = ps.get(i).text();
                        String times = text.substring(text.indexOf("看") + 1, text.indexOf("次")).trim();
                        if (times != "" && times != null) {
                            hourseInfo.set带看次数(Integer.valueOf(times));
                        } else {
                            System.out.println("解析次数 出现错误");
//                        hoursVisitTime += Integer.valueOf(times);
                        }
                        list.add(hourseInfo);
                    }
                    if (i == 3) {
                        Elements strongs = ps.get(i).getElementsByTag("strong");
                        if (strongs.size() > 0) {
                            hourseInfo.set总价(Integer.valueOf(strongs.get(0).text()));
                        }
                    }

                    if (i == 4) {
                        String avgPrice = ps.get(i).text();
//                        String 单价 = avgPrice.replaceAll("单价", "");
                        String 单价 = avgPrice.substring(avgPrice.indexOf("价") + 1, avgPrice.indexOf("元")).trim();

                        hourseInfo.set均价(Integer.valueOf(单价));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }


            }
        }
//        DatePerPage datePerPage= new DatePerPage();
//        datePerPage.setZone(zone);
//        datePerPage.setTotalCount(hourseCount);
//        datePerPage.setVisitCount(hoursVisitTime);
        return list;

    }

    public static Document get5a5jData(String zone) {
        String url = "https://hz.5i5j.com/ershoufang/n3/_" + zone + "/";
        return request5a5j(url);
    }


    public Document getHZhourseGovData() {
//        String url = "http://fgj.hangzhou.gov.cn/col/col1684617/index.html";

//        String url = "https://api.hzfc.cn/hzfcweb_ifs/interaction/scxx";
//        String url = "http://fgj.hangzhou.gov.cn/col/col1229440802/index.html";
        String url = "https://api.hzfc.cn/hzfcweb_ifs/interaction/scxx";
        Document hZhourseGovData = requestHZhourseGov(url);
        Element scrollBox11 = hZhourseGovData.getElementById("scrollBox1");
        Elements elements = scrollBox11.select("div[class=list-item hehe]");
        Set<HzZoneStatic> set = new HashSet<>();
        if (elements.size() > 0) {
            for (Element element : elements) {
                Elements divs = element.getElementsByTag("div");
                HzZoneStatic hzZoneStatic = new HzZoneStatic();
                for (int i = 0; i < divs.size(); i++) {
                    if (i == 1) {
                        hzZoneStatic.setDealType("二手");
                        String zone = divs.get(i).text();
                        hzZoneStatic.setZone(zone);
                    } else if (i == 2) {
                        String text = divs.get(i).text();
                        String countStr = text.replaceAll("套", "");
                        hzZoneStatic.setCount(Integer.valueOf(countStr));
                        hzZoneStatic.setCreateTime(LocalDateTime.now());
                        bizlogger.info("搜集数据 当前={}",JSON.toJSONString(hzZoneStatic));
                        set.add(hzZoneStatic);
                    }
                }
            }
        }
        bizlogger.info("搜集数据 当前集合={}",JSON.toJSONString(set));
        for (HzZoneStatic hzZoneStatic : set) {
            bizlogger.info("搜集数据 当前={}",JSON.toJSONString(hzZoneStatic));
            hzZoneStaticMapper.insert(hzZoneStatic);
        }
        String s = JSON.toJSONString(set);
        bizlogger.info(s);
        return null;
    }

    /**
     * 请求杭州房屋保证局
     *
     * @param url
     * @return
     */
    public static Document requestHZhourseGov(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().
                url(url)
                .addHeader("Host", "api.hzfc.cn")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .addHeader("Sec-Fetch-Mode", "navigate")
                .addHeader("Sec-Fetch-Site", "same-origin")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.92 Safari/537.36")
                .addHeader("Referer", "http://fgj.hangzhou.gov.cn/col/col1621413/index.html")
                .addHeader("Cookie", "zh_choose_undefined=s; SERVERID=71bfd367a49f5c3316644ab3e3801eff|1587371165|1587371158")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String html = response.body().string();
                return Jsoup.parse(html);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static Document request5a5j(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().
                url(url)
                .addHeader("Host", "hz.5i5j.com")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .addHeader("Sec-Fetch-Mode", "navigate")
                .addHeader("Sec-Fetch-Site", "same-origin")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.92 Safari/537.36")
                .addHeader("Referer", "https://hz.5i5j.com/ershoufang/_%E5%81%87%E5%B1%B1%E6%96%B0%E6%9D%91?zn=%E5%81%87%E5%B1%B1%E6%96%B0%E6%9D%91")
                .addHeader("Cookie", "_Jo0OQK=31B2D5C0F781677604D7B36E3B7409926213CF83128F136549E44B178A18B2A2FC49CB126027B5C0628F0011C13516095ECE5039831648C33DDAB118763F49853BC9FC0DF34BBE505AF02631C467319B15B02631C467319B15B869297F6895F5D91GJ1Z1Ug==; PHPSESSID=kddtq0egbd8q3vbi3tqm8r0c1i; domain=hz")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String html = response.body().string();
                return Jsoup.parse(html);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 根据map获取get请求参数
     *
     * @param queries
     * @return
     */
    public static StringBuffer getQueryString(String url, Map<String, String> queries) {
        StringBuffer sb = new StringBuffer(url);
        if (queries != null && queries.keySet().size() > 0) {
            boolean firstFlag = true;
            Iterator iterator = queries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry<String, String>) iterator.next();
                if (firstFlag) {
                    sb.append("?" + entry.getKey() + "=" + entry.getValue());
                    firstFlag = false;
                } else {
                    sb.append("&" + entry.getKey() + "=" + entry.getValue());
                }
            }
        }
        return sb;
    }

    /**
     * 调用okhttp的newCall方法
     *
     * @param request
     * @return
     */
    private static String execNewCall(Request request) {
        Response response = null;
        try {
            OkHttpClient okHttpClient = SpringUtils.getBean("okHttpClient");
            response = okHttpClient.newCall(request).execute();
            int status = response.code();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            logger.error("okhttp3 put error >> ex = {}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return "";
    }

    /**
     * get
     *
     * @param url     请求的url
     * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
     * @return
     */
    public static String get(String url, Map<String, String> queries) {
        StringBuffer sb = getQueryString(url, queries);
        Request request = new Request.Builder()
                .addHeader("Host", "<calculated when request is sent>")
                .addHeader("User-Agent", "PostmanRuntime/7.24.1")
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Encoding", "gzip, deflate, br")
                .addHeader("Connection", "keep-alive")
                .url(sb.toString())
                .build();
        return execNewCall(request);
    }


}

@Data
class DatePerPage {

    String zone;
    //当页数量
    Integer totalCount;
    //当页带看数量
    Integer visitCount;
}


@Data
class HourseInfo {
    String 小区;

    String 户型;

    String 面积;
    //当页带看数量
    Integer 带看次数;
    //均价
    Integer 均价;
    //总价
    Integer 总价;


}