package com.base.httpclient.core;

/**
 * An HTTP POST request.
 * 
 * @author David M. Chandler
 */
public class HttpPost extends HttpMethod {

    /**
     * Constructs an HTTP POST request with name-value pairs to
     * be sent in the request BODY.
     * 
     * @param path Partial URL
     * @param params Name-value pairs to be sent in request BODY
     */
    public HttpPost(String path, ParameterList params) {
        super(path, params);
        this.methodType = MethodType.POST;
        this.contentType = URLENCODED;
        if (params != null) {
            this.content = params.urlEncodedBytes();
        }
    }
    
    /**
     * Constructs an HTTP POST request with arbitrary content.
     * If params is non-null, the name-value pairs will be appended to the QUERY STRING
     * while the content is sent in the request BODY.
     * This is not a common use case and is therefore not represented in the post()
     * methods in {@link AbsHttpClient} or {@link AsyncHttpClient}, 
     * but is nevertheless possible using this constructor.
     * 
     * @param path Partial URL
     * @param params Optional name-value pairs to be appended to QUERY STRING
     * @param contentType MIME type
     * @param data Content to be sent in the request body
     */
    public HttpPost(String path, ParameterList params, String contentType, byte[] data) {
        super(path, params);
        this.methodType = MethodType.POST;
        this.contentType = contentType;
        this.content = data;
    }

}
