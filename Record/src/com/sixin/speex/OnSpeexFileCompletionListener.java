package com.sixin.speex;

/**
 * Speex音频播放完成监听
 * @author shidongxue
 *
 */
public interface OnSpeexFileCompletionListener {
    void onCompletion(SpeexFileDecoder speexdecoder);
    void onError(Exception ex);
}
