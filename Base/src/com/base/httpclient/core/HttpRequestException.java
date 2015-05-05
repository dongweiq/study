
package com.base.httpclient.core;

/**
 * Custom exception class that holds an {@link HttpResponse}. This allows
 * upstream code to receive an HTTP status code and any content received as well
 * as the underlying exception.
 * 
 * @author David M. Chandler
 */
public class HttpRequestException extends Exception {

    private static final long serialVersionUID = -2413629666163901633L;


    public static int TIME_OUT_EXCEPTION = 1;
    public static int OTHER_EXCEPTION = 2;
    
    private int exceptionCode;

    /**
     * Constructs the exception with
     * 
     * @param e
     * @param httpResponse
     */
    public HttpRequestException(Exception e,int exceptionCode) {
        super(e);
        this.exceptionCode = exceptionCode;
    }

    public int getExceptionCode(){
        return exceptionCode;
    }
    
    public boolean isTimeOutException(){
        return exceptionCode == TIME_OUT_EXCEPTION;
    }
}
