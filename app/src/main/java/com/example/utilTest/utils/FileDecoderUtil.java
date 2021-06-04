package com.example.utilTest.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * 解压缩工具类
 */
public class FileDecoderUtil {
    private static final String TAG = FileDecoderUtil.class.getSimpleName();
    //过滤在mac上压缩时自动生成的__MACOSX文件夹
    private static final String MAC_IGNORE = "__MACOSX/";

    /**
     *
     * @param target 目标文件路径
     * @param source  解压缩完成后存放位置
     */
    public static void decompressFile(String target, String source) {
        if(TextUtils.isEmpty(target)){
            return;
        }
        LogUtil.i(TAG,"decode start time :"+System.currentTimeMillis());
        try {
            File file = new File(source);
            if(!file.exists()) {
                return;
            }
            ZipFile zipFile = new ZipFile(file);
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
            ZipEntry zipEntry = null;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String fileName = zipEntry.getName();
                if(fileName != null && fileName.contains(MAC_IGNORE)) {
                    continue;
                }
                File temp = new File(target + File.separator + fileName);
                if(zipEntry.isDirectory()) {
                    File dir = new File(target + File.separator + fileName);
                    dir.mkdirs();
                    continue;
                }
                if (temp.getParentFile() != null && !temp.getParentFile().exists()) {
                    temp.getParentFile().mkdirs();
                }
                byte[] buffer = new byte[1024];
                OutputStream os = new FileOutputStream(temp);
                // 通过ZipFile的getInputStream方法拿到具体的ZipEntry的输入流
                InputStream is = zipFile.getInputStream(zipEntry);
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
                os.close();
                is.close();
            }
            zipInputStream.close();
            LogUtil.i(TAG,"decode end time :"+System.currentTimeMillis());
        } catch (Exception e) {
            ExceptionUtil.printStackTrace(e);
        }
    }
}
