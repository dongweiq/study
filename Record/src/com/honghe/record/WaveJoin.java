package com.honghe.record;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

public class WaveJoin {
	public final static String rawName = "RawAudio.raw";
	public final static String waveName = "FinalAudio.wav";
	public final static String rawName2 = "RawAudio2.raw";
	public final static String waveName2 = "FinalAudio2.wav";
	public final static String joinrawName = "JoinAudio.raw";
	public final static String joinWaveName = "JoinAudio.wav";

	// 获得缓冲区字节大小  
	public static int bufferSizeInBytes = AudioRecord.getMinBufferSize(AudioFileFunc.AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);

	public static void RemoveWaveHeader(String inFileName, String outFileName) {
		RandomAccessFile raf = null;
		FileOutputStream fileOutputStream = null;
		byte[] buf;
		try {
			raf = new RandomAccessFile(inFileName, "r");
			fileOutputStream = new FileOutputStream(outFileName);
			int length=100;
			String str = "";
//			raf.seek(44);
			for (int i = 0; i < length; i++) {
				str = str + (char)raf.readByte();
			}
			Log.e("test", "header:  "+str);
			raf.seek(0);
			raf.skipBytes(44);
			buf = new byte[(int) (raf.length() - 44)];
			raf.readFully(buf);
			fileOutputStream.write(buf);
			fileOutputStream.close();
			raf.close();
		} catch (Exception e) {
			Log.e("test", e.toString());
		}
	}

	public static void Join(String inFileName1, String inFileName2, String outFileName) {
		RandomAccessFile raf1 = null;
		RandomAccessFile raf2 = null;
		FileOutputStream fileOutputStream = null;
		byte[] buf1;
		byte[] buf2;
		try {
			raf1 = new RandomAccessFile(inFileName1, "r");
			raf2 = new RandomAccessFile(inFileName2, "r");
			fileOutputStream = new FileOutputStream(outFileName);
			raf1.seek(0);
			buf1 = new byte[(int) raf1.length()];
			buf2 = new byte[(int) raf2.length()];
			raf1.readFully(buf1);
			raf2.readFully(buf2);
			fileOutputStream.write(buf1);
			fileOutputStream.write(buf2);
			fileOutputStream.close();
			raf1.close();
			raf2.close();

		} catch (Exception e) {
			Log.e("test", e.toString());
		}

	}

	public static void Join() {
		RemoveWaveHeader(AudioFileFunc.getFilePathByName(waveName), AudioFileFunc.getFilePathByName(rawName));
		RemoveWaveHeader(AudioFileFunc.getFilePathByName(waveName2), AudioFileFunc.getFilePathByName(rawName2));
		Join(AudioFileFunc.getFilePathByName(rawName), AudioFileFunc.getFilePathByName(rawName2), AudioFileFunc.getFilePathByName(joinrawName));
		copyWaveFile(AudioFileFunc.getFilePathByName(joinrawName), AudioFileFunc.getFilePathByName(joinWaveName));
	}

	// 这里得到可播放的音频文件  
	public static void copyWaveFile(String inFilename, String outFilename) {
		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long longSampleRate = AudioFileFunc.AUDIO_SAMPLE_RATE;
		int channels = 1;
		long byteRate = 16 * AudioFileFunc.AUDIO_SAMPLE_RATE * channels / 8;
		byte[] data = new byte[bufferSizeInBytes];
		try {
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outFilename);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 36;
			WriteWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
			while (in.read(data) != -1) {
				out.write(data);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 
	 * 这里提供一个头信息。插入这些信息就可以得到可以播放的文件。 
	 * 为我为啥插入这44个字节，这个还真没深入研究，不过你随便打开一个wav 
	 * 音频的文件，可以发现前面的头文件可以说基本一样哦。每种格式的文件都有 
	 * 自己特有的头文件。 
	 */
	public static void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate, int channels, long byteRate) throws IOException {
		byte[] header = new byte[44];
		header[0] = 'R'; // RIFF/WAVE header  
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk  
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk  
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1  
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * 16 / 8); // block align  
		header[33] = 0;
		header[34] = 16; // bits per sample  
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
		out.write(header, 0, 44);
	}
}
