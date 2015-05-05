package com.base.httpclient.core;

/**
 * An HTTP GET request.
 * 
 * @author David M. Chandler
 */
public class HttpGet extends HttpMethod {

    /**
     * Constructs an HTTP GET request.
     * 
     * @param path Partial URL
     * @param params Name-value pairs to be appended to the URL
     */
    public HttpGet(String path, ParameterList params) {
        super(path, params);
        this.methodType = MethodType.GET;
    }

    public HttpGet(String path, ParameterList params, boolean isUrlEncode) {
        super(path, params, isUrlEncode);
        this.methodType = MethodType.GET;
    }
}
