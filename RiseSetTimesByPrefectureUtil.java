import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class RiseSetTimesByPrefectureUtil {

    private static final String GSI_GEOCODING_API_URL = "https://msearch.gsi.go.jp/address-search/AddressSearch?q=";
    private static final String BITLABO_API_URL = "https://labs.bitmeister.jp/ohakon/json/?";

    // 住所から緯度経度を取得
    public static double[] getCoordinatesFromAddress(String address) throws IOException, JSONException {
        String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
        String urlString = GSI_GEOCODING_API_URL + encodedAddress;

        String responseBody = sendHttpRequest(urlString);
        if (responseBody == null || responseBody.isEmpty()) {
            return null;
        }

        JSONArray jsonArray = new JSONArray(responseBody);
        if (jsonArray.length() > 0) {
            JSONObject firstFeature = jsonArray.getJSONObject(0);
            JSONObject geometry = firstFeature.getJSONObject("geometry");
            JSONArray coordinates = geometry.getJSONArray("coordinates");
            if (coordinates.length() == 2) {
                return new double[]{coordinates.getDouble(0), coordinates.getDouble(1)};
            }
        }
        return null;
    }

    // 日の出・日の入り、月の出・月の入り時刻を取得
    public static void getRiseSetTimes(int year, int month, int day, double latitude, double longitude) throws IOException, JSONException {
        String mode = "sun_moon_rise_set";
        String urlString = String.format(
            BITLABO_API_URL + "mode=%s&year=%d&month=%d&day=%d&lat=%.4f&lng=%.4f",
            URLEncoder.encode(mode, StandardCharsets.UTF_8),
            year, month, day, latitude, longitude
        );

        String responseBody = sendHttpRequest(urlString);
        if (responseBody == null || responseBody.isEmpty()) {
            System.out.println("日の出・日の入り時刻のデータが見つかりませんでした。");
            return;
        }

        JSONObject jsonResponse = new JSONObject(responseBody);

        if (jsonResponse.has("error_message")) {
            JSONObject error = jsonResponse.getJSONObject("error_message");
            System.out.println("APIエラー (Code: " + error.getString("code") + "): " + error.getString("description"));
            return;
        }

        JSONObject riseAndSet = jsonResponse.getJSONObject("rise_and_set");

        String sunrise_hm = riseAndSet.optString("sunrise_hm", "--:--");
        String sunset_hm = riseAndSet.optString("sunset_hm", "--:--");
        String moonrise_hm = riseAndSet.optString("moonrise_hm", "--:--");
        String moonset_hm = riseAndSet.optString("moonset_hm", "--:--");

        System.out.println("日の出時刻: " + sunrise_hm);
        System.out.println("日の入り時刻: " + sunset_hm);
        System.out.println("月の出時刻: " + moonrise_hm);
        System.out.println("月の入り時刻: " + moonset_hm);
    }

    // HTTP GETリクエストを送信し、レスポンスボディを返す
    private static String sendHttpRequest(String urlString) throws IOException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = java.net.URI.create(urlString).toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder responseBody = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBody.append(line);
                }
                return responseBody.toString();
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
                StringBuilder errorBody = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    errorBody.append(line);
                }
                System.err.println("APIリクエストエラー (HTTP " + responseCode + "): " + errorBody.toString());
                return null;
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}