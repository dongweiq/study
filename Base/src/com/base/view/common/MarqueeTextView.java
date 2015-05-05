package com.base.view.common;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeTextView extends TextView {

	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setEllipsize(TruncateAt.MARQUEE);
		setSingleLine();
		setMarqueeRepeatLimit(-1);
	}

	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setEllipsize(TruncateAt.MARQUEE);
		setSingleLine();
		setMarqueeRepeatLimit(-1);
	}

	public MarqueeTextView(Context context) {
		super(context);
		setEllipsize(TruncateAt.MARQUEE);
		setSingleLine();
		setMarqueeRepeatLimit(-1);
	}

	@Override
	public boolean isFocused() {
		return true;
	}

}
