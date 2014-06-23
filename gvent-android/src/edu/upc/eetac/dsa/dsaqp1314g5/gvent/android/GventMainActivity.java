package edu.upc.eetac.dsa.dsaqp1314g5.gvent.android;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;

import org.apache.http.client.methods.HttpGet;

import edu.upc.eetac.dsa.dsaqp1314g5.gvent.android.api.Event;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.android.api.EventCollection;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.android.api.GventAPI;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.android.api.GventAndroidException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.app.ListActivity;
import android.content.Intent;

public class GventMainActivity extends ListActivity {
	private final static String TAG = GventMainActivity.class.toString();
	private ArrayList<Event> eventList;
	private EventAdapter adapter;
	
	//Autenticacion//
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gvent_layout);
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("Mikiodelg", "1234"
						.toCharArray());
			}
		});
	
		eventList = new ArrayList<Event>();
		adapter = new EventAdapter(this, eventList);
		setListAdapter(adapter);
		
		(new FetchStingsTask()).execute();
	}
	
	//END Autenticacion//
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.i("miquel","itemclick1");
		Event event = eventList.get(position);
		Log.i("miquel","itemclick2");
		Log.d(TAG, event.getLinks().get("self").getTarget());
		
		Intent intent = new Intent(this, EventDetailActivity.class);
		intent.putExtra("url", event.getLinks().get("self").getTarget());
		Log.i("miquel","itemclick3,pre intent");
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
			
			Log.i("miquel","main-2");
			EventCollection events = null;
			
			try {
				Log.i("miquel","main-1");
				events = GventAPI.getInstance(GventMainActivity.this).getEvents();
			} catch (GventAndroidException e) {
				e.printStackTrace();
			}
			Log.i("miquel","main0"+events);
			
			return events;
		}

		
		@Override
		protected void onPostExecute(EventCollection result) {
			Log.i("miquel","main1"+result);
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
