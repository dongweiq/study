
package com.base.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class FileUtils {

    /**
     * 在SD卡上创建文件
     * 
     * @throws IOException
     */
    public File creatSDFile(String fileName) throws IOException {
        File file = new File(fileName);
        file.createNewFile();
        file.setReadable(false);
        return file;
    }

    /**
     * 在SD卡上创建目录
     * 
     * @param dirName
     */
    public File creatSDDir(String dirName) {
        File dir = new File(dirName);
        dir.mkdir();
        return dir;
    }

    /**
     * 判断SD卡上的文件夹是否存在
     */
    public boolean isFileExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     * @param path 文件保存路径
     * @param fileName 文件名称
     * @param input 流
     * @param filelenth 流长度
     * @param handler 
     * @return
     */
    public File write2SDFromInput(String path, String fileName, InputStream input,int filelenth, Handler handler) {
        File file = null;
        OutputStream output = null;
        try {
            creatSDDir(path);
            file = creatSDFile(path + "/"+fileName);
            output = new FileOutputStream(file);
            byte buffer[] = new byte[4*1024];
            int length;
            int prolen = 0;
            while((length=(input.read(buffer)))!=-1){  
                  output.write(buffer,0,length);
                  prolen += length;
                  if(null!=handler){
                      Message msg = handler.obtainMessage();
                      msg.what = ConsUtil.what_progress;
                      Bundle bundle = new Bundle();
                      bundle.putFloat("progress", (float)prolen/filelenth);
                      msg.setData(bundle);
                      handler.sendMessage(msg);
                  }
                  //RongXinLog.SystemOut("音频  读取进度==="+(float)prolen/filelenth+"   长度===="+prolen);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
            if(file!=null&&file.exists()){
                file.delete();
                file = null;
            }
        } finally {
            try {
            	if(null!=output){
            		output.close();
            	}
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(null!=file&&file.exists()){
            	file.setReadable(true);
            }
        }
        return file;
    }
    
    public static String getJsonDataFromAssets(String name,Context mContext) {
        AssetManager manager = mContext.getAssets();
        InputStream is = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            is = manager.open(name);
            br = new BufferedReader(new InputStreamReader(is));
            String temp = null;
            while (null != (temp = br.readLine())) {
                sb.append(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != br) {
                    br.close();
                }
                if (null != is)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
