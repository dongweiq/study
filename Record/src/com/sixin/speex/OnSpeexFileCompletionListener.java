package com.sixin.speex;

/**
 * Speex音频解码完成监听
 * @author honghe
 *
 */
public interface OnSpeexFileCompletionListener {
    void onCompletion(SpeexFileDecoder speexdecoder);
    void onError(Exception ex);
}
