import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import org.json.JSONObject;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;

public class OpenWeatherMapMain {
    public static void main(String[] args) {
        String city = "Osaka";
        try {
            OpenWeatherMapUtil.printWeather(city); // ← クラス名間違えないように
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }
}

class OpenWeatherMapUtil {
    // OpenWeatherMapのAPIキーを入力してください
    private static final String API_KEY = "59a6bab0b443967d7c07907a49dfd286";

    public static void printWeather(String city) throws Exception {
        String urlStr = "https://api.openweathermap.org/data/2.5/weather?q=" + city +
                "&appid=" + API_KEY + "&units=metric&lang=ja";
        URI uri = new URI(urlStr);
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "UTF-8"));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JSONObject json = new JSONObject(response.toString());
        String weather = json.getJSONArray("weather").getJSONObject(0).getString("description");
        double temp = json.getJSONObject("main").getDouble("temp");

        // 現在時刻（日本時間）を取得
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
        String date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        System.out.printf("日付: %s%n", date);
        System.out.printf("時刻: %s%n", time);

        System.out.printf("%sの天気: %s%n", city, weather);
        System.out.printf("気温: %.1f°C%n", temp);
    }
}