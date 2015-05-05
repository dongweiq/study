
package com.base.httpclient.core;

import java.net.HttpURLConnection;

import android.annotation.SuppressLint;

/**
 * Lightweight HTTP client that facilitates GET, POST, PUT, and DELETE requests
 * using {@link HttpURLConnection}. Extend this class to support specialized
 * content and response types (see {@link BasicHttpClient} for an example). To
 * enable streaming, buffering, or other types of readers / writers, set an
 * alternate {@link RequestHandler}.
 * 
 * @author David M. Chandler
 */
@SuppressLint("NewApi")
public abstract class AbsHttpClient {

    private int maxRetries = 3;

    public static final String BOUNDARY = "------issmobile------";

    public static final String CONTENT_TYPE_URLENCODED = "application/x-www-form-urlencoded;charset=UTF-8";

    public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data; boundary=" + BOUNDARY;

    public static final String CRLF = "\r\n";

    /**
     * 连接超时时间，默认 2s
     */
    protected int connectionTimeout = 2000;

    /**
     * 读取数据超时时间，默认8s
     */
    protected int readTimeout = 8000;

    /**
     * Execute a HEAD request and return the response. The supplied parameters
     * are URL encoded and sent as the query string.
     * 
     * @param path
     * @param params
     * @return Response object
     * @throws HttpRequestException
     */
    public abstract HttpResponse head(String path, ParameterList params) throws HttpRequestException;

    /**
     * Execute a GET request and return the response. The supplied parameters
     * are URL encoded and sent as the query string.
     * 
     * @param path
     * @param params
     * @return Response object
     * @throws HttpRequestException
     */
    public abstract HttpResponse get(String path, ParameterList params) throws HttpRequestException;

    public abstract HttpResponse get(String path, ParameterList params,boolean isUrlEncode) throws HttpRequestException;
    /**
     * Execute a POST request with parameter map and return the response.
     * 
     * @param path
     * @param params
     * @return Response object
     * @throws HttpRequestException
     */
    public abstract HttpResponse post(String path, ParameterList params) throws HttpRequestException;

    /**
     * Execute a POST request with a chunk of data and return the response. To
     * include name-value pairs in the query string, add them to the path
     * argument or use the constructor in {@link HttpPost}. This is not a common
     * use case, so it is not included here.
     * 
     * @param path
     * @param contentType
     * @param data
     * @return Response object
     * @throws HttpRequestException
     */
    public abstract HttpResponse post(String path, String contentType, byte[] data)
            throws HttpRequestException;

    /**
     * Execute a PUT request with the supplied content and return the response.
     * To include name-value pairs in the query string, add them to the path
     * argument or use the constructor in {@link HttpPut}. This is not a common
     * use case, so it is not included here.
     * 
     * @param path
     * @param contentType
     * @param data
     * @return Response object
     * @throws HttpRequestException
     */
    public abstract HttpResponse put(String path, String contentType, byte[] data)
            throws HttpRequestException;

    /**
     * Execute a DELETE request and return the response. The supplied parameters
     * are URL encoded and sent as the query string.
     * 
     * @param path
     * @param params
     * @return Response object
     * @throws HttpRequestException
     */
    public abstract HttpResponse delete(String path, ParameterList params)
            throws HttpRequestException;

    /**
     * 当发生访问超时异常时，在指定访问次数的范围内，自动尝试再次访问
     * 
     * @param httpMethod
     * @return Response object, may be null
     * @throws HttpRequestException
     */
    public HttpResponse tryMany(HttpMethod httpMethod) throws HttpRequestException {
        HttpResponse res = null;
        while (httpMethod.numTries < maxRetries) {
            try {
                setConnectionTimeout(httpMethod.getNextTimeout());
                res = execute(httpMethod);
                if (res != null) {
                    return res;
                }
            } catch (HttpRequestException e) {
                if (e.isTimeOutException() && httpMethod.numTries < (maxRetries - 1)) {
                    continue;
                } else {
                    throw e;
                }
            } finally {
                httpMethod.numTries++;
            }
        }
        return null;
    }

