package com.corefac.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UploadUtil implements Serializable {
    private static final long serialVersionUID = 1742766304457363057L;
    private static final Logger logger = LoggerFactory.getLogger(UploadUtil.class);
    // 获取文件拓展名正则表达式
    private static Pattern fileTypePattern = Pattern.compile("\\.\\w+$");
    private static Pattern keyPattern = Pattern.compile("\\d+\\.\\w+$");

    public static String getFileKey(String fileName) {
        String formatFileName = TimestampUtil.getDatetimeString("yyyyMMddHHmmssSSS") + (int)(Math.random()*9000 +1000);
        if(fileName != null && !fileName.equals("")) {
            Matcher matcher = fileTypePattern.matcher(fileName);
            if(matcher.find())
                return formatFileName + matcher.group();
        }
        return formatFileName;
    }

    public static String getOSSKey(String fileUrl) {
        Matcher matcher = keyPattern.matcher(fileUrl);
        if(matcher.find()) return matcher.group();
        else return null;
    }
}
