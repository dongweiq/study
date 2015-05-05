
package com.base.httpclient.core;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.base.httpclient.core.ParameterList.FileParameter;
import com.base.httpclient.core.ParameterList.HeaderParameter;
import com.base.httpclient.core.ParameterList.InputStreamParameter;
import com.base.httpclient.core.ParameterList.Parameter;
import com.base.httpclient.core.ParameterList.StringParameter;

/**
 * Default {@link RequestHandler} used by {@link BasicHttpClient}. It is
 * intended to be used for simple requests with small amounts of data only (a
 * few kB), as it does no buffering, chunking, streaming, etc. Only character
 * set supported is UTF-8. Only {@link String} content is supported. All
 * responses are treated as {@link String}s. This class is abstract so that it
 * can be easily extended in an anonymous inner class when constructing a
 * client.
 * 
 * @author David M. Chandler
 */
public abstract class BasicRequestHandler implements RequestHandler {

    public BasicRequestHandler() {
    }

    @Override
    public HttpURLConnection openConnection(String urlString) throws IOException {
        try {
            URL url = new URL(urlString);
            if(url.toString().contains("https")){
            	HTTPSTrustManager.allowAllSSL();
            }
            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
            return uc;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(urlString + " is not a valid URL", e);
        }
    }

    @Override
    public void prepareConnection(HttpURLConnection urlConnection, HttpMethod httpMethod)
            throws IOException {
        // Configure connection for request method
        urlConnection.setDoOutput(httpMethod.getMethodType().getDoOutput());
        urlConnection.setDoInput(httpMethod.getMethodType().getDoInput());
        urlConnection.setRequestMethod(httpMethod.getMethodType().getMethodName());

        boolean hasMultiPart = httpMethod.params == null ? false : httpMethod.params.hasMultiPart();
        // Set additional properties
        if (hasMultiPart) {
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Cache-Control", "no-cache");
            urlConnection.setRequestProperty("Content-type", AbsHttpClient.CONTENT_TYPE_MULTIPART);
        } else if (httpMethod.getContentType() != null) {
            urlConnection.setRequestProperty("Content-Type", httpMethod.getContentType());
        }
        urlConnection.setRequestProperty("Accept-Charset", UTF8);
    }

    @Override
    public OutputStream openOutput(HttpURLConnection urlConnection) throws IOException {
        return urlConnection.getOutputStream();
    }

    @Override
    public void writeHeaders(HttpURLConnection urlConnection, HttpMethod httpMethod) {
        ParameterList params = httpMethod.getParams();
        if (params != null) {
            ArrayList<HeaderParameter> headList = params.getHeaderParams();
            for (HeaderParameter param : headList) {
                urlConnection.setRequestProperty(param.name, param.value);
            }
        }
    }

    @Override
    public void writeStream(HttpURLConnection urlConnection, HttpMethod httpMethod)
            throws IOException {
        // out.write(content);
        OutputStream ops = null;
        try {
            ops = openOutput(urlConnection);
            ParameterList params = httpMethod.getParams();
            if (params != null) {
                if (params.hasMultiPart()) {
                    DataOutputStream dos = new DataOutputStream(ops);
                    for (Parameter par : params) {
                        if (par instanceof StringParameter) {

                            StringParameter strPar = (StringParameter) par;
                            dos.writeBytes("--" + AbsHttpClient.BOUNDARY + AbsHttpClient.CRLF);
                            // 为了避免中文乱码，将string转为byte[]之后，再写入
                            dos.write(getPartHeader(strPar.name,null).getBytes("UTF-8"));
                            dos.writeBytes(AbsHttpClient.CRLF);
                            dos.write(strPar.value.getBytes("UTF-8"));
                            dos.writeBytes(AbsHttpClient.CRLF);
                        }

                        if (par instanceof FileParameter) {
                            FileParameter filePar = (FileParameter) par;

                            dos.writeBytes("--" + AbsHttpClient.BOUNDARY + AbsHttpClient.CRLF);
                            dos.write(getPartHeader(filePar.name,filePar.value.getName()).getBytes("UTF-8"));
                            dos.writeBytes(AbsHttpClient.CRLF);
                            InputStream ips = new FileInputStream(filePar.value);
                            try {
                                byte[] buffer = new byte[1024 * 8];
                                int read;
                                while ((read = ips.read(buffer)) != -1) {
                                    dos.write(buffer, 0, read);
                                }
                            } finally {
                                ips.close();
                            }
                            dos.writeBytes(AbsHttpClient.CRLF);
                        }

                        if (par instanceof InputStreamParameter) {
                            InputStreamParameter ipsPar = (InputStreamParameter)par;
                            dos.writeBytes("--" + AbsHttpClient.BOUNDARY + AbsHttpClient.CRLF);
                            dos.write(getPartHeader(ipsPar.name,ipsPar.fileName).getBytes("UTF-8"));
                            dos.writeBytes(AbsHttpClient.CRLF);
                            try {
                                byte[] buffer = new byte[1024 * 8];
                                int read;
                                while ((read = ipsPar.value.read(buffer)) != -1) {
                                    dos.write(buffer, 0, read);
                                }
                            } finally {
                                ipsPar.value.close();
                            }
                            dos.writeBytes(AbsHttpClient.CRLF);
                        }
                    }
                    dos.writeBytes("--" + AbsHttpClient.BOUNDARY + "--" + AbsHttpClient.CRLF);
                    ops.flush();
                } else if (httpMethod.getContent() != null) {
                    ops.write(httpMethod.getContent());
                }

            }
        } finally {
            if (ops != null) {
                try {
                    ops.close();
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public InputStream openInput(HttpURLConnection urlConnection) throws IOException {
        return urlConnection.getInputStream();
    }

    @Override
    public HttpResponse readInputStream(HttpURLConnection urlConnection) throws IOException {
        InputStream ips = openInput(urlConnection);
        byte[] result = null;
        if (ips != null) {
            try {
                byte[] buffer = new byte[1024 * 8];
                ByteArrayOutputStream bops = new ByteArrayOutputStream();
                int nRead;
                while ((nRead = ips.read(buffer)) != -1) {
                    bops.write(buffer, 0, nRead);
                }
                bops.flush();
                result = bops.toByteArray();
            } finally {
                ips.close();
            }
        }
        return new HttpResponse(urlConnection, result);
    }
    
    public String getPartHeader(String name,String fileName){
        StringBuilder sb = new StringBuilder();
        sb.append("Content-Disposition:form-data;name=\"");
        sb.append(name);
        if(fileName!=null){
            sb.append("\";filename=\"");
            sb.append(fileName);
        }
        sb.append("\"");
        sb.append(AbsHttpClient.CRLF);
        return sb.toString();
    }
    

}
