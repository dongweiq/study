package com.base.httpclient.core;

/**
 * An HTTP DELETE request.
 * 
 * @author David M. Chandler
 */
public class HttpDelete extends HttpMethod {

    /**
     * Constructs an HTTP DELETE request.
     * 
     * @param path Partial URL
     * @param params Name-value pairs to be appended to the URL
     */
    public HttpDelete(String path, ParameterList params) {
        super(path, params);
        this.methodType = MethodType.DELETE;
    }

}
