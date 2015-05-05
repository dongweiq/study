package com.base.httpclient.core;

/**
 * An HTTP PUT request.
 * 
 * @author David M. Chandler
 */
public class HttpPut extends HttpMethod {

    /**
     * Constructs an HTTP PUT request with name-value pairs to
     * be sent in the request BODY.
     * 
     * @param path Partial URL
     * @param params Name-value pairs to be sent in request BODY
     */
    public HttpPut(String path, ParameterList params) {
        super(path, params);
        this.methodType = MethodType.PUT;
        this.contentType = URLENCODED;
        if (params != null) {
            this.content = params.urlEncodedBytes();
        }
    }
    
    /**
     * Constructs an HTTP PUT request with arbitrary content.
     * 
     * @param path Partial URL
     * @param params Optional, appended to query string
     * @param contentType MIME type
     * @param data Content to be sent in the request body
     */
    public HttpPut(String path, ParameterList params, String contentType, byte[] data) {
        super(path, params);
        this.methodType = MethodType.PUT;
        this.contentType = contentType;
        this.content = data;
    }

}
