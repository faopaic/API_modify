import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

// org.json ライブラリのインポート
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class RiseSetTimesByPrefecture {

    // 国土地理院 住所検索APIのURL
    private static final String GSI_GEOCODING_API_URL = "https://msearch.gsi.go.jp/address-search/AddressSearch?q=";
    // ビットラボ 日の出・日の入り時刻計算APIのURL (JSON形式)
    private static final String BITLABO_API_URL = "https://labs.bitmeister.jp/ohakon/json/?";

    public static void main(String[] args) {
        // 都道府県のマップを定義
        Map<Integer, String> prefectures = new LinkedHashMap<>();
        prefectures.put(1, "北海道");
        prefectures.put(2, "青森県");
        prefectures.put(3, "岩手県");
        prefectures.put(4, "宮城県");
        prefectures.put(5, "秋田県");
        prefectures.put(6, "山形県");
        prefectures.put(7, "福島県");
        prefectures.put(8, "茨城県");
        prefectures.put(9, "栃木県");
        prefectures.put(10, "群馬県");
        prefectures.put(11, "埼玉県");
        prefectures.put(12, "千葉県");
        prefectures.put(13, "東京都");
        prefectures.put(14, "神奈川県");
        prefectures.put(15, "新潟県");
        prefectures.put(16, "富山県");
        prefectures.put(17, "石川県");
        prefectures.put(18, "福井県");
        prefectures.put(19, "山梨県");
        prefectures.put(20, "長野県");
        prefectures.put(21, "岐阜県");
        prefectures.put(22, "静岡県");
        prefectures.put(23, "愛知県");
        prefectures.put(24, "三重県");
        prefectures.put(25, "滋賀県");
        prefectures.put(26, "京都府");
        prefectures.put(27, "大阪府");
        prefectures.put(28, "兵庫県");
        prefectures.put(29, "奈良県");
        prefectures.put(30, "和歌山県");
        prefectures.put(31, "鳥取県");
        prefectures.put(32, "島根県");
        prefectures.put(33, "岡山県");
        prefectures.put(34, "広島県");
        prefectures.put(35, "山口県");
        prefectures.put(36, "徳島県");
        prefectures.put(37, "香川県");
        prefectures.put(38, "愛媛県");
        prefectures.put(39, "高知県");
        prefectures.put(40, "福岡県");
        prefectures.put(41, "佐賀県");
        prefectures.put(42, "長崎県");
        prefectures.put(43, "熊本県");
        prefectures.put(44, "大分県");
        prefectures.put(45, "宮崎県");
        prefectures.put(46, "鹿児島県");
        prefectures.put(47, "沖縄県");

        String selectedPrefecture = "";
        // Scanner のエンコーディングを明示的に指定 (文字化け対策)
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name()); 

        try {
            System.out.println("--- 日の出・日の入り時刻や、月の出・月の入り時刻を出力したい都道府県を、以下のリストから番号で選択してください。---"); // メッセージ変更
            int count = 0;
            for (Map.Entry<Integer, String> entry : prefectures.entrySet()) {
                System.out.printf("%2d: %-7s", entry.getKey(), entry.getValue());
                count++;
                if (count % 5 == 0) {
                    System.out.println();
                } else {
                    System.out.print("   ");
                }
            }
            if (count % 5 != 0) {
                System.out.println();
            }

            System.out.print("\n番号を入力してください: ");

            int choice = -1;
            while (true) {
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    if (prefectures.containsKey(choice)) {
                        selectedPrefecture = prefectures.get(choice);
                        break;
                    } else {
                        System.out.print("無効な番号です。もう一度入力してください: ");
                    }
                } else {
                    System.out.print("数字を入力してください。もう一度入力してください: ");
                    scanner.next(); // 無効な入力を消費
                }
            }
            scanner.nextLine(); // 次の入力に備えて改行文字を消費

            // 1. 国土地理院APIで選択された都道府県の緯度・経度を取得
            double[] coordinates = getCoordinatesFromAddress(selectedPrefecture);

            if (coordinates == null) {
                System.out.println("選択された都道府県（" + selectedPrefecture + "）の緯度・経度を見つけることができませんでした。");
                return;
            }

            double longitude = coordinates[0];
            double latitude = coordinates[1];

            System.out.println("\n**選択された都道府県:** " + selectedPrefecture); // メッセージ変更
            System.out.println("緯度: " + latitude + ", 経度: " + longitude);

            // 2. ビットラボAPIで日の出・日の入り、月の出・月の入り時刻を取得
            LocalDate today = LocalDate.now(); // 今日の日付を取得
            int year = today.getYear();
            int month = today.getMonthValue();
            int day = today.getDayOfMonth();

            System.out.println("\n--- " + year + "年" + month + "月" + day + "日の情報 ---");
            getRiseSetTimes(year, month, day, latitude, longitude);

        } catch (IOException | JSONException e) {
            System.err.println("エラーが発生しました: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    /**
     * 国土地理院の住所検索APIを使用して、住所から緯度経度を取得します。
     * @param address 検索する住所文字列
     * @return 経度と緯度の配列 (longitude, latitude) または見つからない場合は null
     * @throws IOException HTTP通信エラーが発生した場合
     * @throws JSONException JSONパースエラーが発生した場合
     */
    private static double[] getCoordinatesFromAddress(String address) throws IOException, JSONException {
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

    /**
     * ビットラボAPIを使用して、指定された日付と緯度経度の日の出・日の入り、月の出・月の入り時刻を取得します。
     * @param year 年
     * @param month 月
     * @param day 日
     * @param latitude 緯度
     * @param longitude 経度
     * @throws IOException HTTP通信エラーが発生した場合
     * @throws JSONException JSONパースエラーが発生した場合
     */
    private static void getRiseSetTimes(int year, int month, int day, double latitude, double longitude) throws IOException, JSONException {
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

    /**
     * 指定されたURLにHTTP GETリクエストを送信し、レスポンスボディを文字列として返します。
     * @param urlString リクエストを送信するURL
     * @return レスポンスボディの文字列、またはエラーが発生した場合はnull
     * @throws IOException HTTP通信エラーが発生した場合
     */
    private static String sendHttpRequest(String urlString) throws IOException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
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