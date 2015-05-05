package com.sixin.speex;


import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;


public class SpeexRecorder implements Runnable {

//	private Logger log = LoggerFactory.getLogger(SpeexRecorder.class);
	private volatile boolean isRecording;
	private final Object mutex = new Object();
	private static final int frequency = 8000;
	private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	public static int packagesize = 160;
	private String fileName = null;
//	private AudioRecord recordInstance = null;
	private double voice_value = 0;
	public SpeexRecorder(String fileName) {
		super();
		this.fileName = fileName;
	}
	
	public void setFileName(String fileName){
	    this.fileName = fileName;
	}

	public void run() {
	    
		// 启动编码线程
		SpeexEncoder encoder = new SpeexEncoder(this.fileName);
		Thread encodeThread = new Thread(encoder);
		encoder.setRecording(true);
		encodeThread.start();

		synchronized (mutex) {
			while (!this.isRecording) {
				try {
					mutex.wait();
				} catch (InterruptedException e) {
					throw new IllegalStateException("Wait() interrupted!", e);
				}
			}
		}
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		int bufferRead = 0;
		//缓冲大小
		int bufferSize = AudioRecord.getMinBufferSize(frequency, AudioFormat.CHANNEL_IN_MONO, audioEncoding);
		short[] tempBuffer = new short[bufferSize];
		AudioRecord recordInstance = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, AudioFormat.CHANNEL_IN_MONO, audioEncoding,bufferSize);
		recordInstance.startRecording();

		while (this.isRecording) {
//			log.debug("start to recording.........");
			bufferRead = recordInstance.read(tempBuffer, 0, packagesize);
			// bufferRead = recordInstance.read(tempBuffer, 0, 320);
			int v = 0;
            // 将 buffer 内容取出，进行平方和运算
            for (int i = 0; i < tempBuffer.length; i++) {
                // 这里没有做运算的优化，为了更加清晰的展示代码
                v += tempBuffer[i] * tempBuffer[i];
            }
            if(bufferRead!=0){
                voice_value = Math.log10(v/bufferRead);
                if(Double.isNaN(voice_value)){
                    voice_value = 10d;
                }
            }else{
                voice_value = 0;
            }
			if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
				throw new IllegalStateException("read() returned AudioRecord.ERROR_INVALID_OPERATION");
			} else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) {
				throw new IllegalStateException("read() returned AudioRecord.ERROR_BAD_VALUE");
			} else if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
				throw new IllegalStateException("read() returned AudioRecord.ERROR_INVALID_OPERATION");
			}
//			log.debug("put data into encoder collector....");
			encoder.putData(tempBuffer, bufferRead);

		}
		recordInstance.stop();
		recordInstance.release();
		//tell encoder to stop.
		encoder.setRecording(false);
	}

	public void setRecording(boolean isRecording) {
		synchronized (mutex) {
			this.isRecording = isRecording;
			if (this.isRecording) {
				mutex.notify();
			}
		}
	}

	public boolean isRecording() {
		synchronized (mutex) {
			return isRecording;
		}
	}
	
	public double getAmplitude() {
        return voice_value;
    }
}
