package com.sixin.speex;

/**
 * Speex“Ù∆µ≤•∑≈ÕÍ≥…º‡Ã˝
 * @author shidongxue
 *
 */
public interface OnSpeexCompletionListener {
    void onCompletion(SpeexDecoder speexdecoder);
    void onError(Exception ex);
}
