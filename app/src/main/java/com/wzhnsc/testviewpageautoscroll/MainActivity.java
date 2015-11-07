package com.wzhnsc.testviewpageautoscroll;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
	}

	public void showNAE(View v) {
		Intent intent = new Intent(this, NormalActivity.class);
		startActivity(intent);
	}

	public void showLVE(View v) {
		Intent intent = new Intent(this, ListViewActivity.class);
		startActivity(intent);
	}
}
