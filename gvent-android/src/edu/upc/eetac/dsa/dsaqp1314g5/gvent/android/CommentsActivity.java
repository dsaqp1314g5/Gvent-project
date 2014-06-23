package edu.upc.eetac.dsa.dsaqp1314g5.gvent.android;

import android.app.Activity;
import android.view.Menu;

public class CommentsActivity extends Activity{
	private final static String TAG = CommentsActivity.class.getName();
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.comment_actions, menu);
		return true;
	}
}
