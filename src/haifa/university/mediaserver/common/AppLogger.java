package haifa.university.mediaserver.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yura on 06/03/2016.
 */
public class AppLogger {
    private static final SimpleDateFormat logDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
    private static String logDirPath;
    private static File logFileInfo;
    private static File logFileTrace;
    private static File logFileDebug;
    private static File logFileError;
    private static File logFileFatal;
    private static AppLogger ourInstance = new AppLogger();
    public static AppLogger getInstance() {
        return ourInstance;
    }
    private AppLogger() {
    	logDirPath = Paths.get(".").toAbsolutePath().normalize().toString() + "/logs/";
        logFileInfo = new File(logDirPath +  "/log-info.txt");
        logFileTrace = new File(logDirPath +  "/log-trace.txt");
        logFileDebug = new File(logDirPath +  "/log-debug.txt");
        logFileError = new File(logDirPath +  "/log-error.txt");
        logFileFatal = new File(logDirPath +  "/log-fatal.txt");
    }
    public void writeLog(String tag,String text,Throwable ex, LogLevel level)
    {
        File file;
        switch (level){
            case INFO:
                file = logFileInfo;
                break;
            case TRACE:
                file = logFileTrace;
                break;
            case DEBUG:
                file = logFileDebug;
                break;
            case ERROR:
                file = logFileError;
                break;
            case FATAL:
                file = logFileFatal;
                break;
            default:
                file = logFileInfo;
        }
        if (!file.exists())
        {
            try
            {
            	new File(logDirPath).mkdir();
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
            BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
            tag = tag.isEmpty()?"":tag+": ";
            String exStr = ex!=null ? " exception=" + ex.getMessage() + ", " + ex.getStackTrace() : "";
            buf.append("### " + logDateFormat.format(new Date()) + " => " + tag + text + exStr);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void writeLog(String text)
    {
        writeLog("",text,null,LogLevel.INFO);
    }
    public void writeLog(String tag,String text)
    {
        writeLog(tag,text,null,LogLevel.INFO);
    }
    public void writeLog(String text,LogLevel level)
    {
        writeLog("",text,null,level);
    }
    public void writeLog(String tag, String text,LogLevel level)
    {
        writeLog(tag,text,null,level);
    }
    public enum LogLevel{
        INFO,
        TRACE,
        DEBUG,
        ERROR,
        FATAL
    }
}
