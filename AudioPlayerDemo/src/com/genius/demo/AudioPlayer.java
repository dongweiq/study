package com.genius.demo;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.Contacts.Data;
import android.util.Log;

public class AudioPlayer implements IPlayComplete{

	private final static String TAG = "AudioPlayer";
	
	public final static int    STATE_MSG_ID = 0x0010;
	
	private Handler    mHandler;
	
	private AudioParam mAudioParam;							// 音频参数
	
	private byte[] 	   mData;								// 音频数据
	
	private AudioTrack mAudioTrack;							// AudioTrack对象
	
	private boolean    mBReady = false;						// 播放源是否就绪
	
	private PlayAudioThread mPlayAudioThread;				// 播放线程
	
	public AudioPlayer(Handler handler)
	{
		mHandler = handler;
	}
	
	public AudioPlayer(Handler handler,AudioParam audioParam)
	{
		mHandler = handler;
		setAudioParam(audioParam);	
	}
	
	/*
	 * 设置音频参数
	 */
	public void setAudioParam(AudioParam audioParam)
	{
		mAudioParam = audioParam;
	}
	
	/*
	 * 设置音频源
	 */
	public void setDataSource(byte[] data)
	{
		mData = data;		
	}
	
	
	/*
	 *  就绪播放源
	 */
	public boolean prepare()
	{
		if (mData == null || mAudioParam == null)
		{
			return false;
		}
		
		if (mBReady == true)
		{
			return true;
		}
		
		try {
			createAudioTrack();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
				
		
		mBReady = true;
		
		setPlayState(PlayState.MPS_PREPARE);
		
		return true;
	}
	
	/*
	 * 释放播放源
	 */
	public boolean release()
	{
		stop();

		releaseAudioTrack();
	
		mBReady = false;
		
		setPlayState(PlayState.MPS_UNINIT);
		
		return true;
	}
	
	/*
	 * 播放
	 */
	public boolean play()
	{
		if (mBReady == false)
		{
			return false;
		}
		
		
		switch(mPlayState)
		{
		case PlayState.MPS_PREPARE:
			mPlayOffset = 0;
			setPlayState(PlayState.MPS_PLAYING);
			startThread();	
			break;
		case PlayState.MPS_PAUSE:
			setPlayState(PlayState.MPS_PLAYING);
			startThread();	
			break;
		}
		
		
		return true;
	}
	
	/*
	 * 暂停
	 */
	public boolean pause()
	{

		if (mBReady == false)
		{
			return false;
		}
		
		if (mPlayState == PlayState.MPS_PLAYING)
		{
			setPlayState(PlayState.MPS_PAUSE);
			stopThread();
		}
		
		return true;
	}
	
	/*
	 * 停止
	 */
	public boolean stop()
	{

		if (mBReady == false)
		{
			return false;
		}
		
		setPlayState(PlayState.MPS_PREPARE);
		stopThread();
		
		return true;
		
		
		
	}
	
	
	private synchronized void setPlayState(int state)
	{
		mPlayState = state;
		
		if (mHandler != null)
		{
			Message msg = mHandler.obtainMessage(STATE_MSG_ID);
			msg.obj = mPlayState;
			msg.sendToTarget();
		}
	}
	

	private void createAudioTrack() throws Exception
	{
		
		// 获得构建对象的最小缓冲区大小
		int minBufSize = AudioTrack.getMinBufferSize(mAudioParam.mFrequency, 
													mAudioParam.mChannel,
													mAudioParam.mSampBit);
		
		
		mPrimePlaySize = minBufSize * 2;
		Log.d(TAG, "mPrimePlaySize = " + mPrimePlaySize);
		
//		         STREAM_ALARM：警告声
//		         STREAM_MUSCI：音乐声，例如music等
//		         STREAM_RING：铃声
//		         STREAM_SYSTEM：系统声音
//		         STREAM_VOCIE_CALL：电话声音
		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
									mAudioParam.mFrequency, 
									mAudioParam.mChannel, 
									mAudioParam.mSampBit, 
									minBufSize,
									AudioTrack.MODE_STREAM);
//				AudioTrack中有MODE_STATIC和MODE_STREAM两种分类。
//      		STREAM的意思是由用户在应用程序通过write方式把数据一次一次得写到audiotrack中。
//				这个和我们在socket中发送数据一样，应用层从某个地方获取数据，例如通过编解码得到PCM数据，然后write到audiotrack。
//				这种方式的坏处就是总是在JAVA层和Native层交互，效率损失较大。
//				而STATIC的意思是一开始创建的时候，就把音频数据放到一个固定的buffer，然后直接传给audiotrack，
//				后续就不用一次次得write了。AudioTrack会自己播放这个buffer中的数据。
//				这种方法对于铃声等内存占用较小，延时要求较高的声音来说很适用。
		
		

	}
	
	private void releaseAudioTrack(){
		if (mAudioTrack != null){
			mAudioTrack.stop();				       	
			mAudioTrack.release();
			mAudioTrack = null;
		}
		
	}
	
	
	private void startThread()
	{
		if (mPlayAudioThread == null)
		{
			mThreadExitFlag = false;
			mPlayAudioThread = new PlayAudioThread();
			mPlayAudioThread.start();
		}
	}
	
	private void stopThread()
	{
		if (mPlayAudioThread != null)
		{
			mThreadExitFlag = true;
			mPlayAudioThread = null;
		}
	}

	
	private boolean mThreadExitFlag = false;						// 线程退出标志
	
	private int     mPrimePlaySize = 0;								// 较优播放块大小
	
	private int 	mPlayOffset = 0;								// 当前播放位置
	
	private int     mPlayState = 0;									// 当前播放状态
	
	
	/*
	 *  播放音频的线程
	 */
	class PlayAudioThread extends Thread
	{

		
		@Override
		public void run() {
			// TODO Auto-generated method stub

			
			Log.d(TAG, "PlayAudioThread run mPlayOffset = " + mPlayOffset);
			
			mAudioTrack.play();	
			
				while(true)
				{											
					
					if (mThreadExitFlag == true)
					{
						break;
					}					
					
					try {
						
						int size = mAudioTrack.write(mData, mPlayOffset, mPrimePlaySize);
					
						mPlayOffset += mPrimePlaySize;
		
						
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						AudioPlayer.this.onPlayComplete();
						break;
					}
			
					if (mPlayOffset >= mData.length)
					{			
						AudioPlayer.this.onPlayComplete();
						break;
					}
					
					
				}
				
			mAudioTrack.stop();	
			
		
			Log.d(TAG, "PlayAudioThread complete...");				
			
		}
	
		
		
	}
	
	
	
	@Override
	public void onPlayComplete() {
		// TODO Auto-generated method stub		
		mPlayAudioThread = null;
		if (mPlayState != PlayState.MPS_PAUSE)
		{
			setPlayState(PlayState.MPS_PREPARE);
		}
		
	}	
	
	
}
