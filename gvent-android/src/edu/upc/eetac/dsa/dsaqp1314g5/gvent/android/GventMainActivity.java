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
import android.widget.ArrayAdapter;
import android.app.ListActivity;

public class GventMainActivity extends ListActivity {
	private final static String TAG = GventMainActivity.class.toString();
	private static final String[] items = { "Super Fiesta", "Copa del Mundo",
			"Fiesta fin de curso", "Cena de amigos", "Concerto megachupi",
			"Presentacion DSA" };
	private ArrayAdapter<String> adapter;
	
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
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, items);
		setListAdapter(adapter);
		(new FetchStingsTask()).execute();
	}
	
	//END Autenticacion//
	
	// Fetch//

	private class FetchStingsTask extends
			AsyncTask<Void, Void, EventCollection> {
		private ProgressDialog pd;

		@Override
		protected EventCollection doInBackground(Void... params) {
			EventCollection events = null;
			try {
				events = GventAPI.getInstance(GventMainActivity.this)
						.getEvents();
			} catch (GventAndroidException e) {
				e.printStackTrace();
			}
			return events;
		}

		@Override
		protected void onPostExecute(EventCollection result) {
			ArrayList<Event> events = new ArrayList<Event>(result.getEvents());
			for (Event s : events) {
				Log.d(TAG, s.getId() + "-" + s.getTitle());
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
