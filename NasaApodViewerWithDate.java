

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class NasaApodViewerWithDate {

    // NASA APIキー（自分のキーに置き換えてください）
    private static final String API_KEY = "8PhJaO4tMefOUYNRbpf0pKtIupXOwCbsSDpo4brA";
    private static final String BASE_URL = "https://api.nasa.gov/planetary/apod?api_key=";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("日付を入力してください（例: 2024-06-01）: ");
        String date = scanner.nextLine();
        scanner.close();

        try {
            // APIリクエスト用URLを組み立て
            String requestUrl = BASE_URL + API_KEY + "&date=" + date;

            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            BufferedReader reader;
            if (responseCode == 200) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                System.out.println("APIリクエスト失敗。HTTPコード: " + responseCode);
            }

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String responseBody = response.toString();

            // 必要な情報を抽出（簡易な方法）
            String imageUrl = extractValue(responseBody, "url");
            String title = extractValue(responseBody, "title");
            String explanation = extractValue(responseBody, "explanation");

            // 結果を表示
            System.out.println("\n--- NASA Astronomy Picture of the Day ---");
            System.out.println("日付: " + date);
            System.out.println("タイトル: " + title);
            System.out.println("画像URL: " + imageUrl);
            System.out.println("\n解説: " + WebApiFunctions.translateText(explanation, "JA"));

            // 画像をブラウザで開く（画像URLが取得できた場合のみ）
            if (!imageUrl.equals("見つかりません")) {
                java.awt.Desktop.getDesktop().browse(new URL(imageUrl).toURI());
            }

        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }

    // JSON風の文字列からキーの値を取り出す（非常に簡易）
    private static String extractValue(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start == -1)
            return "見つかりません";
        start += search.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end).replace("\\n", "\n");
    }
}