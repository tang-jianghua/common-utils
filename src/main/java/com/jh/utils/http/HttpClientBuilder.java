package com.jh.utils.http;

import java.util.Map;

/**
 * @author tangjianghua
 * @date 2020/7/6
 */
public class HttpClientBuilder {

    HttpClient httpClient = new HttpClient();

    public HttpClientBuilder setMethod(HttpClient.Method method) {
        httpClient.setMethod(method);
        return this;
    }

    public HttpClientBuilder setHeaders(Map<String, String> headers) {
        httpClient.setHeaders(headers);
        return this;
    }
    public HttpClientBuilder addHeader(String key,String value) {
        httpClient.addHeader(key,value);
        return this;
    }

    public HttpClientBuilder setCharsets(String charsets) {
        httpClient.setCharsets(charsets);
        return this;
    }

    public HttpClientBuilder setRequestBody(String requestBody) {
        httpClient.setRequestBody(requestBody);
        return this;
    }

    public HttpClientBuilder setTimeout(int timeout) {
        httpClient.setTimeout(timeout);
        return this;
    }

    public HttpClient build(String url) {
        httpClient.setUrlStr(url);
        return httpClient;
    }

    public HttpClient buildPostJson(String url) {
        httpClient.setUrlStr(url);
        httpClient.addHeader("contentType","application/json");
        return httpClient;
    }
}
