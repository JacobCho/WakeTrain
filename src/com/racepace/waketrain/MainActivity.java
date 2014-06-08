package com.racepace.waketrain;



import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		View vancouverButton = (View) findViewById(R.id.vancouverButton);
		View seattleButton = (View) findViewById(R.id.seattleButton);
		
		setupActionBar();
		
		vancouverButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent bclist = new Intent(MainActivity.this, BCListActivity.class);
				startActivity(bclist);
			}
		});  
		
		seattleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent washlist = new Intent(MainActivity.this, WashListActivity.class);
				startActivity(washlist);
			}
		}); 

	}
	
	@SuppressLint("NewApi")
	 private void setupActionBar() {
	        ActionBar actionBar = getActionBar();
	        actionBar.setIcon(R.drawable.ic_transparent);
	    }

}
