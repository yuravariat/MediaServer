package haifa.university.mysql;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import haifa.university.info_beads.inputs.browsed_history.HistoryBookmark;
import haifa.university.mediaserver.common.AppLogger;

public class MySqlHelper {
	
	public static String escapeSQL(String x) {
		if(x==null){
			return "";
		}
        StringBuilder sBuilder = new StringBuilder(x.length() * 11/10);

        int stringLength = x.length();

        for (int i = 0; i < stringLength; ++i) {
            char c = x.charAt(i);

            switch (c) {
            case 0: /* Must be escaped for 'mysql' */
                sBuilder.append('\\');
                sBuilder.append('0');

                break;

            case '\n': /* Must be escaped for logs */
                sBuilder.append('\\');
                sBuilder.append('n');

                break;

            case '\r':
                sBuilder.append('\\');
                sBuilder.append('r');

                break;

            case '\\':
                sBuilder.append('\\');
                sBuilder.append('\\');

                break;

            case '\'':
                sBuilder.append('\\');
                sBuilder.append('\'');

                break;

            case '"': /* Better safe than sorry */
                /*if (escapeDoubleQuotes) {
                    sBuilder.append('\\');
                }*/

                sBuilder.append('"');

                break;

            case '\032': /* This gives problems on Win32 */
                sBuilder.append('\\');
                sBuilder.append('Z');

                break;

            case '\u00a5':
            case '\u20a9':
                // escape characters interpreted as backslash by mysql
                // fall through

            default:
                sBuilder.append(c);
            }
        }

        return sBuilder.toString();
    }
	public static final String LAST_3_BYTE_UTF_CHAR = "\uFFFF";
	public static final String REPLACEMENT_CHAR = "\uFFFD"; 

	public static String toValid3ByteUTF8String(String s)  {
	    final int length = s.length();
	    StringBuilder b = new StringBuilder(length);
	    for (int offset = 0; offset < length; ) {
	       final int codepoint = s.codePointAt(offset);

	       // do something with the codepoint
	       if (codepoint > LAST_3_BYTE_UTF_CHAR.codePointAt(0)) {
	           b.append(REPLACEMENT_CHAR);
	       } else {
	           if (Character.isValidCodePoint(codepoint)) {
	               b.appendCodePoint(codepoint);
	           } else {
	               b.append(REPLACEMENT_CHAR);
	           }
	       }
	       offset += Character.charCount(codepoint);
	    }
	    return b.toString();
	}
	public static int InsertBookmarksToDB(String deviceID, List<HistoryBookmark> marks) {
		
		try(MySQLConnector conn = new MySQLConnector()){
			int rows = 0;
			if(marks.size()<=1000){
				rows = conn.insert(createBookMarkInsertStatement(deviceID, marks));
			}
			else{
				List<HistoryBookmark> marks_buffer = new ArrayList<HistoryBookmark>();
				for (int i = 0; i < marks.size(); i++) {
					if(marks_buffer.size()>=1000){
						rows += conn.insert(createBookMarkInsertStatement(deviceID, marks_buffer));
						marks_buffer = new ArrayList<HistoryBookmark>();
					}
					else{
						marks_buffer.add(marks.get(i));
					}
				}
				if(marks_buffer.size()>=0){ // Send the rest
					rows += conn.insert(createBookMarkInsertStatement(deviceID, marks_buffer));
				}
			}
			return rows;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AppLogger.getInstance().writeLog("InsertBookmarksToDB error", " Exception="+e.getMessage() + 
					"stack trace = " + e.getStackTrace(),
                    AppLogger.LogLevel.ERROR);
			return 0;
		}
	}
	private static String createBookMarkInsertStatement(String deviceID, List<HistoryBookmark> marks){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuilder insertQuery = new StringBuilder(
				 "insert into users_data.browse_history (`device_id`,`url`,`visits`,`sourceApp`,`lastVisited`,`title`,`created`)\n values ");

		for (int i = 0; i < marks.size(); i++) {
			HistoryBookmark mark = marks.get(i);
			insertQuery.append(( i > 0 ? "\n," : "" )
					+ "('" + deviceID + "'"
					+ ",'" + MySqlHelper.escapeSQL(mark.url!=null && mark.url.length()>1000 ? mark.url.substring(0,1000):mark.url)+"'"
					+ "," + mark.visits +""
					+ ",'" + MySqlHelper.escapeSQL(mark.sourceApp)+"'"
					+ ",'" + formatter.format(mark.lastVisited)+"'"
					+ ",'" + MySqlHelper.toValid3ByteUTF8String(MySqlHelper.escapeSQL(mark.title!=null && mark.title.length()>250 ? mark.title.substring(0,250):mark.title))+"'"
					+ ",'" + formatter.format(mark.created)+"'"
					+ ")");
		}
		return insertQuery.toString();
	}
}
