package edu.upc.eetac.dsa.dsaqp1314g5.gvent.android;

import edu.upc.eetac.dsa.dsaqp1314g5.gvent.android.api.Event;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.android.api.GventAPI;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.android.api.GventAndroidException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.CursorJoiner.Result;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class EventDetailActivity extends Activity{
	private final static String TAG = EventDetailActivity.class.getName();
	private Event event = new Event();
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.event_actions, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
        case R.id.MnuOpc1:
        	Intent intent = new Intent(this, CommentsActivity.class);
        	intent.putExtra("id",event.getId());
    		startActivity(intent);
    		return true;
     
    	default:
    		return super.onOptionsItemSelected(item);
    	}
     
    }
		@Override
		protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				setContentView(R.layout.event_detail_layout);
				String urlEvent = (String) getIntent().getExtras().get("url");
				(new FetchStingTask()).execute(urlEvent);
			}
		
		
		private void loadEvent(Event event) {
			TextView tvDetailtitle = (TextView) findViewById(R.id.tvDetailtitle);
			TextView tvDetailcategory = (TextView) findViewById(R.id.tvDetailcategory);
			TextView tvDetaildescription = (TextView) findViewById(R.id.tvDetaildescription);
			TextView tvDetailowner = (TextView) findViewById(R.id.tvDetailowner);
			TextView tvDetailstate = (TextView) findViewById(R.id.tvDetailstate);
			TextView tvDetailcoordx = (TextView) findViewById(R.id.tvDetailcoordx);
			TextView tvDetailcoordy = (TextView) findViewById(R.id.tvDetailcoordy);
			
			tvDetailtitle.setText(event.getTitle());
			tvDetailcategory.setText(event.getCategory());
			tvDetaildescription.setText(event.getDescription());
			tvDetailowner.setText(event.getOwner());
			tvDetailstate.setText(event.getState());
			tvDetailcoordx.setText(event.getCoordX());
			tvDetailcoordy.setText(event.getCoordY());
		}
		
		private class FetchStingTask extends AsyncTask<String, Void, Event> {
			private ProgressDialog pd;
		 
			@Override
			protected Event doInBackground(String... params) {
				Event event = null;
				try {
					event = GventAPI.getInstance(EventDetailActivity.this).getEvent(params[0]);
				} catch (GventAndroidException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return event;
			}
		 
			@Override
			protected void onPostExecute(Event result) {
				loadEvent(result);
				event = result;
				if (pd != null) {
					pd.dismiss();
				}
			}
		 
			@Override
			protected void onPreExecute() {
				pd = new ProgressDialog(EventDetailActivity.this);
				pd.setTitle("Loading...");
				pd.setCancelable(false);
				pd.setIndeterminate(true);
				pd.show();
			}
		 
		}
}
