package haifa.university.mediaserver.common;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class CSVUtils {

    private static final char DEFAULT_SEPARATOR = ',';
    private static final char QUOTE = '"';

    public static void writeLine(Writer w, List<String> values) throws IOException {
        writeLine(w, values, DEFAULT_SEPARATOR, QUOTE);
    }

    public static void writeLine(Writer w, List<String> values, char separators) throws IOException {
        writeLine(w, values, separators, QUOTE);
    }

    //https://tools.ietf.org/html/rfc4180
    private static String followCVSformat(String value) {

        String result = value==null?"":value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }

    public static void writeLine(Writer w, List<String> values, char separators, char customQuote) throws IOException {

        boolean first = true;

        //default customQuote is empty

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            }

            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());
    }
    public static List<String> SplitCSV(String line)
    {
    	List<String> segments = new ArrayList<String>();
    	StringBuilder s = new StringBuilder();
        boolean escaped = false, inQuotes = false;
        for(char c : line.toCharArray())
        {
            if (c == ',' && !inQuotes)
            {
            	segments.add(s.toString());
                s.setLength(0);;
            }
            else if (c == '\\' && !escaped)
            {
                escaped = true;
            }
            else if (c == '"' && !escaped)
            {
                inQuotes = !inQuotes;
            }
            else
            {
                escaped = false;
                s.append(c);
            }
        }
        segments.add(s.toString());
        return segments;
    }
}