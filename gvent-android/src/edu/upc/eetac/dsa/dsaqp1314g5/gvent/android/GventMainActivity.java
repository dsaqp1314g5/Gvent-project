package edu.upc.eetac.dsa.dsaqp1314g5.gvent.android;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.android.api.Event;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.android.api.EventCollection;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.android.api.GventAPI;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.android.api.GventAndroidException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class GventMainActivity extends ListActivity {
	private final static String TAG = GventMainActivity.class.toString();
	private ArrayList<Event> eventList;
	private EventAdapter adapter;
	
	//Autenticacion//
	
	/** Called when the activity is first created. */
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.gvent_actions, menu);
		return true;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gvent_layout);
		SharedPreferences prefs = getSharedPreferences("gvent-profile",
				Context.MODE_PRIVATE);
		final String username = prefs.getString("username", null);
		final String password = prefs.getString("password", null);
	 
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password
						.toCharArray());
			}
		});
		Log.d(TAG, "authenticated with " + username + ":" + password);
	 
		eventList = new ArrayList<Event>();
		adapter = new EventAdapter(this, eventList);
		setListAdapter(adapter);
		(new FetchStingsTask()).execute();
	}
		
	//END Autenticacion//
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Event event = eventList.get(position);
		Log.d(TAG, event.getLinks().get("self").getTarget());
		
		Intent intent = new Intent(this, EventDetailActivity.class);
		intent.putExtra("url", event.getLinks().get("self").getTarget());
		startActivity(intent);
	}
	
	private void addEvents(EventCollection events){
		eventList.addAll(events.getEvents());
		adapter.notifyDataSetChanged();
	}
	
	// Fetch//

	private class FetchStingsTask extends
			AsyncTask<Void, Void, EventCollection> {
		
		private ProgressDialog pd;

		@Override
		protected EventCollection doInBackground(Void... params) {
			EventCollection events = null;
			try {
				events = GventAPI.getInstance(GventMainActivity.this).getEvents();
			} catch (GventAndroidException e) {
				e.printStackTrace();
			}		
			return events;
		}

		
		@Override
		protected void onPostExecute(EventCollection result) {
			if (result==null){
			}
			else{
			addEvents(result);
			}
			if (pd != null) {
				pd.dismiss();
			}
		}
		
		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(GventMainActivity.this);
			pd.setTitle("Searching...");
			pd.setCancelable(false);
			pd.setIndeterminate(true);
			pd.show();
		}

	}

	// End Fetch//

}
