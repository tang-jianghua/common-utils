package com.jh.utils.http;


import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author tangjianghua
 * @date 2020/7/6
 */
public class HttpClient {


    enum Method {
        GET,
        POST
    }

    private String urlStr;

    private int timeout = 60000;

    private Method method = Method.GET;

    private Map<String, String> headers;

    private String charsets = "utf-8";

    private String requestBody;


    public String getUrlStr() {
        return urlStr;
    }

    public void setUrlStr(String urlStr) {
        this.urlStr = urlStr;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getCharsets() {
        return charsets;
    }

    public void setCharsets(String charsets) {
        this.charsets = charsets;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public void addHeader(String key,String value){
        if(this.headers==null){
            headers=new HashMap<>();
        }
        String old = headers.get(key);
        if(old!=null){
            value =old+";"+value;
        }
        headers.put(key,value);
    }

    public String doConnect() {
        final URL url;
        try {
            url = new URL(this.urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        //添加tls安全套接层协议
        if ("https".equalsIgnoreCase(urlStr.substring(0, 5))) {
            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new X509TrustManager[]{CustomerX509TrustManager.getInstance()}, new SecureRandom());
            } catch (GeneralSecurityException gse) {
                gse.printStackTrace();
            }
            if (sslContext != null)
                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((s, sslSession) -> true);
        }
        //打开链接
        HttpURLConnection httpURLConnection;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(timeout);
            httpURLConnection.setReadTimeout(timeout);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            //添加请求头
            if (!headers.isEmpty()) {
                Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> next = iterator.next();
                    httpURLConnection.setRequestProperty(next.getKey(), next.getValue());
                }
            }
            httpURLConnection.setRequestMethod(method.name());
        } catch (ProtocolException e) {
            e.printStackTrace();
            return null;
        }
        if (method == Method.POST) {

            httpURLConnection.setDoOutput(true);
            try {
                final OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(requestBody.getBytes(charsets));
                outputStream.flush();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        try {
            int statusCode = httpURLConnection.getResponseCode();
            if (statusCode < HttpURLConnection.HTTP_OK || statusCode >= HttpURLConnection.HTTP_MULT_CHOICE) {
                throw new Exception("失败返回码[" + statusCode + "]");
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            InputStream inputStream = httpURLConnection.getInputStream();

            byte[] bytes = new byte[1024];
            while (inputStream.read(bytes) != -1) {
                byteArrayOutputStream.write(bytes);
            }
            inputStream.close();
            byte[] array = byteArrayOutputStream.toByteArray();
            return new String(array);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
