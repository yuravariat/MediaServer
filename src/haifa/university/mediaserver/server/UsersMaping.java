package haifa.university.mediaserver.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class UsersMaping {
	private static final String mapingsFile = "users-maping.json";
	private static long mapingsFileLastModified = 0;
	private static HashMap<String,String> users;
	
	public static String getUserName(String deviceID){
		if(users==null || mapingsFileWasUpdated()){
			load();
			if(users==null){
				users = new HashMap<>();
			}
		}
		if(users.containsKey(deviceID)){
			return "user: " + users.get(deviceID);
		}
		return "user: unknown";
	}
	private static boolean mapingsFileWasUpdated(){
		File f = new File(mapingsFile);
		return mapingsFileLastModified != f.lastModified();
	}
	private static void load() {

        StringBuffer jsonStr = new StringBuffer();

        File f = new File(mapingsFile);

        BufferedReader br = null;

        if (!f.exists()) {
            return;
        }
        try {
            br = new BufferedReader(new FileReader(f));
            String line = null;
            while ((line = br.readLine()) != null) {
                jsonStr.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String settingsStr = jsonStr.toString();

        if (settingsStr == null || settingsStr.equals("")) {
            return;
        }

        // set parameters
        try {
        	mapingsFileLastModified = f.lastModified();
        	users = new HashMap<>();
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(settingsStr).getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();//will return members of your object
            for (Map.Entry<String, JsonElement> entry: entries) {
            	users.put(entry.getKey(),entry.getValue()!=null?entry.getValue().toString():"");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
