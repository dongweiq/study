package com.honghe.record;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.sixin.speex.ShortAndByte;
import com.sixin.speex.Speex;

import android.util.Log;


public class WaveSpeex {
	private Speex speex = null;

	public static String encodedSpxName = "FinalAudio.spx";
	public static String decodedSpxName = "decodespx.raw";
	public static String decodedSpxNamewav = "decodespx.wav";

	public WaveSpeex() {
		speex = new Speex();
		speex.init();
	}

	public void raw2spx(String inFileName, String outFileName) {

		FileInputStream rawFileInputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			rawFileInputStream = new FileInputStream(inFileName);
			fileOutputStream = new FileOutputStream(outFileName);
			byte[] rawbyte = new byte[320];
			byte[] encoded = new byte[160];
			//将原数据转换成spx压缩的文件，speex只能编码160字节的数据，需要使用一个循环
			int readedtotal = 0;
			int size = 0;
			int encodedtotal = 0;
			while ((size = rawFileInputStream.read(rawbyte, 0, 320)) != -1) {
				readedtotal = readedtotal + size;
				short[] rawdata = ShortAndByte.byteArray2ShortArray(rawbyte);
				int encodesize = speex.encode(rawdata, 0, encoded, rawdata.length);
				fileOutputStream.write(encoded, 0, encodesize);
				encodedtotal = encodedtotal + encodesize;
				Log.e("test", "readedtotal " + readedtotal + "\n size" + size + "\n encodesize" + encodesize + "\n encodedtotal" + encodedtotal);
			}
			fileOutputStream.close();
			rawFileInputStream.close();
		} catch (Exception e) {
			Log.e("test", e.toString());
		}

	}

	public void wave2spx() {
		WaveJoin.RemoveWaveHeader(AudioFileFunc.getFilePathByName(WaveJoin.joinWaveName), AudioFileFunc.getFilePathByName(WaveJoin.joinrawName));
		raw2spx(AudioFileFunc.getFilePathByName(WaveJoin.joinrawName), AudioFileFunc.getFilePathByName(encodedSpxName));
	}

	public void spx2wav() {
		spx2raw(AudioFileFunc.getFilePathByName(encodedSpxName), AudioFileFunc.getFilePathByName(decodedSpxName));
		WaveJoin.copyWaveFile(AudioFileFunc.getFilePathByName(decodedSpxName), AudioFileFunc.getFilePathByName(decodedSpxNamewav));
	}

	public void spx2raw(String inFileName, String outFileName) {
		FileInputStream inAccessFile = null;
		FileOutputStream fileOutputStream = null;
		try {
			inAccessFile = new FileInputStream(inFileName);
			fileOutputStream = new FileOutputStream(outFileName);
			byte[] inbyte = new byte[20];
			short[] decoded = new short[160];
			int readsize = 0;
			int readedtotal = 0;
			int decsize = 0;
			int decodetotal = 0;
			while ((readsize = inAccessFile.read(inbyte, 0, 20)) != -1) {
				readedtotal = readedtotal + readsize;
				decsize = speex.decode(inbyte, decoded, readsize);
				fileOutputStream.write(ShortAndByte.shortArray2ByteArray(decoded), 0, decsize*2);
				decodetotal = decodetotal + decsize;
				Log.e("test", "readsize " + readsize + "\n readedtotal" + readedtotal + "\n decsize" + decsize + "\n decodetotal" + decodetotal);
			}
			fileOutputStream.close();
			inAccessFile.close();
		} catch (Exception e) {
			Log.e("test", e.toString());
		}
	}
}
