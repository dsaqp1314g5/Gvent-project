package edu.upc.eetac.dsa.dsaqp1314g5.gvent.android;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqp1314g5.gvent.android.api.Comment;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.android.api.CommentCollection;
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

public class CommentsActivity extends ListActivity{
	
	private final static String TAG = CommentsActivity.class.getName();
	private ArrayList<Comment> commentList = new ArrayList<Comment>();
	private CommentAdapter adapter;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.comment_actions, menu);
		return true;
	}
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_detail_layout);
	//	String urlCommentsEvent = (String) getIntent().getExtras().get("url");
		
		
		(new FetchStingsTask()).execute();
	}
	
	private void addComments(CommentCollection comments){
		Log.i("miquel", "addComments activity" + comments.getComments());
		commentList.addAll(comments.getComments());
		Log.i("miquel", "adespues ddComments activity"+commentList);
		adapter = new CommentAdapter(this, commentList);
		setListAdapter(adapter);
		adapter.notifyDataSetChanged();
		Log.i("miquel", "despuesadapterchange"+commentList);
	}
	
	private class FetchStingsTask extends
		AsyncTask<Void, Void, CommentCollection> {

		private ProgressDialog pd;
		@Override
		protected CommentCollection doInBackground(Void... params) {
			
			CommentCollection comments = null;
			
			try {
				
				String id = (String) getIntent().getExtras().get("id");
				Log.i("miquel", "doinbacktry"+ id);
				comments = GventAPI.getInstance(CommentsActivity.this).getComments(id);
				Log.i("miquel", "getcomments"+comments);
			} catch (GventAndroidException e) {
				e.printStackTrace();
			}

			
			return comments;
		}
	
		@Override
		protected void onPostExecute(CommentCollection result) {
			Log.i("miquel", "onPostExecute:"+result);
			if (result==null){
			}
			else{
			addComments(result);
			}
			if (pd != null) {
				pd.dismiss();
			}
		}
		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(CommentsActivity.this);
			pd.setTitle("Searching...");
			pd.setCancelable(false);
			pd.setIndeterminate(true);
			pd.show();
		}
}
		
}
