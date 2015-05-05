package com.yangyueyue.demo.luyin.tools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.util.Log;

/**
 * 
 * @author yang_yueyue
 * SD卡相常用关操作的工具类
 *
 */
public class SDcardTools {
	
	/****************************************************************
	 * 
	 * 判断SD卡是否存在
	 * @return
	 */
	public static boolean isHaveSDcard(){
		//判断SD卡是否存在 存在返回true 不存在返回false
		return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
	
	/*****************************************************************
	 *
	 * 把文件保存到SD卡中
	 * @param data
	 * @param pathName
	 * @param fileName
	 * @throws IOException 
	 */
	public static void saveFileToSDcard(byte[] data,String pathName,String fileName) throws IOException{
		//要保存的文件的路径
		String filePath = getSDPath()+"/"+pathName;
		Log.i("test", "SDcard路径是 =》"+filePath+fileName);
		//实例化文件夹
		File dir = new File(filePath);
		if(!dir.exists()){
			//如果文件夹不存在 则创建文件夹
			dir.mkdir();
		}
		Log.i("test", filePath);
		File file = new File(filePath+"/"+fileName);
		if(!file.exists()){
			//如果文件不存在 创建
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bos.write(data);
			bos.flush();
			bos.close();
			fos.close();
		}
	}
	
	/*****************************************************************
	 *  
	 * 获得SD卡的路径
	 */
	public static String getSDPath(){
		String sdDir=null;
		if(isHaveSDcard()){
			sdDir = Environment.getExternalStorageDirectory().toString();//获得根目录
		}
		return sdDir;
	}
	
	/** *************************************************************************
	 * 
	 * 从SD卡的dir目录下得到type类型的文件
	 * @param path
	 * @param type
	 * @return
	 */
	public static List<String> getFileFormSDcard(File dir,String type){
		List<String> listFilesName = new ArrayList<String>();
		if(isHaveSDcard()){
			File[] files = dir.listFiles();
			if(files !=null){
				for(int i=0; i<files.length; i++){
					if(files[i].getName().indexOf(".")>=0){
						// 只取Type类型的文件
						String filesResult = files[i].getName()
						.substring(files[i].getName().indexOf("."));
						if(filesResult.toLowerCase().equals(type.toLowerCase())){
							listFilesName.add(files[i].getName());
						}
						
					}
				}
			}
		}
		return listFilesName;
	}
	
}
