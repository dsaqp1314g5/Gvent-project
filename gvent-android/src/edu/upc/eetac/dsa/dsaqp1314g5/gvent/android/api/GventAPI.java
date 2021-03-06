package edu.upc.eetac.dsa.dsaqp1314g5.gvent.android.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.Map;
import java.util.Properties;
 



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 



import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class GventAPI {
	
	private final static String TAG = GventAPI.class.getName();
	private static GventAPI instance = null;
	private URL url;
 
	private GventRootAPI rootAPI = null;

	private GventAPI(Context context) throws IOException,
		GventAndroidException{
		super();
		AssetManager assetManager = context.getAssets();
		Properties config = new Properties();
		config.load(assetManager.open("config.properties"));
		String serverAddress = config.getProperty("server.address");
		String serverPort = config.getProperty("server.port");
		url = new URL("http://" + serverAddress + ":" + serverPort
				+ "/gvent-api");
 
		Log.d("LINKS", url.toString());
		getRootAPI();
	}
	public final static GventAPI getInstance(Context context)
			throws GventAndroidException {
		if (instance == null)
			try {
				instance = new GventAPI(context);
			} catch (IOException e) {
				throw new GventAndroidException(
						"Can't load configuration file");
			}
		return instance;
	}
	private void getRootAPI() throws GventAndroidException {
		Log.d(TAG, "getRootAPI()");
		rootAPI = new GventRootAPI();
		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);
			urlConnection.connect();
		} catch (IOException e) {
			throw new GventAndroidException(
					"Can't connect to Beeter API Web Service");
		}
 
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			 
			JSONObject jsonObject = new JSONObject(sb.toString());
			JSONArray jsonLinks = jsonObject.getJSONArray("links");
			parseLinks(jsonLinks, rootAPI.getLinks());
		} catch (IOException e) {
			throw new GventAndroidException(
					"Can't get response from Beeter API Web Service");
		} catch (JSONException e) {
			throw new GventAndroidException("Error parsing Beeter Root API");
		}
 
	}
	
	public EventCollection getEvents() throws GventAndroidException{
		Log.d(TAG, "getEvents()");
		EventCollection events = new EventCollection();
		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection) new URL(rootAPI.getLinks()
					.get("events").getTarget()).openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);
			urlConnection.connect();
		} catch (IOException e) {
			throw new GventAndroidException(
					"Can't connect to Beeter API Web Service");
		}
		
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			
			JSONObject jsonObject = new JSONObject(sb.toString());
			JSONArray jsonLinks = jsonObject.getJSONArray("links");
			parseLinks(jsonLinks, events.getLinks());
			
			JSONArray jsonEvents = jsonObject.getJSONArray("events");
			for (int i = 0; i < jsonEvents.length(); i++) {
				Event event = new Event();
				JSONObject jsonEvent = jsonEvents.getJSONObject(i);
				event.setTitle(jsonEvent.getString("title"));
				event.setCoordX(jsonEvent.getString("coordX"));
				event.setCoordY(jsonEvent.getString("coordY"));
				event.setId(jsonEvent.getString("id"));
				jsonLinks = jsonEvent.getJSONArray("links");
				parseLinks(jsonLinks, event.getLinks());
//				event.setEventDate(jsonEvent.getString("date"));
				parseLinks(jsonLinks, event.getLinks());
				events.getEvents().add(event);
			}
		} catch (IOException e) {
			throw new GventAndroidException(
					"Can't get response from Beeter API Web Service");
		} catch (JSONException e) {
			throw new GventAndroidException("Error parsing Beeter Root API");
		}
		Log.i("miquel","getfinal"+events);
		return events;
	}
	
	public CommentCollection getComments(String idevent) throws GventAndroidException{
		Log.i("miquel", "getcomments1");
		CommentCollection comments = new CommentCollection();
		HttpURLConnection urlConnection = null;
		try {
			Log.i("miquel", "getcommentstry");
			urlConnection = (HttpURLConnection) new URL("http://192.168.1.2:8080/gvent-api/events/"+idevent+"/comments").openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);
			urlConnection.connect();
			Log.i("miquel", "getcommentstryfinal");
		} catch (IOException e) {
			throw new GventAndroidException(
					"Can't connect to Beeter API Web Service");
		}
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			
			JSONObject jsonObject = new JSONObject(sb.toString());
			JSONArray jsonLinks = jsonObject.getJSONArray("links");
			parseLinks(jsonLinks, comments.getLinks());

			JSONArray jsonEvents = jsonObject.getJSONArray("comments");
			Log.i("miquel", "getcommentsfor");
			for (int i = 0; i < jsonEvents.length(); i++) {
				Comment comment = new Comment();
				JSONObject jsonEvent = jsonEvents.getJSONObject(i);
				comment.setUsername(jsonEvent.getString("username"));
				Log.i("miquel", "getcomments username:"+jsonEvent.getString("username"));
				comment.setComment(jsonEvent.getString("comment"));
				Log.i("miquel", "getcomments comment:"+jsonEvent.getString("comment"));
				jsonLinks = jsonEvent.getJSONArray("links");
				comment.setId(jsonEvent.getString("id"));
				parseLinks(jsonLinks, comment.getLinks());
//				event.setEventDate(jsonEvent.getString("date"));
				Log.i("miquel", "getcomments antes add for :"+comment);
				comments.getComments().add(comment);
				Log.i("miquel", "get comments fin for");
			}
		} catch (IOException e) {
			throw new GventAndroidException(
					"Can't get response from Beeter API Web Service");
		} catch (JSONException e) {
			throw new GventAndroidException("Error parsing Beeter Root API");
		}
		Log.i("miquel", "getcommentsresult"+comments);
		return comments;
	}
	
	public Event getEvent(String urlEvent) throws GventAndroidException {
		Event event = new Event();
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(urlEvent);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);
			urlConnection.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			JSONObject jsonSting = new JSONObject(sb.toString());
			event.setTitle(jsonSting.getString("title"));
			event.setDescription(jsonSting.getString("description"));
			event.setOwner(jsonSting.getString("owner"));
			event.setCategory(jsonSting.getString("category"));
			event.setState(jsonSting.getString("state"));
			event.setCoordX(jsonSting.getString("coordX"));
			event.setCoordY(jsonSting.getString("coordY"));
			event.setId(jsonSting.getString("id"));
			
			JSONArray jsonLinks = jsonSting.getJSONArray("links");
			parseLinks(jsonLinks, event.getLinks());
		} catch (MalformedURLException e) {
			Log.e(TAG, e.getMessage(), e);
			throw new GventAndroidException("Bad sting url");
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			throw new GventAndroidException("Exception when getting the sting");
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage(), e);
			throw new GventAndroidException("Exception parsing response");
		}
	 
		return event;
	}
	
	private void parseLinks(JSONArray jsonLinks, Map<String, Link> map)
			throws GventAndroidException, JSONException {
		for (int i = 0; i < jsonLinks.length(); i++) {
			Link link = SimpleLinkHeaderParser.parseLink(jsonLinks.getString(i));
			String rel = link.getParameters().get("rel");
			String rels[] = rel.split("\\s");
			for (String s : rels)
				map.put(s, link);
		}
	}
}