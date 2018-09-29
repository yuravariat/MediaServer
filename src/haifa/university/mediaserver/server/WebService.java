package haifa.university.mediaserver.server;

import static spark.Spark.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import haifa.university.mediaserver.common.AppLogger;
import haifa.university.mediaserver.common.Helpers;
import haifa.university.mysql.MySqlHelper;
import haifa.university.info_beads.generic.Triplet;
import haifa.university.info_beads.inputs.browsed_history.*;
import spark.Request;

public class WebService {
	private static WebService instance;
	private WebService(){
		
	}
	public static WebService GetInstance(){
		if(instance==null){
			instance = new WebService();
		}
		return instance;
	}
	private String RequestToString(Request req){
		String str = req.requestMethod() + " " + req.scheme() + "://"
				+ req.host() + req.pathInfo() 
				+ (req.queryString()!=null && req.queryString().length() > 0 ? "?" + req.queryString():"");
		
		return str;
	}
	public boolean Start(){
		
		port(8080);
		
		get("/", (req, res) -> {
			System.out.println(RequestToString(req));
			return "It works";
		});
		post("/send-data", (req, res) -> {
			System.out.println(RequestToString(req));
			String reqBody = req.body();
			AppLogger.getInstance().writeLog("Request send-data", RequestToString(req) + "\nData="+reqBody,
                    AppLogger.LogLevel.TRACE);
			
			String output = "";
			
			try{
				GsonBuilder builder = new GsonBuilder(); 

				// Register an adapter to manage the date types as long values

				builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
					SimpleDateFormat formatter2 = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.ENGLISH);
					@Override
					public Date deserialize(JsonElement arg0, java.lang.reflect.Type arg1,
							JsonDeserializationContext arg2) throws JsonParseException {
						// TODO Auto-generated method stub
						String date = arg0.getAsString();
						formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
						formatter2.setTimeZone(TimeZone.getTimeZone("UTC"));

						Date dateObj = null;
						try {
							dateObj = formatter.parse(date);
						} catch (ParseException e) {
							try {
								dateObj = formatter2.parse(date);
							} catch (ParseException e2) {
								//System.err.println("Failed to parse Date due to:", e);
								return null;
							}
						}
						return dateObj;
					}
				});

				Gson gson = builder.create();
				
				java.lang.reflect.Type listType = new TypeToken<Triplet<ArrayList<Triplet>>>(){}.getType();
				//Triplet<ArrayList<Triplet>> triplet = new Triplet<ArrayList<Triplet>>("");
				
				Triplet<ArrayList<Triplet>> triplet = gson.fromJson(reqBody, listType);
				
				Helpers.WriteUserData(triplet.getInfoItem().getSupplementalData(), reqBody);
				
				output = "Triplet " + triplet.getId() + "\n##################################\n";
				
				for(int i = 0; i < triplet.getInfoItem().getInfoValue().size();i++){
					Triplet tr = triplet.getInfoItem().getInfoValue().get(i);
					output += "\nTriplet " + tr.getId();
					switch(tr.getId()){
						case "BrowseHistoryInfoBead":
							String marksStr = gson.toJson(tr.getInfoItem().getInfoValue());
							java.lang.reflect.Type marksTypes = new TypeToken<ArrayList<HistoryBookmark>>() {
							}.getType();
							List<HistoryBookmark> marks = gson.fromJson(marksStr, marksTypes);
							MySqlHelper.InsertBookmarksToDB(triplet.getInfoItem().getSupplementalData(), marks);
							output += "\nCasted O.K ";
						break;
					}
				}
			}
			catch(Exception e){
				AppLogger.getInstance().writeLog("Request send-data error", " Exception="+e.getMessage() + 
						"stack trace = " + e.getStackTrace(),
	                    AppLogger.LogLevel.ERROR);
			}
			return output;
		});
		post("/send-log", (req, res) -> {
			System.out.println(RequestToString(req));
			String reqBody = req.body();
			
			JsonParser jp = new JsonParser();
			JsonObject jsonObj = (JsonObject)jp.parse(reqBody);
			String key = jsonObj.get("id").getAsString();
			String level = jsonObj.get("level").getAsString();
			String log = jsonObj.get("log").getAsString();
			
			Helpers.WriteUserLog(key,level, log);
			
			return "O.K";
		});
		
		// examples
		//get("/hello", (req, res) -> {
		//	System.out.println(RequestToString(req));
		//	Set<String> headers = req.headers();
		//	Map<String,String> cookies = req.cookies();
		//	String body = req.body();
		//	return "Hello World";
		//});
		// matches "GET /hello/foo" and "GET /hello/bar"
		// request.params(":name") is 'foo' or 'bar'
		//get("/hello/:name", (req, res) -> {
		//	System.out.println(RequestToString(req));
		//    return "Hello: " + req.params(":name");
		//});
		
		return true;
	}
	
	public boolean Stop(){
		stop();
		return true;
	}
}
