package edu.upc.eetac.dsa.dsaqp1314g5.gvent.android;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqp1314g5.gvent.android.api.Event;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EventAdapter extends BaseAdapter {
	
	private ArrayList<Event> data;
	private LayoutInflater inflater;
	
	public EventAdapter(Context context, ArrayList<Event> data) {
		super();
		inflater = LayoutInflater.from(context);
		this.data = data;
	}
	
	@Override
	public int getCount() {
		return data.size();
	}
	 
	@Override
	public Object getItem(int position) {
		return data.get(position);
	}
	 
	@Override
	public long getItemId(int position) {
		return Long.parseLong(((Event)getItem(position)).getId());
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_row_event, null);
			viewHolder = new ViewHolder();
			viewHolder.tvSubject = (TextView) convertView
					.findViewById(R.id.tvSubject);
			viewHolder.tvUsername = (TextView) convertView
					.findViewById(R.id.tvUsername);
			viewHolder.tvDate = (TextView) convertView
					.findViewById(R.id.tvDate);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String title = data.get(position).getTitle();
		String category = data.get(position).getCategory();
		String date = SimpleDateFormat.getInstance().format(
				data.get(position).getEventDate());
		viewHolder.tvSubject.setText(title);
		viewHolder.tvUsername.setText(category);
		viewHolder.tvDate.setText(date);
		return convertView;
	}
	private static class ViewHolder {
		TextView tvSubject;
		TextView tvUsername;
		TextView tvDate;
	}
}
