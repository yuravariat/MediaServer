package haifa.university.dmoz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import javax.management.Query;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import haifa.university.info_beads.generic.Triplet;
import haifa.university.info_beads.inputs.browsed_history.HistoryBookmark;
import haifa.university.mediaserver.common.AppLogger;
import haifa.university.mediaserver.common.CSVUtils;
import haifa.university.mediaserver.common.GenericResponse;
import haifa.university.mediaserver.common.Helpers;
import haifa.university.mysql.MySQLConnector;
import haifa.university.mysql.MySqlHelper;

public class DmozExtractor {
	public static GenericResponse<DmozLink> FindDmozCat(String url) {
		GenericResponse<DmozLink> response = new GenericResponse<>();
		URL aURL;
		try {
			aURL = new URL(url);

			// find all resources starting with domain of this url
			// String domain = aURL.getProtocol() + "://" + aURL.getHost();
			String domain = aURL.getHost();
			try(MySQLConnector conn = new MySQLConnector()){
				
				ResultSet res = conn
						.query("select `catid`,`topic`,`type`,`resource`,`title` from content_links where resource like '"
								+ "http://" + domain + "%' OR resource like 'https://" + domain + "%';");
	
				List<DmozLink> resources = new ArrayList<>();
				if (res.next()) {
					DmozLink link = new DmozLink(res.getLong(1), res.getString(2), res.getString(3), res.getString(4),
							res.getString(5));
					link.resource = link.resource.replace("https:", "http:").toLowerCase();// normalize
					resources.add(link);
				}
				// find best match
				if (resources.size() > 0) {
					
					String holeUrl = aURL.toString().replace("https:", "http:").toLowerCase();// normalize
					List<Integer> indexes = new ArrayList<>();
					for (int i = 0; i < resources.size(); i++) {
						indexes.add(i);
					}
					for (int i = 0; i < holeUrl.length(); i++) {
						if (indexes.size() == 1) {
							break;
						}
						for (int j = 0; j < indexes.size(); j++) {
							if (resources.get(indexes.get(j)).resource.length() < i
									|| resources.get(indexes.get(j)).resource.charAt(i) != holeUrl.charAt(i)) {
								indexes.remove(j);
								j--;
							}
						}
					}
					System.out.println("url=[" + url + "] matched resource=[" + resources.get(indexes.get(0)).resource
							+ "] catid=[" + resources.get(indexes.get(0)).catid + "] topic=["
							+ resources.get(indexes.get(0)).topic + "]");
					response.data = resources.get(indexes.get(0));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response.addError(e.getMessage() + " " + e.getStackTrace());
				AppLogger.getInstance().writeLog("Find DMOZ Category error", " Exception="+e.getMessage() + 
						"stack trace = " + e.getStackTrace(),
	                    AppLogger.LogLevel.ERROR);
			}

			return response;

		} catch (MalformedURLException e) {
			e.printStackTrace();
			response.addError(e.getMessage() + " " + e.getStackTrace());
			return response;
		} 
	}
	public static void MySQLConnection_Test() {
		try {
			try(MySQLConnector conn = new MySQLConnector()){
				ResultSet rs = conn.query("select * from datatypes limit 2");
				while (rs.next()) {
					System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	public static void URL_Test() {
		URL aURL;
		try {
			aURL = new URL("http://example.com:80/docs/books/tutorial" + "/index.html?name=networking#DOWNLOADING");
			System.out.println("URL = " + aURL);
			System.out.println("protocol = " + aURL.getProtocol());
			System.out.println("authority = " + aURL.getAuthority());
			System.out.println("host = " + aURL.getHost());
			System.out.println("port = " + aURL.getPort());
			System.out.println("path = " + aURL.getPath());
			System.out.println("query = " + aURL.getQuery());
			System.out.println("filename = " + aURL.getFile());
			System.out.println("ref = " + aURL.getRef());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void urlsFromFilesIntoScv() {

		GsonBuilder builder = new GsonBuilder();
		// Register an adapter to manage the date types as long values
		builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			SimpleDateFormat formatter2 = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.ENGLISH);

			@Override
			public Date deserialize(JsonElement arg0, java.lang.reflect.Type arg1, JsonDeserializationContext arg2)
					throws JsonParseException {
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
						// System.err.println("Failed to parse Date due to:",
						// e);
						return null;
					}
				}
				return dateObj;
			}
		});

		Gson gson = builder.create();
		java.lang.reflect.Type listType = new TypeToken<Triplet<ArrayList<Triplet>>>() {
		}.getType();

		List<HistoryBookmark> user_marks = new ArrayList<HistoryBookmark>();
		
		final AtomicInteger count = new AtomicInteger();
		try (Stream<Path> paths = Files.walk(Paths.get("data/urls"))) {
			paths.forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					// read file into stream, try-with-resources
					StringBuilder str = new StringBuilder();
					try (Stream<String> stream = Files.lines(filePath)) {
						stream.forEach(line -> str.append(line));
					} catch (IOException e) {
						e.printStackTrace();
					}
					Triplet<ArrayList<Triplet>> triplet = gson.fromJson(str.toString(), listType);
					
					
					int rowsInserted = 0;
					int marksCount = 0;
					for (int i = 0; i < triplet.getInfoItem().getInfoValue().size(); i++) {
						Triplet tr = triplet.getInfoItem().getInfoValue().get(i);
						// System.out.println("\nTriplet " + tr.getId());
						switch (tr.getId()) {
						case "BrowseHistoryInfoBead":
							String marksStr = gson.toJson(tr.getInfoItem().getInfoValue());
							java.lang.reflect.Type marksTypes = new TypeToken<ArrayList<HistoryBookmark>>() {
							}.getType();
							List<HistoryBookmark> marks = gson.fromJson(marksStr, marksTypes);
							marksCount = marks.size();
							for (HistoryBookmark historyBookmark : marks) {
								user_marks.add(historyBookmark);
							}
							rowsInserted = MySqlHelper.InsertBookmarksToDB("ffffffff-f7de-b069-2262-06053c333701", marks);
							break;
						}
					}
					System.out.println(count.incrementAndGet() + " - " + filePath + ". " +
							    marksCount + " marks, " + 
								(rowsInserted > 0 ? rowsInserted + " were inserted to db." : ""));
					
					// System.out.println(filePath + " = > " + str.toString());
				}
			});

			System.out.println(user_marks.size() + " collected urls");

			String csvFile = Paths.get("data") + "/urls.csv";
			File file = new File(csvFile);
			FileWriter writer;
			if (file.exists()) {
				writer = new FileWriter(file, true);// if file exists append to
													// file. Works fine.
			} else {
				file.createNewFile();
				writer = new FileWriter(file);
			}
			CSVUtils.writeLine(writer, Arrays.asList("url", "visits", "sourceApp", "lastVisited",
					"isBookmark" + "title", "created", "favicon", "thumbnail", "touchIcon", "userEntered"));
			for (HistoryBookmark mark : user_marks) {
				CSVUtils.writeLine(writer,
						Arrays.asList(mark.url, String.valueOf(mark.visits), mark.sourceApp,
								String.valueOf(mark.lastVisited), mark.title, String.valueOf(mark.created),
								mark.favicon, mark.thumbnail, mark.touchIcon, mark.userEntered));
			}
			writer.flush();
			writer.close();

			System.out.println("saved to csv!, " + csvFile);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static List<HistoryBookmark> loadMarksFromDB(String key, int limit){
		List<HistoryBookmark> user_marks = new ArrayList<HistoryBookmark>();
		try {
			try(MySQLConnector conn = new MySQLConnector()){
				ResultSet rs = conn.query("select id, device_id, url, visits, sourceApp, lastVisited, title, created" +
								" from users_data.browse_history" +
								" where device_id='" + key + "'" +
								" order by created desc" + (limit>0?" limit " + limit:""));
				while (rs.next()) {
					HistoryBookmark mark = new HistoryBookmark();
					mark.url = rs.getNString(3);
					mark.visits = rs.getInt(4);
					mark.sourceApp = rs.getString(5);
					mark.lastVisited = rs.getDate(6);
					mark.title = rs.getNString(7);
					mark.created = rs.getDate(8);
					user_marks.add(mark);
					System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				AppLogger.getInstance().writeLog("loadMarksFromDB error", " Exception="+e.getMessage() + 
						"stack trace = " + e.getStackTrace(),
	                    AppLogger.LogLevel.ERROR);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return user_marks;
	}
	public static void saveBookmarksToScv(List<HistoryBookmark> user_marks, String filename) {
		try{
			String csvFile = Paths.get("data") + "/" + filename + ".csv";
			File file = new File(csvFile);
			FileWriter writer;
			if (file.exists()) {
				writer = new FileWriter(file, true);// if file exists append to
													// file. Works fine.
			} else {
				file.createNewFile();
				writer = new FileWriter(file);
			}
			CSVUtils.writeLine(writer, Arrays.asList("url", "visits", "sourceApp", "lastVisited",
					"isBookmark" , "title", "created", "favicon", "thumbnail", "touchIcon", "userEntered"));
			for (HistoryBookmark mark : user_marks) {
				CSVUtils.writeLine(writer,
						Arrays.asList(mark.url, String.valueOf(mark.visits), mark.sourceApp,
								String.valueOf(mark.lastVisited), mark.title, String.valueOf(mark.created),
								mark.favicon, mark.thumbnail, mark.touchIcon, mark.userEntered));
			}
			writer.flush();
			writer.close();

			System.out.println("saved to csv!, " + csvFile);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static List<HistoryBookmark> loadBookmarksFromScv(String filename) {
		try{
			String csvFile = Paths.get("data") + "/" + filename + ".csv";
			File file = new File(csvFile);
			if (file.exists()) {
				List<HistoryBookmark> user_marks = new ArrayList<>();
				try(BufferedReader br = new BufferedReader(new FileReader(file))) {
					int count = 0;
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				    for(String line; (line = br.readLine()) != null; ) {
				    	if(count==0){
				    		// first line is headers.
				    		count++;
				    		continue;
				    	}
				        List<String> segments = CSVUtils.SplitCSV(line);
				        HistoryBookmark mark = new HistoryBookmark();
				        mark.url = segments.get(0);
				        mark.visits = Integer.parseInt(segments.get(1));
				        mark.sourceApp = segments.get(2);
				        mark.lastVisited = null; //segments.get(3);
				        mark.isBookmark = true;
				        mark.title = segments.get(4);
				        try {
							mark.created = formatter.parse(segments.get(5));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				        mark.favicon = segments.get(6);
				        mark.thumbnail = segments.get(7);
				        mark.touchIcon = segments.get(8);
				        mark.userEntered = segments.get(9);
				        count++;
				        user_marks.add(mark);
				    }
				}
				return user_marks;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
