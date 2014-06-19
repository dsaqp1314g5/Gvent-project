package edu.upc.eetac.dsa.dsaqp1314g5.gvent.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.app.ListActivity;

public class GventMainActivity extends ListActivity
{
	private final static String TAG = GventMainActivity.class.toString();
	private static final String[] items = { "Super Fiesta", "Copa del Mundo", "Fiesta fin de curso", "Cena de amigos",
			"Concerto megachupi", "Presentacion DSA" };
	private ArrayAdapter<String> adapter;
 
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gvent_layout);
        adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, items);
		setListAdapter(adapter);
	}
}
