package haifa.university.mediaserver.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class Helpers {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH_mm");
	public static void WriteUserData(String key, String data){
		String logDirPath = Paths.get(".").toAbsolutePath().normalize().toString() + "/user_data/" + key + "/";
		String filePath = logDirPath +  "data-" + dateFormat.format(new Date()) + ".txt";
        File file = new File(filePath);
        if (!file.exists())
        {
            try
            {
            	file.getParentFile().mkdirs();
            	file.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
            buf.append(data);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	public static void WriteUserLog(String key,String level, String log){
		String logDirPath = Paths.get(".").toAbsolutePath().normalize().toString() + "/user_data/" + key + "/logs/";
		String filePath = logDirPath +  "/log-"+ level + "-" + dateFormat.format(new Date()) + ".txt";
        File file = new File(filePath);
        if (!file.exists())
        {
            try
            {
            	file.getParentFile().mkdirs();
            	file.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
        	BufferedWriter buf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
            buf.append(log);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	public static Gson CreateGsonSerializer(){
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

		return builder.create();
	}
}