    /**
     * Set maximum number of retries to attempt, capped at 18. On the 18th
     * retry, the connection timeout will be 4,181 sec = 1 hr 9 min.
     * 
     * @param maxRetries
     */
    public void setMaxRetries(int maxRetries) {
        if (maxRetries < 1 || maxRetries > 18) {
            throw new IllegalArgumentException("Maximum retries must be between 1 and 18");
        }
        this.maxRetries = maxRetries;
    }

    /**
     * 用指定的HttpMethod请求数据，默认使用BasicRequestHandler
     * 
     * @param httpMethod
     * @return
     * @throws HttpRequestException
     */
    protected HttpResponse execute(HttpMethod httpMethod) throws HttpRequestException {
        return execute(httpMethod, new BasicRequestHandler() {
        });
    }

    /**
     * 用指定的HttpMethod和RequestHandler请求数据
     * 
     * @param httpMethod
     * @param requestHandler
     * @return
     * @throws HttpRequestException
     */
    protected HttpResponse execute(HttpMethod httpMethod, RequestHandler requestHandler)
            throws HttpRequestException {

        HttpURLConnection uc = null;
        HttpResponse httpResponse = null;
        long startTime = System.currentTimeMillis();

        try {
            httpMethod.isConnected = false;
            uc = requestHandler.openConnection(httpMethod.getRequestUrl());
            uc.setConnectTimeout(connectionTimeout);
            uc.setReadTimeout(readTimeout);
            requestHandler.prepareConnection(uc, httpMethod);
            requestHandler.writeHeaders(uc, httpMethod);
            uc.connect(); 
            httpMethod.isConnected = true;
            if (uc.getDoOutput()) {
                requestHandler.writeStream(uc, httpMethod);
            }
            if (uc.getDoInput()) {
                httpResponse = requestHandler.readInputStream(uc);
            } else {
                httpResponse = new HttpResponse(uc, null);
            }
        } catch (Exception e) {
            if (isTimeoutException(startTime, httpMethod.isConnected)) {
                throw new HttpRequestException(e, HttpRequestException.TIME_OUT_EXCEPTION);
            } else {
                throw new HttpRequestException(e, HttpRequestException.OTHER_EXCEPTION);
            }
        } finally {
            if (uc != null) {
                uc.disconnect();
            }
            httpMethod.isConnected = false;
        }
        return httpResponse;
    }

    /**
     * Convenience method creates a new ParameterMap to hold query params
     * 
     * @return Parameter map
     */
    public ParameterList newParams() {
        return new ParameterList();
    }

    // /**
    // * Returns the {@link CookieManager} associated with this client.
    // *
    // * @return CookieManager
    // */
    // public static CookieManager getCookieManager() {
    // return (CookieManager) CookieHandler.getDefault();
    // }
    //
    // /**
    // * Initialize the app-wide {@link CookieManager}. This is all that's
    // * necessary to enable all Web requests within the app to automatically
    // send
    // * and receive cookies.
    // */
    // protected static void ensureCookieManager() {
    // if (CookieHandler.getDefault() == null) {
    // CookieHandler.setDefault(new CookieManager());
    // }
    // }

    /**
     * 判断是否超时
     * 
     * @param startTime
     * @param isConnected
     * @return
     */
    protected boolean isTimeoutException(long startTime, boolean isConnected) {
        long elapsedTime = System.currentTimeMillis() - startTime + 10; // fudge
        if (isConnected) {
            return elapsedTime >= readTimeout;
        } else {
            return elapsedTime >= connectionTimeout;
        }
    }

    /**
     * 设置连接超时时间
     * 
     * @param connectionTimeout
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * 设置读取数据超时时间
     * 
     * @param readTimeout
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

}
