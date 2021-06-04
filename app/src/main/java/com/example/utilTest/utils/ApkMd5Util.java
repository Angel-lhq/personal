package com.example.utilTest.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Locale;

/**
 *计算apk的md5值
 * Calculate the MD5 value of APK
 */
public class ApkMd5Util {

    public static String getAPKMD5(Context context) {
        String md5 = "";
        String path = context.getApplicationContext().getPackageResourcePath();
        File file = new File(path);
        try {
            md5 = getFileMD5String(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5;
    }

    public static String getFileMD5String(File file) throws Exception {
        InputStream fis;
        MessageDigest messagedigest = MessageDigest.getInstance("MD5");
        fis = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int numRead = 0;
        while ((numRead = fis.read(buffer)) > 0) {
            messagedigest.update(buffer, 0, numRead);
        }
        fis.close();
        return byte2Hex(messagedigest.digest());
    }

    public static String byte2Hex(byte[] b) {
        if (null == b)
            return null;
        StringBuffer sBuffer = new StringBuffer();
        String sTmep;
        for (int i = 0; i < b.length; i++) {
            sTmep = Integer.toHexString(b[i] & 0xFF);
            if (sTmep.length() == 1)
                sBuffer.append("0");
            sBuffer.append(sTmep.toUpperCase(Locale.getDefault()));
        }
        return sBuffer.toString();
    }

}
