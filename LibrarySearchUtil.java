import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;

public class LibrarySearchUtil {
    private static final String API_BASE_URL = "https://api.calil.jp/library";
    private static final String APP_KEY = "3364770bfd19bc95456dc9d8afb68e3f"; // あなたのCalil APIキー

    public static void searchAndPrintLibraries(String selectedPrefecture) {
        try {
            String encodedPref = URLEncoder.encode(selectedPrefecture, StandardCharsets.UTF_8.toString());
            String apiUrl = String.format("%s?appkey=%s&format=json&pref=%s",
                    API_BASE_URL, APP_KEY, encodedPref);

            URL url = java.net.URI.create(apiUrl).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept-Charset", "UTF-8");

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                conn.disconnect();

                String rawJsonResponse = content.toString();

                String jsonToParse = rawJsonResponse.trim();
                if (jsonToParse.startsWith("callback(") && jsonToParse.endsWith(");")) {
                    jsonToParse = jsonToParse.substring("callback(".length(), jsonToParse.length() - 2);
                }

                if (jsonToParse.equals("[]")) {
                    System.out.println(selectedPrefecture + "の図書館情報は見つかりませんでした。");
                    System.out.println("※ Calil APIにその都道府県の図書館データがないか、APIキーが正しくない可能性があります。");
                } else {
                    try {
                        JSONArray jsonArray = new JSONArray(jsonToParse);
                        if (jsonArray.length() > 0) {
                            System.out.println("\n--- " + selectedPrefecture + "の図書館情報 ---");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject library = jsonArray.getJSONObject(i);
                                System.out.println("------------------------------------");
                                System.out.println("図書館名: " + library.optString("formal", "N/A"));
                                System.out.println("住所: " + library.optString("address", "N/A"));
                            }
                        } else {
                            System.out.println(selectedPrefecture + "の図書館情報は見つかりませんでした。");
                            System.out.println("※ Calil APIにその都道府県の図書館データがないか、APIキーが正しくない可能性があります。");
                        }
                    } catch (Exception e) {
                        System.out.println("JSONパースエラーが発生しました。受け取ったデータがJSON配列形式ではありません。");
                        System.out.println("エラーメッセージ: " + e.getMessage());
                        System.out.println("パースを試みた内容:\n" + jsonToParse);
                    }
                }

            } else {
                System.err.println("Calil APIからのデータ取得に失敗しました。レスポンスコード: " + responseCode);
                BufferedReader err = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                String errorLine;
                StringBuilder errorContent = new StringBuilder();
                while ((errorLine = err.readLine()) != null) {
                    errorContent.append(errorLine);
                }
                err.close();
                System.err.println("エラーメッセージ: " + errorContent.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}