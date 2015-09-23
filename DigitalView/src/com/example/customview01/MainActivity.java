package com.example.customview01;

import com.example.customview01.view.DigitalView;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {
	private DigitalView digitalView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		digitalView = (DigitalView) findViewById(R.id.digitalView);
		findViewById(R.id.button1).setOnClickListener(this);
		findViewById(R.id.button2).setOnClickListener(this);
		findViewById(R.id.button3).setOnClickListener(this);
		findViewById(R.id.button4).setOnClickListener(this);
		findViewById(R.id.button11).setOnClickListener(this);
		findViewById(R.id.button12).setOnClickListener(this);
		findViewById(R.id.button13).setOnClickListener(this);
		findViewById(R.id.button14).setOnClickListener(this);
		findViewById(R.id.button15).setOnClickListener(this);
		findViewById(R.id.button16).setOnClickListener(this);
		new Thread(new Runnable() {
			int value = 0;

			@Override
			public void run() {
				while (true) {
					value++;
					digitalView.post(new Runnable() {

						@Override
						public void run() {
							digitalView.setValue(-value);
						}
					});
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			digitalView.setColor(Color.BLUE);
			break;
		case R.id.button2:
			digitalView.setColor(Color.RED);
			break;
		case R.id.button3:
			digitalView.setColor(Color.WHITE);
			break;
		case R.id.button4:
			digitalView.setColor(Color.YELLOW);
			break;
		case R.id.button11:
			digitalView.setNumbers(1);
			break;
		case R.id.button12:
			digitalView.setNumbers(2);
			break;
		case R.id.button13:
			digitalView.setNumbers(3);
			break;
		case R.id.button14:
			digitalView.setNumbers(4);
			break;
		case R.id.button15:
			digitalView.setNumbers(5);
			break;
		case R.id.button16:
			digitalView.setNumbers(6);
			break;

		default:
			break;
		}
	}
}
