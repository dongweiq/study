
package com.base.httpclient.core;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.util.Log;

import com.base.httpclient.core.ParameterList.Parameter;

/**
 * Parameter map knows offers convenience methods for chaining add()s as well as
 * URL encoding.
 * 
 * @author David M. Chandler
 */
public class ParameterList extends ArrayList<Parameter> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static abstract class Parameter {
        public String name;
    }

    public static class StringParameter extends Parameter {
        public String value;

        public StringParameter(String name, String value) {
            super();
            if (name == null || value == null) {
                throw new RuntimeException("args can not be null");
            }
            this.name = name;
            this.value = value;
        }

    }

    public static class InputStreamParameter extends Parameter {
        public InputStream value;

        public String fileName;

        public InputStreamParameter(String name, InputStream value, String fileName) {
            super();
            if (name == null || value == null || fileName == null) {
                throw new RuntimeException("args can not be null");
            }
            this.name = name;
            this.value = value;
            this.fileName = fileName;
        }

    }

    public static class FileParameter extends Parameter {
        public File value;

        public FileParameter(String name, File value) {
            super();
            if (name == null || value == null) {
                throw new RuntimeException("args can not be null");
            }
            this.name = name;
            this.value = value;
        }
    }

    public static class HeaderParameter extends Parameter {
        public String value;

        public HeaderParameter(String name, String value) {
            super();
            if (name == null || value == null) {
                throw new RuntimeException("args can not be null");
            }
            this.name = name;
            this.value = value;
        }
    }

    /**
     * Returns URL encoded data
     * 
     * @return URL encoded String
     */
    public String urlEncode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            Parameter param = get(i);
            if (param instanceof StringParameter) {
                StringParameter stringParam = (StringParameter) param;
                if (i > 0) {
                    sb.append("&");
                }
                sb.append(stringParam.name);
                sb.append("=");
                try {
                    sb.append(URLEncoder.encode(stringParam.value, RequestHandler.UTF8));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    public String urlEncode(boolean isEncode) {
        StringBuilder sb = new StringBuilder();
        if (isEncode) {// 编码
            for (int i = 0; i < size(); i++) {
                Parameter param = get(i);
                if (param instanceof StringParameter) {
                    StringParameter stringParam = (StringParameter) param;
                    if (i > 0) {
                        sb.append("&");
                    }
                    sb.append(stringParam.name);
                    sb.append("=");
                    try {
                        sb.append(URLEncoder.encode(stringParam.value, RequestHandler.UTF8));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
//            return sb.toString().substring(1, sb.length());
            return sb.toString();
        } else {// 非编码
            for (int i = 0; i < size(); i++) {
                Parameter param = get(i);
                if (param instanceof StringParameter) {
                    StringParameter stringParam = (StringParameter) param;
                    sb.append(stringParam.value);
                }
            }
            return sb.toString();
        }

    }
    /**
     * Return a URL encoded byte array in UTF-8 charset.
     * 
     * @return URL encoded bytes
     */
    public byte[] urlEncodedBytes() {
        byte[] bytes = null;
        try {
            bytes = urlEncode().getBytes(RequestHandler.UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bytes;
    }
    
    public boolean hasMultiPart(){
        for (int i = 0; i < size(); i++) {
            Parameter param = get(i);
            if((param instanceof InputStreamParameter)||(param instanceof FileParameter)){
                return true;
            }
        }
        return false;
    }

    public ArrayList<HeaderParameter> getHeaderParams() {
        ArrayList<HeaderParameter> list = new ArrayList<HeaderParameter>();
        for (int i = 0; i < size(); i++) {
            Parameter param = get(i);
            if (param instanceof HeaderParameter) {
                list.add((HeaderParameter) param);
            }
        }
        return list;
    }

}
