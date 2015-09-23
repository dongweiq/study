package com.example.customview01.view;

import com.example.customview01.R;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class DigitalView extends LinearLayout {
	private ImageView imageView1, imageView2, imageView3, imageView4,
			imageView5, imageView6, imageView7;
	private int[] images = { R.drawable.zero217x324, R.drawable.one217x324,
			R.drawable.two217x324, R.drawable.three217x324,
			R.drawable.four217x324, R.drawable.five217x324,
			R.drawable.six217x324, R.drawable.seven217x324,
			R.drawable.eight217x324, R.drawable.nine217x324,
			R.drawable.blank217x324, R.drawable.minus217x324 };
	private int six, five, four, three, two, one, numbers, textColor;
	private Drawable icon0, icon1, icon2, icon3, icon4, icon5, icon6, icon7,
			icon8, icon9, icon11;

	public DigitalView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DigitalView(Context context) {
		this(context, null);
	}

	public DigitalView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.digitalView, defStyleAttr, 0);
		int n = a.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.digitalView_textNumbers:
				numbers = a.getInt(attr, 1);
				break;
			case R.styleable.digitalView_textColor:
				textColor = a.getColor(attr, Color.BLACK);
			default:
				break;
			}
		}
		initView(context);
	}

	public void initView(Context context) {
		View view = View.inflate(getContext(), R.layout.digitalview, null);
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		addView(view);
		imageView1 = (ImageView) findViewById(R.id.imageView1);
		imageView2 = (ImageView) findViewById(R.id.imageView2);
		imageView3 = (ImageView) findViewById(R.id.imageView3);
		imageView4 = (ImageView) findViewById(R.id.imageView4);
		imageView5 = (ImageView) findViewById(R.id.imageView5);
		imageView6 = (ImageView) findViewById(R.id.imageView6);
		imageView7 = (ImageView) findViewById(R.id.imageView7);
		icon0 = context.getResources().getDrawable(images[0]);
		icon1 = context.getResources().getDrawable(images[1]);
		icon2 = context.getResources().getDrawable(images[2]);
		icon3 = context.getResources().getDrawable(images[3]);
		icon4 = context.getResources().getDrawable(images[4]);
		icon5 = context.getResources().getDrawable(images[5]);
		icon6 = context.getResources().getDrawable(images[6]);
		icon7 = context.getResources().getDrawable(images[7]);
		icon8 = context.getResources().getDrawable(images[8]);
		icon9 = context.getResources().getDrawable(images[9]);
		icon11 = context.getResources().getDrawable(images[11]);
		setNumbers(numbers);
		setColor(textColor);
	}

	/**
	 * <br/>
	 * 概述：着色 <br/>
	 * 
	 * @param color
	 */
	public void setColor(int color) {
		icon0 = tintDrawable(icon0, ColorStateList.valueOf(color));
		icon1 = tintDrawable(icon1, ColorStateList.valueOf(color));
		icon2 = tintDrawable(icon2, ColorStateList.valueOf(color));
		icon3 = tintDrawable(icon3, ColorStateList.valueOf(color));
		icon4 = tintDrawable(icon4, ColorStateList.valueOf(color));
		icon5 = tintDrawable(icon5, ColorStateList.valueOf(color));
		icon6 = tintDrawable(icon6, ColorStateList.valueOf(color));
		icon7 = tintDrawable(icon7, ColorStateList.valueOf(color));
		icon8 = tintDrawable(icon8, ColorStateList.valueOf(color));
		icon9 = tintDrawable(icon9, ColorStateList.valueOf(color));
		icon11 = tintDrawable(icon11, ColorStateList.valueOf(color));
	}

	/**
	 * <br/>
	 * 概述：设置位数 <br/>
	 */
	public void setNumbers(int numbers) {
		this.numbers = numbers;
		switch (numbers) {
		case 1:
			imageView1.setVisibility(View.VISIBLE);
			imageView2.setVisibility(View.GONE);
			imageView3.setVisibility(View.GONE);
			imageView4.setVisibility(View.GONE);
			imageView5.setVisibility(View.GONE);
			imageView6.setVisibility(View.GONE);
			break;
		case 2:
			imageView1.setVisibility(View.VISIBLE);
			imageView2.setVisibility(View.VISIBLE);
			imageView3.setVisibility(View.GONE);
			imageView4.setVisibility(View.GONE);
			imageView5.setVisibility(View.GONE);
			imageView6.setVisibility(View.GONE);
			break;
		case 3:
			imageView1.setVisibility(View.VISIBLE);
			imageView2.setVisibility(View.VISIBLE);
			imageView3.setVisibility(View.VISIBLE);
			imageView4.setVisibility(View.GONE);
			imageView5.setVisibility(View.GONE);
			imageView6.setVisibility(View.GONE);
			break;
		case 4:
			imageView1.setVisibility(View.VISIBLE);
			imageView2.setVisibility(View.VISIBLE);
			imageView3.setVisibility(View.VISIBLE);
			imageView4.setVisibility(View.VISIBLE);
			imageView5.setVisibility(View.GONE);
			imageView6.setVisibility(View.GONE);
			break;
		case 5:
			imageView1.setVisibility(View.VISIBLE);
			imageView2.setVisibility(View.VISIBLE);
			imageView3.setVisibility(View.VISIBLE);
			imageView4.setVisibility(View.VISIBLE);
			imageView5.setVisibility(View.VISIBLE);
			imageView6.setVisibility(View.GONE);
			break;
		case 6:
			imageView1.setVisibility(View.VISIBLE);
			imageView2.setVisibility(View.VISIBLE);
			imageView3.setVisibility(View.VISIBLE);
			imageView4.setVisibility(View.VISIBLE);
			imageView5.setVisibility(View.VISIBLE);
			imageView6.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
	}

	/**
	 * <br/>
	 * 概述：设置数值 <br/>
	 */
	public void setValue(int value) {
		if (value < 0) {
			imageView7.setVisibility(View.VISIBLE);
			value = -value;
		} else {
			imageView7.setVisibility(View.GONE);
		}
		six = value % 1000000 / 100000;
		five = value % 100000 / 10000;
		four = value % 10000 / 1000;
		three = value % 1000 / 100;
		two = value % 100 / 10;
		one = value % 10;
		imageView1.setImageResource(images[one]);
		imageView2.setImageResource(images[two]);
		imageView3.setImageResource(images[three]);
		imageView4.setImageResource(images[four]);
		imageView5.setImageResource(images[five]);
		imageView6.setImageResource(images[six]);
		if (six == 0) {
			imageView6.setImageResource(images[10]);
			if (five == 0) {
				imageView5.setImageResource(images[10]);
				if (four == 0) {
					imageView4.setImageResource(images[10]);
					if (three == 0) {
						imageView3.setImageResource(images[10]);
						if (two == 0) {
							imageView2.setImageResource(images[10]);
						}
					}
				}
			}
		}
		imageView1.invalidate();
		imageView2.invalidate();
		imageView3.invalidate();
		imageView4.invalidate();
		imageView5.invalidate();
		imageView6.invalidate();
		imageView7.invalidate();
	}

	/**
	 * <br/>
	 * 概述：给drawable着色 <br/>
	 * 
	 * @param drawable
	 * @param colors
	 * @return
	 */
	public static Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
		final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
		DrawableCompat.setTintList(wrappedDrawable, colors);
		return wrappedDrawable;
	}
}
