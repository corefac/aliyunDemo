package com.corefac.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileUtils {

    public static String uploadFile(String ex, String path) throws Exception {

        String fileName = "/appLog.txt";
        File logFile = new File(path);
        if (!logFile.isDirectory()) {
            logFile.mkdir();
        }
        FileWriter fw = null;
        try {
            // 如果文件存在，则追加内容；如果文件不存在，则创建文件
            File f = new File(logFile + fileName);
            fw = new FileWriter(f, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println("APP异常内容");
            pw.println(ex);
            pw.flush();
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

}
