package com.example.utilTest.net;

import com.example.utilTest.utils.Log;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {
    private static final String TAG = HttpClientUtil.class.getSimpleName();
    private static URL url;
    private static HttpURLConnection connection;
    public static final String REQUEST_GET= "GET";
    public static final String REQUEST_POST= "POST";

    private HttpClientUtil() {
    }

    public static HttpClientUtil create(String urlstr) throws Exception{
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        //首先需要获取到HttpURLConnection的实例,一般只需要new 出一个URL对象,并传入目标的网络地址,然后调用一下openConnection()方法即可,
        url = new URL(urlstr);
        connection = (HttpURLConnection) url.openConnection();
        return httpClientUtil;
    }

    /**
     *
     * @param method 请求方式GET/POST
     * @return 返回请求结果
     * @throws Exception
     */
    private static String request(String method) throws Exception{
        if (connection == null){
            return null;
        }
         //在得到了HttpURLConnection的实例之后,可以设置一下HTTP请求所使用的方法。常用的两个方法为： POST和GET .GET表示希望从服务器那里获取到数据,而POST则表示希望提交数据给服务器。
        connection.setRequestMethod(method);
        //接下来就可以进行一些自由的定制了,比如设置连接超时、读取超时的毫秒数，以及服务器希望得到的一些消息头等，这部分是可以根据自己的需求情况写的
        connection.setConnectTimeout(6000);
        connection.setReadTimeout(6000);
        //之后再调用getInputStream()方法就可以获取到服务器返回的输入流了,剩下的任务就是对输入流进行读取
        InputStream in = connection.getInputStream();
        StringBuilder stringBuffer = new StringBuilder();
        byte[] bytes = new byte[2048];
        int len = 0;
        while ((len = in.read(bytes)) != -1){
            stringBuffer.append(new String(bytes,0,len));
        }
        return stringBuffer.toString();
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public static String get() throws Exception{
        return request(REQUEST_GET);
    }

    /**
     *
     * @param param post的参数 xx=xx&yy=yy
     * @return
     * @throws Exception
     */
    public static String post(String param) throws Exception{
        // 发送POST请求必须设置如下两行
        connection.setDoOutput(true);
        connection.setDoInput(true);
        // 获取URLConnection对象对应的输出流
        PrintWriter printWriter = new PrintWriter(connection.getOutputStream());
        // 发送请求参数
        printWriter.write(param);//post的参数 xx=xx&yy=yy
        // flush输出流的缓冲
        printWriter.flush();
        //获得cookie中的数据
        Map<String, List<String>> cookies = connection.getHeaderFields();
        List<String> setCookies = cookies.get("Set-Cookie");
        Log.i(TAG,setCookies.toString());
        return request(REQUEST_POST);
    }

    public static void destroy(){
        //最后可以调用disconnect()这个方法将这个HTTP连接关闭掉
        if (connection == null){
            return;
        }
        connection.disconnect();
    }
}
