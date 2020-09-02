package com.baidu.shop.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName PinyinUtil
 * @Description: TODO
 * @Author lihongyang
 * @Date 2020/9/1
 * @Version V1.0
 **/
public class PinyinUtil {

    public static final Boolean TO_FUUL_PINYIN = true;

    public static final Boolean TO_FIRST_CHAR_PINYIN = false;

    //-------------------------获取汉字首字母或全拼大写字母-------------------------------------
    //isFull是否全拼
    public static String getUpperCase(String chinese, boolean isFull) {
        return convertHanzi2Pinyin(chinese, isFull).toUpperCase();
    }

    public static String getLowerCase(String chinese, boolean isFull) {
        return convertHanzi2Pinyin(chinese, isFull).toLowerCase();
    }

    //----------------------------将汉字转成拼音----------------------------------------------
    private static String convertHanzi2Pinyin(String hanzi, boolean isFull) {
        /***
         * ^[\u2E80-\u9FFF]+$ 匹配所有东亚区的语言
         * ^[\u4E00-\u9FFF]+$ 匹配简体和繁体
         * ^[\u4E00-\u9FA5]+$ 匹配简体
         */
        String regExp = "^[\u4E00-\u9FFF]+$";
        StringBuffer sb = new StringBuffer();
        if (hanzi == null || "".equals(hanzi.trim())) {
            return "";
        }
        String pinyin = "";
        for (int i = 0; i < hanzi.length(); i++) {
            char unit = hanzi.charAt(i);
            //是汉字，则转拼音
            if (match(String.valueOf(unit), regExp)) {
                pinyin = convertSingleHanzi2Pinyin(unit);
                if (isFull) {
                    sb.append(pinyin);
                } else {
                    sb.append(pinyin.charAt(0));
                }
            } else {
                sb.append(unit);
            }
        }
        return sb.toString();
    }

    //将单个汉字转成拼音
    private static String convertSingleHanzi2Pinyin(char hanzi) {
        HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
        outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        String[] res;
        StringBuffer sb = new StringBuffer();
        try {
            res = PinyinHelper.toHanyuPinyinStringArray(hanzi, outputFormat);
            sb.append(res[0]);//对于多音字，只用第一个拼音
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return sb.toString();
    }

    //匹配 根据字符和正则表达式进行匹配
    private static boolean match(String str, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

}
