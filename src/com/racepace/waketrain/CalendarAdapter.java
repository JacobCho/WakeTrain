package com.racepace.waketrain;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.racepace.waketrain.R;

public class CalendarAdapter extends ArrayAdapter<EventsGetter> {
	public CalendarAdapter(Context context, ArrayList<EventsGetter> mEvents) {
		super(context, 0, mEvents);
	}
	
	// Translate a particular event given a position
	// into a relevant row within an adapterview
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		EventsGetter event = getItem(position);
		
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
				convertView = inflater.inflate(R.layout.cards_list_item, null);
		}
		
		TextView title = (TextView) convertView.findViewById(R.id.titleText);
		TextView month = (TextView) convertView.findViewById(R.id.monthText);
		TextView date = (TextView) convertView.findViewById(R.id.dateText);
		ImageView thumb = (ImageView) convertView.findViewById(R.id.thumb);
		
		title.setText(event.getTitle());
	    month.setText(event.getStartMonth());
	    date.setText(event.getStartDate());
	    
	    // Check if race is TNR or Big Chop, and load thumbnail
	    if ((event.getTitle()).substring(0,3).contains("TNR")){
	    	thumb.setImageResource(R.drawable.tnr);
	    } else if ((event.getTitle()).substring(0,3).contains("Big")) {
	    	thumb.setImageResource(R.drawable.bigchop);
	    } else if ((event.getTitle()).substring(0,8).contains("Canadian")){
	    	thumb.setImageResource(R.drawable.champs);
	    } else if ((event.getTitle()).substring(0,5).contains("Sound")) {
	    	thumb.setImageResource(R.drawable.soundrowers);
	    }
	    else {
	    	thumb.setImageResource(R.drawable.waketrain);
	    }
		
		return convertView;
	}
	
}
