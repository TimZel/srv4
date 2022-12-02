import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Request {
    String method;
    String path;
    String http;
    String exe;
    Map<String, String> mapHeadersValues;
    List bodyList;

    public static final Pattern patternHeaderValue = Pattern.compile("(\\p{Lu}{1,2}.*(\\:))(\\s.*)");
    public static final Pattern patternExe = Pattern.compile("(\\.[^\s]*)");
    public Request(String method, String path, String http, String exe, Map<String, String> mapHeadersValues, List bodyList) {
        this.method = method;
        this.path = path;
        this.http = http;
        this.exe = exe;
        this.mapHeadersValues = mapHeadersValues;
        this.bodyList = bodyList;
    }

    public static Request parse(InputStream isr) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(isr));

        String[] methodPathHttp = reader.readLine().split(" ");
        String method = methodPathHttp[0];
        String path = methodPathHttp[1];
        String http = methodPathHttp[2];

        String exe;
        Matcher matcherExe = patternExe.matcher(path);
        if(matcherExe.find()) {
            exe = matcherExe.group();
        } else {
            exe = null;
        }

        Map<String, String> mapHeadersValuesParse = new HashMap<>();
        List bodyList = new ArrayList<>();
        int emptySpacesCounter = 0;
            while (reader.ready()) {
                String headersBody = reader.readLine();
                Matcher matcherHeaderValue = patternHeaderValue.matcher(headersBody);
                if (!(headersBody.length() == 0)) {
                    if(emptySpacesCounter > 0) {
                            bodyList.add(headersBody);
                    }
                    if (matcherHeaderValue.find()) {
                        mapHeadersValuesParse.put(matcherHeaderValue.group(1), matcherHeaderValue.group(3));
                    }
                } else if (emptySpacesCounter >= 0) {
                    emptySpacesCounter++;
                }
            }
        return new Request(method, path, http, exe, mapHeadersValuesParse, bodyList);
    }
}





