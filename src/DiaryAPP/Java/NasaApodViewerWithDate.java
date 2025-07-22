package DiaryAPP.Java;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

public class NasaApodViewerWithDate {

    private static final String API_KEY = "8PhJaO4tMefOUYNRbpf0pKtIupXOwCbsSDpo4brA";
    private static final String BASE_URL = "https://api.nasa.gov/planetary/apod?api_key=";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("日付を入力してください（例: 2024-06-01）: ");
            String date = scanner.nextLine();
            showApod(date);
        }
    }

    public static void showApod(String date) {
        try {
            String requestUrl = BASE_URL + API_KEY + "&date=" + date;
            URL url = new URI(requestUrl).toURL();
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

            String responseBody = response.toString();

            String imageUrl = extractValue(responseBody, "url");
            String title = extractValue(responseBody, "title");
            String explanation = extractValue(responseBody, "explanation");

            System.out.println("\n--- NASA Astronomy Picture of the Day ---");
            System.out.println("日付: " + date);
            System.out.println("タイトル: " + title);
            System.out.println("画像URL: " + imageUrl);
            System.out.println("\n解説: " + WebApiFunctions.translateText(explanation, "JA"));

        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }

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