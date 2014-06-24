package edu.upc.eetac.dsa.dsaqp1314g5.gvent.android;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.android.api.Comment;
import edu.upc.eetac.dsa.dsaqp1314g5.gvent.android.api.Event;


public class CommentAdapter  extends BaseAdapter{
	private ArrayList<Comment> data;
	private LayoutInflater inflater;
	
	public CommentAdapter(Context context,ArrayList<Comment> data){
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
		return Long.parseLong(((Comment)getItem(position)).getId());
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {			
		convertView = inflater.inflate(R.layout.comment_detail_layout, null);
		viewHolder = new ViewHolder();
		viewHolder.tvuser = (TextView) convertView
				.findViewById(R.id.tvuser);
		viewHolder.tvcomment = (TextView) convertView
				.findViewById(R.id.tvcomment);

		convertView.setTag(viewHolder);
	} else {
		viewHolder = (ViewHolder) convertView.getTag();
	}
	String user = data.get(position).getUsername();
	String comment = data.get(position).getComment();
//	String date = SimpleDateFormat.getInstance().format(
//			data.get(position).getEventDate());
	viewHolder.tvuser.setText(user);
	viewHolder.tvcomment.setText(comment);
	return convertView;
}

private static class ViewHolder {
	TextView tvuser;
	TextView tvcomment;
	TextView tvDate;
}
}
