package com.racepace.waketrain;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class WashClient {
	public String CALENDAR_URL =
	"https://www.google.com/calendar/feeds/grl4arse34q0qc8g910e7sdgh8%40group.calendar.google.com/public/full?alt=json";
	private static final String ORDER_BY = "&orderby=starttime";
	private static final String FUTURE_EVENTS = "&futureevents=true";
	private static final String SORT_ORDER = "&sortorder=ascending";
	private static final String MAX_RESULTS = "&max-results=";
	public String NUM_RESULTS = "50";
			
			
	private AsyncHttpClient client;
			
			
	public WashClient() {
		this.client = new AsyncHttpClient();
	}
			
	public void getCalendarData(JsonHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		client.get(CALENDAR_URL + ORDER_BY + FUTURE_EVENTS + SORT_ORDER + MAX_RESULTS + NUM_RESULTS, params, handler);
	}
}
