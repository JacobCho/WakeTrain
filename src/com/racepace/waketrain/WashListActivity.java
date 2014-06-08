package com.racepace.waketrain;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.racepace.waketrain.R;
import com.racepace.waketrain.cachemanager.CacheManager;
import com.racepace.waketrain.cachemanager.CacheTransactionException;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class WashListActivity extends Activity {
	private ListView listView;
	private CalendarAdapter calAdapter;
	private View mFakeHeader;
	private View mHeader;
	private KenBurnsView mHeaderPicture;
	private int mMinHeaderTranslation;
	private int mActionBarHeight;
    private int mHeaderHeight;
    private int mActionBarTitleColor;
    private AlphaForegroundColorSpan mAlphaForegroundColorSpan;
    private SpannableString mSpannableString;

    private TypedValue mTypedValue = new TypedValue();
	WashClient client;
	
	final String mFileName = "washlistcache.txt";
	CacheManager mCacheManager = new CacheManager();
	private Boolean SAVED;
	private Boolean TABLET = false;
	
	RaceDetailDialog dialog = new RaceDetailDialog();
	RaceDetailTablet tablet = new RaceDetailTablet();
	
	FrameLayout menuContainer;
	FrameLayout mainContentFrame;
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState != null) {}
		
		
		mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
		mMinHeaderTranslation = -mHeaderHeight + getActionBarHeight();
		
		setContentView(getLayoutResId());
		
		// Set up views if tablet view
		
		if(findViewById(R.id.tabletView) != null) {
			 menuContainer = (FrameLayout) findViewById(R.id.menuContainer);
			 menuContainer.addView(getLayoutInflater().inflate(R.layout.activity_listview, null));
			 
			 mainContentFrame = (FrameLayout) findViewById(R.id.mainContentFrame);
			 mainContentFrame.addView(getLayoutInflater().inflate(R.layout.empty, null));
			 TABLET = true;
		 } else {
			
		 }
		
		mCacheManager = CacheManager.getInstance(this);
		
		listView = (ListView) findViewById(R.id.listView1);
		ArrayList<EventsGetter> mEvents = new ArrayList<EventsGetter>();
		calAdapter = new CalendarAdapter(this, mEvents);	
		
		 /************* VARIABLES FOR NOTBORINGACTIONBAR **************/
		
		mHeader = findViewById(R.id.header);
		
		mHeaderPicture = (KenBurnsView) findViewById(R.id.header_picture);
	    mHeaderPicture.setResourceIds(R.drawable.samish, R.drawable.greg);
		mActionBarTitleColor = getResources().getColor(R.color.actionbar_title_color);
        mSpannableString = new SpannableString(getString(R.string.app_name));
        mAlphaForegroundColorSpan = new AlphaForegroundColorSpan(mActionBarTitleColor);
        
        //  Set a Click Listener for the Header Picture to prevent crashing				
        mHeaderPicture.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		
        	}
        });
		
        /************* END OF VARIABLES FOR NOTBORINGACTIONBAR **************/
		
        SAVED = mCacheManager.cacheCheck(mFileName);
		setUpListView();
		fetchEvents();
		setupActionBar();
	}
	
	private void setUpListView() {
		
		mFakeHeader = getLayoutInflater().inflate(R.layout.fake_header, listView, false);
		listView.addHeaderView(mFakeHeader);
		listView.setAdapter(calAdapter);
		
		listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int scrollY = getScrollY();
                //sticky actionbar
                mHeader.setTranslationY(Math.max(-scrollY, mMinHeaderTranslation));

                float ratio = clamp(mHeader.getTranslationY() / mMinHeaderTranslation, 0.0f, 1.0f);

                setTitleAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
            }
        });
		
	/************* Dialog Fragment Launcher on list click  **************/
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
				
				EventsGetter event = (EventsGetter) calAdapter.getItem(position - 1);
				String title = event.getTitle();
				String content = event.getContent();
				String location = event.getLocation();
				String time = event.getStartTime();
					
				
				if (TABLET) {
					
					RaceDetailTablet racetablet = (RaceDetailTablet)getFragmentManager()
							.findFragmentById(R.id.mainContentFrame);
					
					racetablet = RaceDetailTablet.newInstance(title, content, location, time);
					
					FragmentTransaction ft = getFragmentManager().beginTransaction();
					ft.remove(racetablet);
					ft.replace(R.id.mainContentFrame, racetablet);
					ft.commit(); 
				

				}
				else {
				
				Bundle b = new Bundle();
				b.putString("title", title);
				b.putString("content", content);
				b.putString("location", location);
				b.putString("time", time);
				
				dialog.setArguments(b);

				FragmentManager fm = getFragmentManager();
				dialog.show(fm, "Race Details");
				}
			}
		
		});
	}
	
	/************* FETCH JSON ITEMS FROM CALENDARCLIENT CLASS **************/
	
	private void fetchEvents() {
		client = new WashClient();
		
		// Check if device is connected to internet
		if (isNetworkOnline() && !SAVED){
		
		client.getCalendarData(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int code, JSONObject body) {
				JSONArray items = null;
				try {
					// write JSON results to cache file
					mCacheManager.write(body, mFileName);
					
					items = body.getJSONObject("feed").getJSONArray("entry");
					ArrayList<EventsGetter> events = EventsGetter.fromJson(items);
					
					for (EventsGetter event : events) {
						calAdapter.add(event);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (CacheTransactionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		} 
		
		else {
			
			/***** NO CONNECTION / READ CACHE FILE *****/
			JSONObject cacheread = null;
			JSONArray items = null;
			try {	
				cacheread = mCacheManager.readJSONObject(mFileName);
				items = cacheread.getJSONObject("feed").getJSONArray("entry");
			
				ArrayList<EventsGetter> events = EventsGetter.fromJson(items);
				for (EventsGetter event : events) {
					calAdapter.add(event);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (CacheTransactionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	
	// Network Connection Checker
	public boolean isNetworkOnline() {
	    boolean status=false;
	    try{
	        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo netInfo = cm.getNetworkInfo(0);
	        if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
	            status= true;
	        }else {
	            netInfo = cm.getNetworkInfo(1);
	            if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
	                status= true;
	        }
	    }catch(Exception e){
	        e.printStackTrace();  
	        return false;
	    }
	    return status;

	    }  
	
	
	/******************* ACTION BAR *********************/
	
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        getMenuInflater().inflate(R.menu.main, menu);
	        return true;
	    }
	 
	 @Override
	 	public boolean onOptionsItemSelected(MenuItem item) {
		 switch (item.getItemId()) {
		 case android.R.id.home:
			 finish();
			 return true;
		 
		 // On Refresh button click, clear adapter, re-fetch JSON data, re-set data to adapter
		 case R.id.refresh:
			 calAdapter.clear();
			 SAVED = false;
			 fetchEvents();
			 calAdapter.notifyDataSetChanged();
			 listView.setAdapter(calAdapter);
			 Toast.makeText(getApplicationContext(), "Refreshing..", Toast.LENGTH_SHORT).show();
			 return true;
			 
		 default:
			 return super.onOptionsItemSelected(item);
		 }
		 
	 }
	 

	 /************* METHODS FOR NOTBORINGACTIONBAR **************/
	 
	 // Get Scroll Position
	 public int getScrollY() {
		    View c = listView.getChildAt(0);
		    if (c == null) {
		        return 0;
		    }

		    int firstVisiblePosition = listView.getFirstVisiblePosition();
		    int top = c.getTop();

		    int headerHeight = 0;
		    if (firstVisiblePosition >= 1) {
		        headerHeight = mFakeHeader.getHeight();
		    }

		    return -top + firstVisiblePosition * c.getHeight() + headerHeight;
		}
	
	 
	 public int getActionBarHeight() {
	        if (mActionBarHeight != 0) {
	            return mActionBarHeight;
	        }
	        getTheme().resolveAttribute(android.R.attr.actionBarSize, mTypedValue, true);
	        mActionBarHeight = TypedValue.complexToDimensionPixelSize(mTypedValue.data, getResources().getDisplayMetrics());
	        return mActionBarHeight;
	    }
	 
	 public static float clamp(float value, float max, float min) {
		    return Math.max(Math.min(value, min), max);
		}
	 

	 private void setTitleAlpha(float alpha) {
	        mAlphaForegroundColorSpan.setAlpha(alpha);
	        mSpannableString.setSpan(mAlphaForegroundColorSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        getActionBar().setTitle(mSpannableString);
	    }
	 
	 
	 @SuppressLint("NewApi")
	 private void setupActionBar() {
	        ActionBar actionBar = getActionBar();
	        actionBar.setIcon(R.drawable.ic_transparent);
	        actionBar.setDisplayHomeAsUpEnabled(true);	
	    }
	 
	 /************* END OF METHODS FOR NOTBORINGACTIONBAR **************/
	 
	 protected int getLayoutResId() {
		    return R.layout.activity_masterdetail;
	 }
	 

	 @Override
	    public void onSaveInstanceState(Bundle outState) {
	       super.onSaveInstanceState(outState);
	    }	
}
