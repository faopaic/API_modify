package iroiro.java;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.json.JSONObject;

public class NasaApodViewerWithDate {

    private static final String API_KEY = "8PhJaO4tMefOUYNRbpf0pKtIupXOwCbsSDpo4brA";
    private static final String BASE_URL = "https://api.nasa.gov/planetary/apod?api_key=";

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in); // ← 変更せず維持
        System.out.print("日付を入力してください（例: 2024-06-01）: ");
        String date = scanner.nextLine();
        showApod(date);
    }

    public static void showApod(String date) throws Exception {
        String requestUrl = BASE_URL + API_KEY + "&date=" + date;
        URL url = new URI(requestUrl).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        JSONObject json = new JSONObject(response.toString());
        String title = json.optString("title", "タイトルなし");
        String imageUrl = json.optString("url", "URLなし");
        String explanation = json.optString("explanation", "説明なし");

        System.out.println("\n--- NASA Astronomy Picture of the Day ---");
        System.out.println("日付: " + date);
        System.out.println("タイトル: " + title);
        System.out.println("画像URL: " + imageUrl);
        System.out.println("\n解説: " + WebApiFunctions.translateText(explanation, "JA"));
    }
}
