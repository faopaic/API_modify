package DiaryAPP.Java;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class DateTimeApiUtil {
    public static DateTimeInfo getDateTime(String timezone) throws Exception {
        String urlStr = "http://worldtimeapi.org/api/timezone/" + timezone;
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        JSONObject json = new JSONObject(response.toString());
        // datetime フィールドは例: "2025-07-23T14:30:00.123456+09:00"
        String datetime = json.getString("datetime");

        return new DateTimeInfo(datetime);
    }
}

class DateTimeInfo {
    public final String datetime; // ISO8601形式の日時文字列

    public DateTimeInfo(String datetime) {
        this.datetime = datetime;
    }
}
