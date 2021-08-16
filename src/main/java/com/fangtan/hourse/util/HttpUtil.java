package com.fangtan.hourse.util;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

@Slf4j
public class HttpUtil {

    public static final String ACCEPT_XWWW  = "application/x-www-form-urlencoded";
    public static final String ACCEPT_JSON  = "application/json";
    public static final String ACCEPT_BEAN  = "application/x-java-serialized-object";
    public static final String ACCEPT_FORM  = "multipart/form-data";
    public static final String ACCEPT_XML   = "text/xml";
    public static final String ACCEPT_RAW   = "text/plain";

    public static String doDelete(String url) {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", ACCEPT_XWWW);
        headers.put("Charset", "UTF-8");
        return invoke(url, null, headers, HttpMethod.DELETE.name());
    }

    public static String doGet(String url, String contentType){
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", contentType);
        headers.put("Charset", "UTF-8");
        return invoke(url, null, headers, HttpMethod.GET.name());
    }

    public static String doGet(String url, Map<String, String> params, String contentType){
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", contentType);
        headers.put("Charset", "UTF-8");
        return invoke(url, map2String(params), headers, HttpMethod.GET.name());
    }

    public static String doGet(String url, Map<String, String> params, Map<String, String> headers){
        return invoke(url, map2String(params), headers, HttpMethod.GET.name());
    }

    public static String doPost(String url, String contentType){
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", contentType);
        headers.put("Charset", "UTF-8");
        return invoke(url, null, headers, HttpMethod.POST.name());
    }

    public static String doPost(String url, Map<String, String> params, String contentType, String accept){
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", contentType);
        headers.put("Accept", accept);
        headers.put("Charset", "UTF-8");
        return invoke(url, map2String(params), headers, HttpMethod.POST.name());
    }

    public static String doPost(String url, Map<String, String> params, String token , String contentType, String accept){
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", contentType);
        headers.put("token", token);
        headers.put("Accept", accept);
        headers.put("Charset", "UTF-8");
        return invoke(url, map2String(params), headers, HttpMethod.POST.name());
    }

    public static String doPost(String url, Map<String, String> params, String contentType){
        return doPost(url, params, contentType, contentType);
    }

    public static String doPost(String url, Map<String, String> params, Map<String, String> headers, String contentType){
        return invoke(url, map2String(params), headers, HttpMethod.POST.name());
    }

    public static String doPost(String url, String params, String contentType){
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", contentType);
        headers.put("Accept", contentType);
        return invoke(url, params, headers, HttpMethod.POST.name());
    }

    public static String invoke(String url, String request, Map<String, String> headers, String method){
        URL _url = null;
        HttpURLConnection _connection = null;
        BufferedReader _input = null;
        StringBuffer result = new StringBuffer();
        int httpStatus = 0;
        try {
            _url = new URL(url);
            _connection = (HttpURLConnection) _url.openConnection();
            if (headers!=null){
                for (String key:headers.keySet()){
                    _connection.setRequestProperty(key, headers.get(key));
                }
                _connection.setRequestProperty("Charset", "UTF-8");
            } else {
                _connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            }
            _connection.setRequestProperty("Connection", "keep-alive");
            _connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.81 Safari/537.36");
            _connection.setConnectTimeout(60000);
            _connection.setReadTimeout(60000);
            _connection.setUseCaches(false);                // 不能使用缓存，防止重复提交
            _connection.setDoOutput(true);                  // Post请求往往需要向服务器端发送数据参数，所以需要setDoInput(true)
            _connection.setRequestMethod(method);    // 总是要通过getInputStream()从服务端获得响应所以setDoInput()默认是true；
            OutputStream outputStream = _connection.getOutputStream();
            outputStream.write(request == null ? new byte[0] : request.getBytes(), 0, request == null ? 0 : request.getBytes().length);
            outputStream.flush();
            outputStream.close();

            httpStatus = _connection.getResponseCode();
            _input = new BufferedReader(new InputStreamReader(_connection.getInputStream()));
            String s = null;
            while ((s = _input.readLine()) != null)
                result.append(s);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.append("请求异常!");
        } finally {
            try {
                if (_input != null) _input.close();
                if (_connection != null) _connection.disconnect();
            } catch (IOException ioe){
                ioe.printStackTrace();
                result.append(ioe.getMessage());
            }
        }

        log.info("{} CURL {}:{} param:{} resp:{}", httpStatus, method, url,
                StringUtil.cutOut(request == null ? "" : request, 1024),
                StringUtil.cutOut(result.toString(), 10240));
        return result.toString();
    }

    private static String map2String(Map<String, String> params){
        StringBuilder sb = new StringBuilder();
        if (params!=null){
            for (String key:params.keySet()){
                String value = params.get(key);
                if (value.contains("&")){
                    try {
                        value = URLEncoder.encode(value, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                sb.append(key+"="+value+"&");
            }
        }
        return sb.toString();
    }

}
