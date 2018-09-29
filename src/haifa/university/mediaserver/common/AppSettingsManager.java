package haifa.university.mediaserver.common;

/**
 * Created by yura on 21/10/2015.
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import com.google.gson.Gson;

import haifa.university.mysql.MySQLConnection;

public class AppSettingsManager {

    public static AppSettingsManager instance = null;
    private AppSettings settings;

    private String settingsFile = "settings.json";
    private Date appStarted;
    private Date webServerStarted;
    private String buildVersion = "2.3";

    public static synchronized AppSettingsManager getInstance() {
        if (instance == null) {
            instance = new AppSettingsManager();
            instance.appStarted = new Date();
        }
        return instance;
    }
    private AppSettingsManager() {
        super();
        load();
    }
    public void releaseInstance() {
        instance.clean();
        instance = null;
    }
    private void clean() {
    }
    public synchronized AppSettings getSettings() {
        if(settings==null){
        	load();
        	if(settings==null){
        		settings = new AppSettings();
        	}
        }
        return settings;
    }
    public Date getAppStarted(){
        return appStarted;
    }
    public Date getWebServerStarted(){
        return webServerStarted;
    }
    public void setWebServerStarted(Date start){
        webServerStarted = start;
    }
    public String getBuildVersion(){
        return buildVersion;
    }
    public void save() {
        File f = new File(settingsFile);

        if (!f.exists())
        {
            try
            {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        String jsonString = "";
        try {
            Gson gson = new Gson();
            jsonString = gson.toJson(getSettings());
        } catch (Throwable e) {
            e.printStackTrace();
        }

        BufferedWriter br = null;
        try {
            br = new BufferedWriter(new FileWriter(f,false));
            br.write(jsonString);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.flush();
                    br.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void load() {

        StringBuffer jsonStr = new StringBuffer();

        File f = new File(settingsFile);

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
            settings = new AppSettings();
            return;
        }

        // set parameters
        try {
            Gson gson = new Gson();
            settings = gson.fromJson(settingsStr, AppSettings.class);
            if(settings.web_server_port<=0){
            	settings.web_server_port = 80;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public class AppSettings {
        public String version;
        public int web_server_port;
        public MySQLConnection dmozConnection;
    }
}

