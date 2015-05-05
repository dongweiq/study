
package com.base.httpclient.core;

/**
 * Holds data for an HTTP request to be made with the attached HTTP client.
 * 
 * @author David M. Chandler
 */
public abstract class HttpMethod {

    public static final String URLENCODED = "application/x-www-form-urlencoded;charset=UTF-8";

    public static final String MULTIPART = "multipart/form-data";

    private String baseUrl = ""; // avoid null in URL

    protected MethodType methodType;

    protected String contentType;

    protected byte[] content;

    protected ParameterList params;

    public boolean isConnected;

    public boolean isUrlEncode = true;

    /**
     * Constructs a request with optional params appended to the query string.
     * 
     * @param baseUrl
     * @param params
     */
    public HttpMethod(String baseUrl, ParameterList params) {
        this.params = params;
        this.baseUrl = baseUrl;
    }
    
    public HttpMethod(String baseUrl, ParameterList params,boolean isUrlEncode) {
        this.params = params;
        this.baseUrl = baseUrl;
        this.isUrlEncode = isUrlEncode;
    }

    public String getRequestUrl() {
        switch (getMethodType()) {
            case POST:
            case PUT:
                return baseUrl;
            default:
                final StringBuilder result = new StringBuilder(baseUrl);
                if (params != null) {
                    // Add '?' if missing and add '&' if params already exist in
                    // base
                    // url
                    final int queryStart = baseUrl.indexOf('?');
                    final int lastChar = result.length() - 1;
                    if (queryStart == -1)
                        result.append('?');
                    else if (queryStart < lastChar && baseUrl.charAt(lastChar) != '&')
                        result.append('&');
                    result.append(params.urlEncode(isUrlEncode));
                }
                return result.toString();
        }
    }

    public MethodType getMethodType() {
        return methodType;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public ParameterList getParams() {
        return params;
    }


    static int[] fib = new int[20];
    static {
        // Compute Fibonacci series for backoff
        for (int i = 0; i < 20; i++) {
            fib[i] = i < 2 ? i : fib[i - 2] + fib[i - 1];
        }
    }

    /**
     * Implements exponential backoff using the Fibonacci series, which has the
     * effect of backing off with a multiplier of ~1.618 (the golden mean)
     * instead of 2, which is rather boring.
     * 
     * @param numTries Current number of attempts completed
     * @return Connection timeout in ms for next attempt
     */
    protected int getNextTimeout() {
        // For n=0,1,2,3 returns 1000,2000,3000,5000
        return 1000 * fib[numTries + 2];
    }

    int numTries;

}
