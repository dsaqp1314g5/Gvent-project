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
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.app.ListActivity;

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
			
//			HttpGet get  = new HttpGet("http://" + serverAddress + ":" + serverPort
//					+ "/gvent-api/events");
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
