package com.fangtan.hourse.service;

import com.alibaba.fastjson.JSON;
import com.fangtan.hourse.dao.LianjiaHourseMapper;
import com.fangtan.hourse.domain.LianjiaHourse;
import com.fangtan.hourse.enumConfig.LianjiaSearchEnum;
import com.fangtan.hourse.enumConfig.LianjiaZoneSearchEnum;
import com.fangtan.hourse.enumConfig.RedisExpireEnum;
import com.fangtan.hourse.enumConfig.RedisIndexEnum;
import com.fangtan.hourse.redis.IRedisCache;
import com.fangtan.hourse.util.StringUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LianjiaService {

    protected static Logger bizlogger = LoggerFactory.getLogger("biz");


    @Autowired
    private LianjiaHourseMapper lianjiaHourseMapper;

    @Autowired
    private IRedisCache redisCache;



//    public static void main(String[] args) {
//        String pinyinZone = StringUtils.getPingYin("dajiangdong1");
//        System.out.println("pinyinZone "+pinyinZone);
//
//    }

    public void deleteZoneCache(String zone){
        String pinyinZone = StringUtils.getPingYin(zone);
        String key ="HOURSE_LIANJIA_" + pinyinZone;
        redisCache.del(RedisIndexEnum.INDEX_0,key);
    }

    /**
     * 获取房源编号
     *
     * @param zone
     * @return
     * @throws InterruptedException
     */
    public  Set<String> getAllLianjiaHousecode(String zone) throws InterruptedException {

        Integer totalCount = getLianjiaTotalCount(zone);
        String pinyinZone = StringUtils.getPingYin(zone);
        String key ="HOURSE_LIANJIA_" + pinyinZone;

        List<String> lianjiaHousecodes;
        try{
             lianjiaHousecodes = redisCache.listGetAll(RedisIndexEnum.INDEX_0, key);
        }catch (Exception e){
            bizlogger.error(" redis 获取异常 ",e);
            lianjiaHousecodes= null;
        }
        bizlogger.info("当前区域={} 的集合是={}",pinyinZone,JSON.toJSONString(lianjiaHousecodes));
        if(!CollectionUtils.isEmpty(lianjiaHousecodes)){
            HashSet<String> sets = Sets.newHashSet();
            sets.addAll(lianjiaHousecodes);
            return sets;
        }

        ArrayList<String> allLianjiaHousecode = Lists.newArrayList();
        Set<String> set = Sets.newHashSet();
        if (totalCount > 3000) {
//            Set<String> allLianjiaHousecodeLess500 = getAllLianjiaHousecodeLess500(zone);
//            Set<String> allLianjiaHousecodeMore500 = getAllLianjiaHousecodeMore500(zone);
            LianjiaSearchEnum[] values = LianjiaSearchEnum.values();
            for (LianjiaSearchEnum value : values) {
                Set<String> allLianjiaHousecodeByRange = getAllLianjiaHousecodeByRange(zone, value.getCode());
                set.addAll(allLianjiaHousecodeByRange);
                System.out.println("循环添加");
            }
        } else {
            Integer pageCount = new BigDecimal(totalCount / 30).setScale(5, BigDecimal.ROUND_UP).intValue();
            for (int i = 1; i < pageCount + 2; i++) {
                set.addAll(getLianjiaHousecodeByPage(zone, i));
                System.out.println(zone+"所有房源 add ... ");
                Thread.sleep(getRandomTime());
            }
        }
        allLianjiaHousecode.addAll(set);
        redisCache.listPushAll(RedisIndexEnum.INDEX_0, key, allLianjiaHousecode, RedisExpireEnum.EXPIRE_FOREVER);
        return set;
    }

    public  Set<String> getAllLianjiaHousecodeLess500(String zone) throws InterruptedException {
        Integer totalCount = getLianjiaTotalCountLess500(zone);
        Integer pageCount = new BigDecimal(totalCount / 30).setScale(5, BigDecimal.ROUND_UP).intValue();
        Set<String> set = Sets.newHashSet();
        for (int i = 1; i < pageCount + 2; i++) {
            set.addAll(getLianjiaHousecodeByPageLess500(zone, i));
            System.out.println("500万以下的房子  add ... ");
            Thread.sleep(getFastRandomTime());
        }
        return set;
    }

    public  Set<String> getAllLianjiaHousecodeMore500(String zone) throws InterruptedException {
        Integer totalCount = getLianjiaTotalCountMore500(zone);
        Integer pageCount = new BigDecimal(totalCount / 30).setScale(5, BigDecimal.ROUND_UP).intValue();
        Set<String> set = Sets.newHashSet();
        for (int i = 1; i < pageCount + 2; i++) {
            set.addAll(getLianjiaHousecodeByPageMore500(zone, i));
            System.out.println("500万以上的房子  add ... ");
            Thread.sleep(getFastRandomTime());
        }
        return set;
    }

    public  Set<String> getAllLianjiaHousecodeByRange(String zone,String range) throws InterruptedException {
        Integer totalCount = getLianjiaTotalCountByRange(zone,range);
        if(totalCount<1){
            return new HashSet<>();
        }
        Integer pageCount = new BigDecimal(totalCount / 30).setScale(5, BigDecimal.ROUND_UP).intValue();
        Set<String> set = Sets.newHashSet();
        for (int i = 1; i < pageCount + 2; i++) {
            set.addAll(getLianjiaHousecodeByPageByRange(zone, i,range));
            System.out.println(range+"  add ... ");
            Thread.sleep(getFastRandomTime());
        }
        return set;
    }

    public static Long getRandomTime() {
        Random random = new Random();
        int i = random.nextInt(1000);
        return 1900L + i;
    }

    public static Long getFastRandomTime() {
        Random random = new Random();
        int i = random.nextInt(100);
        return 1200L + i;
    }


    /**
     * 查询每个房源信息
     *
     * @param zone
     * @return
     * @throws InterruptedException
     */
    public List parseLianjiaDate(String zone) throws InterruptedException {
        String prefix = "https://hz.lianjia.com/ershoufang/";
        Set<String> allLianjiaHousecode = getAllLianjiaHousecode(zone);
        bizlogger.info("原始数量 ={}", allLianjiaHousecode.size());
        Set<String> existHourseSet = lianjiaHourseMapper.findByZone(zone).stream().map(LianjiaHourse::getHousecode).collect(Collectors.toSet());
        allLianjiaHousecode.removeAll(existHourseSet);
        bizlogger.info("剩余数量 ={} ", allLianjiaHousecode.size());
        for (String hourseCode : allLianjiaHousecode) {
            if(Strings.isNullOrEmpty(hourseCode)){
                bizlogger.info("房源编码为空");
                continue;
            }

            List<LianjiaHourse> list = lianjiaHourseMapper.findByCode(hourseCode);
            String url = prefix + hourseCode + ".html";
            LianjiaHourse newLianjiaHourse = parse(url, hourseCode, zone);
            Thread.sleep(getRandomTime());
            if(Strings.isNullOrEmpty(newLianjiaHourse.getSubZone())){
                bizlogger.info("房源获取失败 当前请求url ={}",url);
                continue;
            }
            if(newLianjiaHourse==null){
                lianjiaHourseMapper.offSale(hourseCode);
            }
            if (CollectionUtils.isEmpty(list)) {
                try{
                    lianjiaHourseMapper.insert(newLianjiaHourse);

                }catch (Exception e){
                    bizlogger.error("当前code为 ={} 插入异常",hourseCode,e);
                    continue;
                }
            }else{
                LianjiaHourse oldLianjiaHourse = list.get(0);
                if(oldLianjiaHourse!=null){
                    //价格变动了需要更改
                    if(newLianjiaHourse.getTotalPrice().intValue()!=oldLianjiaHourse.getNowTotalPrice()){
                        oldLianjiaHourse.setNowTotalPrice(newLianjiaHourse.getTotalPrice());
                        lianjiaHourseMapper.updateNowPrice(oldLianjiaHourse);
                    }
                }
            }
        }
        bizlogger.info("解析结束");
        return null;
    }

    public Element getFirstElement(Elements elements) {
        if (elements.size() > 0) {
            Element element = elements.get(0);
            return element;
        } else {
            System.out.println("获取元素失败 " + JSON.toJSONString(elements));
            return null;
        }
    }


    public static String getInfoBySibling(String name, Elements spans) {
        for (Element span : spans) {
            if (span.text().contains(name)) {
                String value = span.siblingElements().get(0).text();
                return value;
            }
        }
        return "";
    }

    public static String getInfoByParent(String name, Elements spans) {
        for (Element span : spans) {
            if (span.text().contains(name)) {
                String value = span.parent().text();
                return value;
            }
        }
        return "";
    }

    public void parseTest() {
        String url = "https://hz.lianjia.com/ershoufang/103107504777.html";
        LianjiaHourse test = parse(url, "test", "test");
        lianjiaHourseMapper.insert(test);
    }

    public  LianjiaHourse parse(String url, String code, String zone) {
        Document document = requestLianjia(url);
        LianjiaHourse lianjiaHourse = new LianjiaHourse();
        try {
            lianjiaHourse.setZone(zone);
            lianjiaHourse.setHousecode(code);
            lianjiaHourse.setHourseTitle(document.select("h1[class=main]").get(0).text());
            Element priceElement = document.select("div[class=price]").get(0);
            String totalPriceStr = priceElement.select("span[class=total]").get(0).text();
            String avgPriceStr = priceElement.select("span[class=unitPriceValue]").get(0).text();
            lianjiaHourse.setTotalPrice(StringUtils.removeChinese(totalPriceStr).intValue());
            lianjiaHourse.setAvgPrice(StringUtils.removeChinese(avgPriceStr.split("/")[0]).intValue());
//        lianjiaHourse.setsiz

            Element hourseInfoElement = document.select("div[class=houseInfo]").get(0);
            String roomStyle = hourseInfoElement.select("div[class=room]").get(0).select("div[class=mainInfo]").get(0).text();
            lianjiaHourse.setRoomType(roomStyle);
            String roomSizStr = hourseInfoElement.select("div[class=area]").get(0).select("div[class=mainInfo]").get(0).text();
            lianjiaHourse.setRoomSize(StringUtils.removeChinese(roomSizStr));


            Element aroundInfoElement = document.select("div[class=aroundInfo]").get(0).select("div[class=communityName]").get(0);
            String text = aroundInfoElement.select("a[class=info]").get(0).text();
            lianjiaHourse.setCommunityName(text);

            String subZone = document.select("div[class=areaName]").get(0).select("span[class=info]").get(0).getElementsByTag("a").get(1).text();
            lianjiaHourse.setSubZone(subZone);

            Element introductionElement = document.getElementById("introduction");
            Elements spans = introductionElement.select("span[class=label]");
            String 建筑面积Str = getInfoByParent("建筑面积", spans).split("㎡")[0];
            String 套内面积Str = getInfoByParent("套内面积", spans).split("㎡")[0];
            String 房屋用途 = getInfoBySibling("房屋用途", spans);
            String 房源核验统一编码 = getInfoBySibling("房源核验统一编码", spans);
            BigDecimal 套内面积 = StringUtils.removeChinese(套内面积Str);
            BigDecimal 建筑面积 = StringUtils.removeChinese(建筑面积Str);
            lianjiaHourse.setBuildSize(建筑面积);
            lianjiaHourse.setRoomSize(套内面积);
            lianjiaHourse.setHourseRate(new BigDecimal(0));
            if (套内面积.intValue() > 0) {
                BigDecimal 得房率 = 套内面积.divide(建筑面积, 4, BigDecimal.ROUND_UP);
                lianjiaHourse.setHourseRate(得房率);
            }
            lianjiaHourse.setUseAge(房屋用途);
            lianjiaHourse.setHouseCheckcode(房源核验统一编码);


            Element element = document.getElementById("infoList");
            if (element == null) {
                return lianjiaHourse;
            }
            Elements divs = element.select("div[class=row]");
//        for (Element element : elements) {


            BigDecimal livingRoomSize = new BigDecimal(0);
            Integer sleepingRoomCount = 0;
            Integer watchRoomCount = 0;
            Integer balconyCount = 0;
            BigDecimal kitchenSize = new BigDecimal(0);

            for (int i = 0; i < divs.size(); i++) {
                String div = divs.get(i).text();
                if (!Strings.isNullOrEmpty(div) && div.contains("客厅")) {
                    String sizeStr = divs.get(i).text();
                    livingRoomSize = StringUtils.removeChinese(sizeStr);
                } else if (!Strings.isNullOrEmpty(div) && div.contains("卧")) {
                    lianjiaHourse.setSleepingRoomCount(sleepingRoomCount++);
                    ;
                } else if (!Strings.isNullOrEmpty(div) && div.contains("卫")) {
                    watchRoomCount++;
                } else if (!Strings.isNullOrEmpty(div) && div.contains("阳台")) {
                    balconyCount++;
                } else if (!Strings.isNullOrEmpty(div) && div.contains("厨")) {
                    String sizeStr = divs.get(i).text();
                    kitchenSize = StringUtils.removeChinese(sizeStr);
                }
            }
            lianjiaHourse.setLivingRoomSize(livingRoomSize);
            lianjiaHourse.setSleepingRoomCount(sleepingRoomCount);
            lianjiaHourse.setWatchRoomCount(watchRoomCount);
            lianjiaHourse.setBalconyCount(balconyCount);
            lianjiaHourse.setKitchenSize(kitchenSize);

        } catch (Exception e) {
            e.printStackTrace();
            bizlogger.error("异常 hourseCode ={} 异常url={} 异常信息", code, url, e);
            return lianjiaHourse;
        }

        return lianjiaHourse;

    }


    /**
     * 获取房源编号
     *
     * @param zone
     * @param page
     * @return
     */
    public  List getLianjiaHousecodeByPage(String zone, Integer page) {
        String pinyinZone = StringUtils.getPingYin(zone);
        String url = "https://hz.lianjia.com/ershoufang/" + pinyinZone + "/pg" + page + "/";
        return getLianjiaHouseCodes(url);
    }

    /**
     * 获取500w以下 房源
     *
     * @param zone
     * @param page
     * @return
     */
    public  List getLianjiaHousecodeByPageLess500(String zone, Integer page) {
        String pinyinZone = StringUtils.getPingYin(zone);
        String url = "https://hz.lianjia.com/ershoufang/" + pinyinZone + "/pg" + page + "p1p2p3p4/";
        return getLianjiaHouseCodes(url);

    }

    /**
     * 获取500万以上房源
     *
     * @param zone
     * @param page
     * @return
     */
    public  List getLianjiaHousecodeByPageMore500(String zone, Integer page) {
        String pinyinZone = StringUtils.getPingYin(zone);
        String url = "https://hz.lianjia.com/ershoufang/" + pinyinZone + "/pg" + page + "p5p6/";
        return getLianjiaHouseCodes(url);

    }

    public  List getLianjiaHousecodeByPageByRange(String zone, Integer page,String range) {
        String pinyinZone = StringUtils.getPingYin(zone);
        String url = "https://hz.lianjia.com/ershoufang/" + pinyinZone + "/pg" + page +range+ "/";
        return getLianjiaHouseCodes(url);

    }


    public  List getLianjiaHouseCodes(String url) {
        List<String> list = Lists.newArrayList();

        Document document = requestLianjia(url);
        Elements elements = document.select("a[class=noresultRecommend img LOGCLICKDATA]");
        for (Element element : elements) {
            String housecode = element.attr("data-housecode");
            System.out.println(housecode);
            list.add(housecode);
        }
        return list;
    }


    public  Integer getLianjiaTotalCount(String zone) {
        String pinyinZone = StringUtils.getPingYin(zone);
        String url = "https://hz.lianjia.com/ershoufang/" + pinyinZone + "/";
        return getAllLianjiaTotalCount(url);
    }

    public  Integer getLianjiaTotalCountLess500(String zone) {
        String pinyinZone = StringUtils.getPingYin(zone);
        String url = "https://hz.lianjia.com/ershoufang/" + pinyinZone + "/p1p2p3p4/";
        return getAllLianjiaTotalCount(url);
    }

    public  Integer getLianjiaTotalCountMore500(String zone) {
        String pinyinZone = StringUtils.getPingYin(zone);
        String url = "https://hz.lianjia.com/ershoufang/" + pinyinZone + "/p5p6/";
        return getAllLianjiaTotalCount(url);
    }

    public  Integer getLianjiaTotalCountByRange(String zone,String range) {
        String pinyinZone = StringUtils.getPingYin(zone);
        String url = "https://hz.lianjia.com/ershoufang/" + pinyinZone + "/"+range+"/";
        return getAllLianjiaTotalCount(url);
    }

    public  Integer getAllLianjiaTotalCount(String url) {
        Integer totalCount = 0;
        Document document = requestLianjia(url);
        Elements elements = document.select("h2[class=total fl]");
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

    public  Document requestLianjia(String url) {
        OkHttpClient client = new OkHttpClient();
        Object ssidObj = redisCache.get(RedisIndexEnum.INDEX_0, "ssid");
        Object digv_extendsObj = redisCache.get(RedisIndexEnum.INDEX_0, "digv_extends");
        String cookie="";
        if(ssidObj!=null && digv_extendsObj!=null){
            String ssid=String.valueOf(ssidObj);
            String digv_extends=String.valueOf(digv_extendsObj);
            cookie="lianjia_uuid=f21dc9c4-5f31-4469-88ae-04069cf591fe; _smt_uid=5eaa756d.cd03683; UM_distinctid=171c9dab35ebc1-0629d63bd7dc42-30607c00-13c680-171c9dab35f766; _ga=GA1.2.639112175.1588229488; Hm_lvt_9152f8221cb6243a53c83b956842be8a=1602224899; select_city=330100; digv_extends="+digv_extends+"; lianjia_ssid="+ssid+"; _gid=GA1.2.1954693181.1603862853; CNZZDATA1253492436=136417260-1588225333-https%253A%252F%252Fwww.baidu.com%252F%7C1603866356; CNZZDATA1254525948=79133001-1588228309-https%253A%252F%252Fwww.baidu.com%252F%7C1603866789; cy_ip=60.191.79.194; CNZZDATA1255633284=725074903-1588228688-https%253A%252F%252Fwww.baidu.com%252F%7C1603867495; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%22171c9dab4fce90-0840ef442be061-30607c00-1296000-171c9dab4fdaee%22%2C%22%24device_id%22%3A%22171c9dab4fce90-0840ef442be061-30607c00-1296000-171c9dab4fdaee%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E7%9B%B4%E6%8E%A5%E6%B5%81%E9%87%8F%22%2C%22%24latest_referrer%22%3A%22%22%2C%22%24latest_referrer_host%22%3A%22%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC_%E7%9B%B4%E6%8E%A5%E6%89%93%E5%BC%80%22%2C%22%24latest_utm_source%22%3A%22baidu%22%2C%22%24latest_utm_medium%22%3A%22pinzhuan%22%2C%22%24latest_utm_campaign%22%3A%22wyhangzhou%22%2C%22%24latest_utm_content%22%3A%22biaotimiaoshu%22%2C%22%24latest_utm_term%22%3A%22biaoti%22%7D%7D; f-token=WVfq5FCfn8xsOxrAZLJCTu7Q9kGg1H9S8Hvse9XJUdb6paEZ+MqlXATjISEdsKt0Fqg7dgHLKUFp4rZMaP6anDgqpzsGCmlMzZHjw38tZZIZITyz4mYxM1PMEEd77+SYBtw=; CNZZDATA1255604082=1132291929-1588225809-https%253A%252F%252Fwww.baidu.com%252F%7C1603871304; _gat=1; _gat_past=1; _gat_global=1; _gat_new_global=1; _gat_dianpu_agent=1; Hm_lpvt_9152f8221cb6243a53c83b956842be8a=1603871490; srcid=eyJ0Ijoie1wiZGF0YVwiOlwiMTdlYjBiMWQzNmE0ZDQzNmZhMWNlYzFjNTFjYzg0YjgxZmM2NmE4NTVkNDcyNDViMGU3MzdmNGY5NmU1ZWE2Y2E0ZmFjMmFhZmUzOGFjZjRkNzJkY2MyMDJiZDkxYzQ1MWY2NzBlYWEzZTA3MjlmN2FmMDRhNmMwZTdlZWE5ZTkwMTlhM2NhZWVlNTc5MzQ3NDM3Y2QzMWNjNmIxNDFkYjc2MDhkMDM3YzlmMGNkMjZkZmI5OWNhMjQwNzM0NzZiMGE5MzliZmIwNzczNzIzN2ZlYzJiN2NkOTY3MWI1YmRkYmFjNGE2MmQ2MGJmMmUwZTUzOWJhNDM3NmE2MTM5OTk0Mzg5YTNlN2RiNmU1N2NjYzI0OWM5N2E3MjRjMzg3MWM5M2Q2MDI2OWZjYWI2MmExODk5ZmJhZWQ2YzRlYzY4MzU3OTMyMjAxOWE2NjcyODc0NTM1ZTQ0NzgzNzYwMlwiLFwia2V5X2lkXCI6XCIxXCIsXCJzaWduXCI6XCI5YjdlOWE3MlwifSIsInIiOiJodHRwczovL2h6LmxpYW5qaWEuY29tL2Vyc2hvdWZhbmcvMTAzMTEwOTQyNjI3Lmh0bWwiLCJvcyI6IndlYiIsInYiOiIwLjEifQ==";
        }else{
            cookie="lianjia_uuid=f21dc9c4-5f31-4469-88ae-04069cf591fe; _smt_uid=5eaa756d.cd03683; UM_distinctid=171c9dab35ebc1-0629d63bd7dc42-30607c00-13c680-171c9dab35f766; _ga=GA1.2.639112175.1588229488; Hm_lvt_9152f8221cb6243a53c83b956842be8a=1602224899; select_city=330100; digv_extends=%7B%22utmTrackId%22%3A%2221583074%22%7D; lianjia_ssid=5ffea509-3bfc-48b8-b417-b80e1e244b69; _gid=GA1.2.1954693181.1603862853; CNZZDATA1253492436=136417260-1588225333-https%253A%252F%252Fwww.baidu.com%252F%7C1603866356; CNZZDATA1254525948=79133001-1588228309-https%253A%252F%252Fwww.baidu.com%252F%7C1603866789; cy_ip=60.191.79.194; CNZZDATA1255633284=725074903-1588228688-https%253A%252F%252Fwww.baidu.com%252F%7C1603867495; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%22171c9dab4fce90-0840ef442be061-30607c00-1296000-171c9dab4fdaee%22%2C%22%24device_id%22%3A%22171c9dab4fce90-0840ef442be061-30607c00-1296000-171c9dab4fdaee%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E7%9B%B4%E6%8E%A5%E6%B5%81%E9%87%8F%22%2C%22%24latest_referrer%22%3A%22%22%2C%22%24latest_referrer_host%22%3A%22%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC_%E7%9B%B4%E6%8E%A5%E6%89%93%E5%BC%80%22%2C%22%24latest_utm_source%22%3A%22baidu%22%2C%22%24latest_utm_medium%22%3A%22pinzhuan%22%2C%22%24latest_utm_campaign%22%3A%22wyhangzhou%22%2C%22%24latest_utm_content%22%3A%22biaotimiaoshu%22%2C%22%24latest_utm_term%22%3A%22biaoti%22%7D%7D; f-token=WVfq5FCfn8xsOxrAZLJCTu7Q9kGg1H9S8Hvse9XJUdb6paEZ+MqlXATjISEdsKt0Fqg7dgHLKUFp4rZMaP6anDgqpzsGCmlMzZHjw38tZZIZITyz4mYxM1PMEEd77+SYBtw=; CNZZDATA1255604082=1132291929-1588225809-https%253A%252F%252Fwww.baidu.com%252F%7C1603871304; _gat=1; _gat_past=1; _gat_global=1; _gat_new_global=1; _gat_dianpu_agent=1; Hm_lpvt_9152f8221cb6243a53c83b956842be8a=1603871490; srcid=eyJ0Ijoie1wiZGF0YVwiOlwiMTdlYjBiMWQzNmE0ZDQzNmZhMWNlYzFjNTFjYzg0YjgxZmM2NmE4NTVkNDcyNDViMGU3MzdmNGY5NmU1ZWE2Y2E0ZmFjMmFhZmUzOGFjZjRkNzJkY2MyMDJiZDkxYzQ1MWY2NzBlYWEzZTA3MjlmN2FmMDRhNmMwZTdlZWE5ZTkwMTlhM2NhZWVlNTc5MzQ3NDM3Y2QzMWNjNmIxNDFkYjc2MDhkMDM3YzlmMGNkMjZkZmI5OWNhMjQwNzM0NzZiMGE5MzliZmIwNzczNzIzN2ZlYzJiN2NkOTY3MWI1YmRkYmFjNGE2MmQ2MGJmMmUwZTUzOWJhNDM3NmE2MTM5OTk0Mzg5YTNlN2RiNmU1N2NjYzI0OWM5N2E3MjRjMzg3MWM5M2Q2MDI2OWZjYWI2MmExODk5ZmJhZWQ2YzRlYzY4MzU3OTMyMjAxOWE2NjcyODc0NTM1ZTQ0NzgzNzYwMlwiLFwia2V5X2lkXCI6XCIxXCIsXCJzaWduXCI6XCI5YjdlOWE3MlwifSIsInIiOiJodHRwczovL2h6LmxpYW5qaWEuY29tL2Vyc2hvdWZhbmcvMTAzMTEwOTQyNjI3Lmh0bWwiLCJvcyI6IndlYiIsInYiOiIwLjEifQ==";
        }

        Request request = new Request.Builder().
                url(url)
                .addHeader("Host", "hz.lianjia.com")
                .addHeader("Upgrade-Insecure-Requests", "1")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .addHeader("Sec-Fetch-Mode", "navigate")
                .addHeader("Sec-Fetch-Site", "same-origin")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.92 Safari/537.36")
                .addHeader("Referer", "https://hz.lianjia.com/ershoufang/")
                .addHeader("Cookie", cookie)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                List<String> headers = response.headers("Set-Cookie");
                if(headers.size()>0){
                    for (String cookies : headers) {
                        String[] split = cookies.split(";");
                        if(split.length>0){
                            if(split[0].contains("ssid")){
                                String[] strs = split[0].split("=");
                                if(strs.length>1){
                                    bizlogger.info("current ssid ={}",strs[1]);
                                    redisCache.set(RedisIndexEnum.INDEX_0,"ssid",strs[1],RedisExpireEnum.EXPIRE_1D);
                                }
                            }
                        }
                        if(split[0].contains("extends")){
                            String[] strs = split[0].split("=");
                            if(strs.length>1){
                                bizlogger.info("current extends ={}",strs[1]);
                                redisCache.set(RedisIndexEnum.INDEX_0,"digv_extends",strs[1],RedisExpireEnum.EXPIRE_1D);
                            }
                        }
                    }
                }
                String html = response.body().string();
                return Jsoup.parse(html);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
//
//    public static List<HourseInfo> getLianjiaData(String zone, Integer pageNum) {
//        return null;
//    }

//    public static List<HourseInfo> getLianjiaData(String zone, Integer pageNum) {
//        return null;
//    }
//
//
//    public static Integer getLianjiaTotalCount(String zone) {
//        Integer totalCount = 0;
//        Document a5jData = get5a5jData(zone);
//        Elements elements = a5jData.select("div[class=total-box fl]");
//        if (elements.size() > 0) {
//            Element element = elements.get(0);
//            Elements spans = element.getElementsByTag("span");
//            if (spans.size() > 0) {
//                for (Element span : spans) {
//                    String countStr = span.text();
//                    totalCount = Integer.valueOf(countStr);
//                    break;
//                }
//            }
//        }
//        return totalCount;
//    }


}
