package iroiro.java;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.json.JSONObject;

public class DateTimeApiUtil {
    public static DateTimeInfo getDateTime(String timezone) {
        try {
            String urlStr = "https://worldtimeapi.org/api/timezone/" + timezone;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000); // 5秒で接続失敗と判断
            conn.setReadTimeout(5000); // 応答が5秒以内にないと失敗
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HTTP error: " + responseCode);
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            JSONObject json = new JSONObject(response.toString());
            String datetime = json.getString("datetime");

            return new DateTimeInfo(datetime);

        } catch (Exception e) {
            // API取得に失敗した場合はローカルの時刻を取得して返す
            ZonedDateTime now = ZonedDateTime.now();
            // 例: "2025-07-24T10:00:00+09:00"
            String datetime = now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            return new DateTimeInfo(datetime);
        }
    }
}
