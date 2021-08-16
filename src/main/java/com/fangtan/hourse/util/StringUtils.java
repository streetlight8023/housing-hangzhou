package com.fangtan.hourse.util;

import com.google.common.base.Strings;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.math.BigDecimal;

public class StringUtils {

    public static BigDecimal removeChinese(String str){
        String string="";
        for (int i = 0; i < str.length(); i++){
            String str0="";
            if (str.substring(i, i + 1).matches("[\u4e00-\u9fa5]+")){
            }else{
                str0 = str.substring(i, i + 1) + "";
            }
            string +=str0;
        }
        if(!Strings.isNullOrEmpty(string)){
            return new BigDecimal(Double.valueOf(string)).setScale(2, BigDecimal.ROUND_UP);
        }
        return new BigDecimal(0);
    }

    public static String getPingYin(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        String output = "";
        if (inputString != null && inputString.length() > 0
                && !"null".equals(inputString)) {
            char[] input = inputString.trim().toCharArray();
            try {
                for (int i = 0; i < input.length; i++) {
                    if (java.lang.Character.toString(input[i]).matches(
                            "[\\u4E00-\\u9FA5]+")) {
                        String[] temp = PinyinHelper.toHanyuPinyinStringArray(
                                input[i], format);
                        output += temp[0];
                    } else{
                        output += java.lang.Character.toString(input[i]);
                    }

                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        } else {
            return "*";
        }
        return output;
    }
}
