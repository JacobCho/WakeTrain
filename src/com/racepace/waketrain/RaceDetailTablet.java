package com.racepace.waketrain;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RaceDetailTablet extends Fragment {
	private TextView description;
	private TextView location;
	private TextView startTime;
	private static TextView title;
	private ImageView webButton;
	private ImageView mapButton;
	private CalendarAdapter calAdapter;
	
	private String url;
	private String map;
	
	
	
	public RaceDetailTablet() {
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.racedetaildialog, container, false);	
		
		Bundle b = getArguments();
		
		title = (TextView) view.findViewById(R.id.dialogTitle);
		description = (TextView) view.findViewById(R.id.description);
		location = (TextView) view.findViewById(R.id.location);
		startTime = (TextView) view.findViewById(R.id.startTime);
		webButton = (ImageView) view.findViewById(R.id.webbutton);
		mapButton = (ImageView) view.findViewById(R.id.mapbutton);
		
		title.setText(b.getString("title"));
		description.setText(b.getString("content"));
		location.setText(b.getString("location"));
		startTime.setText(b.getString("time"));
		
		url = description.getText().toString();
		map = location.getText().toString();

		// If description starts with "http", show web image, and set click listener
		
		if (url.startsWith("http")) {
			webButton.setVisibility(View.VISIBLE);
			webButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
						Intent descripinternet = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
						startActivity(descripinternet);
				}
			});
		} else {
			webButton.setVisibility(View.INVISIBLE);
		}
		
		
		mapButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent where = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=" + map));
				startActivity(where);
			}
		});

		
		return view;
	
	}
	
	 public static RaceDetailTablet newInstance(String title, String content, String location, String time) {
		 RaceDetailTablet r = new RaceDetailTablet();

	        // Supply index input as an argument.
	        Bundle b = new Bundle();
	        b.putString("title", title);
			b.putString("content", content);
			b.putString("location", location);
			b.putString("time", time);

	        r.setArguments(b);

	        return r;
	    }
	
}
