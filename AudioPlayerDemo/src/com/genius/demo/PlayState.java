package com.genius.demo;

public interface PlayState {
	
	public static final int MPS_UNINIT = 0;				// 未就绪
	
	public static final int MPS_PREPARE = 1;			// 准备就绪(停止)
	
	public static final int MPS_PLAYING = 2;			// 播放中
	
	public static final int MPS_PAUSE = 3;				// 暂停
}
