package com.example.utilTest.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 获取app和apk的签名信息
 * Get the signature information of app and apk
 */
public class AutographUtil {

    private static final String TAG = AutographUtil.class.getSimpleName();

    public static AutographUtil getInstance(){
        return new AutographUtil();
    }

    private AutographUtil() {
    }

    /**
     * 从APK中读取签名
     * @param file
     * @return
     * @throws IOException
     */
    public String getSignaturesFromApk(File file) {
        List<String> signatures=new ArrayList<String>();
        JarFile jarFile;
        try {
            jarFile=new JarFile(file);
            JarEntry je=jarFile.getJarEntry("AndroidManifest.xml");
            byte[] readBuffer=new byte[8192];
            Certificate[] certs=loadCertificates(jarFile, je, readBuffer);
            if(certs != null) {
                for(Certificate c: certs) {
                    String sig=toCharsString(c.getEncoded());
                    signatures.add(sig);
                }
            }
        } catch(Exception ex) {
            ExceptionUtil.printStackTrace(ex);
        }
        if (signatures.size() > 0){
            return signatures.get(0);
        }
        return null;
    }

    /**
     * 加载签名
     * @param jarFile
     * @param je
     * @param readBuffer
     * @return
     */
    private static Certificate[] loadCertificates(JarFile jarFile, JarEntry je, byte[] readBuffer) {
        try {
            InputStream is=jarFile.getInputStream(je);
            while(is.read(readBuffer, 0, readBuffer.length) != -1) {
            }
            is.close();
            return je != null ? je.getCertificates() : null;
        } catch(IOException e) {
            ExceptionUtil.printStackTrace(e);
        }
        return null;
    }

    /**
     * 将签名转成转成可见字符串
     * @param sigBytes
     * @return
     */
    private static String toCharsString(byte[] sigBytes) {
        byte[] sig=sigBytes;
        final int N=sig.length;
        final int N2=N * 2;
        char[] text=new char[N2];
        for(int j=0; j < N; j++) {
            byte v=sig[j];
            int d=(v >> 4) & 0xf;
            text[j * 2]=(char)(d >= 10 ? ('a' + d - 10) : ('0' + d));
            d=v & 0xf;
            text[j * 2 + 1]=(char)(d >= 10 ? ('a' + d - 10) : ('0' + d));
        }
        return new String(text);
    }

    public String getSign(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
        Iterator<PackageInfo> iter = apps.iterator();
        while(iter.hasNext()) {
            PackageInfo packageinfo = iter.next();
            String packageName = packageinfo.packageName;
            if (packageName.equals(context.getPackageName())) {
                Log.i(TAG, packageinfo.signatures[0].toCharsString());
                return packageinfo.signatures[0].toCharsString();
            }
        }
        return null;
    }

    public void getSingInfo(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            parseSignature(sign.toByteArray());
        } catch (Exception e) {
            ExceptionUtil.printStackTrace(e);
        }
    }
    public void parseSignature(byte[] signature) {
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));
            String pubKey = cert.getPublicKey().toString();
            String signNumber = cert.getSerialNumber().toString();
            System.out.println("signName:" + cert.getSigAlgName());
            System.out.println("pubKey:" + pubKey);
            System.out.println("signNumber:" + signNumber);
            System.out.println("subjectDN:"+cert.getSubjectDN().toString());
        } catch (Exception e) {
            ExceptionUtil.printStackTrace(e);
        }
    }

    /**
     * 从 apk 中获取 MD5 签名信息
     *
     * @param apkPath
     * @return
     * @throws Exception
     */
    public static String getApkSignatureMD5(String apkPath) {
        byte[] bytes = getSignaturesFromApk(apkPath);
        String sign = hexDigest(bytes, "MD5");
        return sign;
    }

    public static String getApkSignatureSHA1(String apkPath) {
        byte[] bytes = getSignaturesFromApk(apkPath);
        String sign = hexDigest(bytes, "SHA1");
        return sign;
    }

    public static String getApkSignatureSHA256(String apkPath) {
        byte[] bytes = getSignaturesFromApk(apkPath);
        String sign = hexDigest(bytes, "SHA256");
        return sign;
    }

    /**
     * 获取已经安装的 app 的 MD5 签名信息
     *
     * @param context
     * @param pkgName
     * @return
     */
    public static String getAppSignatureMD5(Context context, String pkgName) {
        return getAppSignature(context, pkgName, "MD5");
    }

    public static String getAppSignatureSHA1(Context context, String pkgName) {
        return getAppSignature(context, pkgName, "SHA1");
    }

    public static String getAppSignatureSHA256(Context context, String pkgName) {
        return getAppSignature(context, pkgName, "SHA256");
    }

    public static String getAppSignature(Context context, String pkgName, String algorithm) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    pkgName, PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            String signStr = hexDigest(sign.toByteArray(), algorithm);
            return signStr;
        } catch (PackageManager.NameNotFoundException e) {
            ExceptionUtil.printStackTrace(e);
        }
        return "";
    }

    /**
     * 从APK中读取签名
     *
     * @param apkPath
     * @return
     * @throws IOException
     */
    public static byte[] getSignaturesFromApk(String apkPath){
        File file;
        JarFile jarFile;
        try {
            file = new File(apkPath);
            jarFile = new JarFile(file);
            JarEntry je = jarFile.getJarEntry("AndroidManifest.xml");
            byte[] readBuffer = new byte[8192];
            Certificate[] certs = loadCertificates(jarFile, je, readBuffer);
            if (certs != null) {
                for (Certificate c : certs) {
                    return c.getEncoded();
                }
            }
        } catch (Exception ex) {
            ExceptionUtil.printStackTrace(ex);
        }
        return null;
    }

    public static String hexDigest(byte[] bytes, String algorithm) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        byte[] md5Bytes = md5.digest(bytes);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

	//获取当前应用安装后的CERT.RSA、CERT.SF、MANIFEST.MF等文件
    public static void getFileContent(Context context) {
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();

                if (entryName.startsWith("META-INF/CERT.RSA")) { //xxx 表示要读取的文件名
                    //利用ZipInputStream读取文件
                    long size = entry.getSize();
                    if (size > 0) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(zipfile.getInputStream(entry)));
                        String line;
                        while ((line = br.readLine()) != null) {  //文件内容都在这里输出了，根据你的需要做改变
                            Log.i(TAG,line);
                        }
                        br.close();
                    }
                }
                if (entryName.startsWith("META-INF/CERT.SF")) { //xxx 表示要读取的文件名
                    //利用ZipInputStream读取文件
                    long size = entry.getSize();
                    if (size > 0) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(zipfile.getInputStream(entry)));
                        String line;
                        while ((line = br.readLine()) != null) {  //文件内容都在这里输出了，根据你的需要做改变
                            Log.i(TAG,line);
                        }
                        br.close();
                    }
                }
                if (entryName.startsWith("META-INF/MANIFEST.MF")) { //xxx 表示要读取的文件名
                    //利用ZipInputStream读取文件
                    long size = entry.getSize();
                    if (size > 0) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(zipfile.getInputStream(entry)));
                        String line;
                        while ((line = br.readLine()) != null) {  //文件内容都在这里输出了，根据你的需要做改变
                            Log.i(TAG,line);
                        }
                        br.close();
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
