package iroiro.java;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

public class NewsDataApiExample {

    private static final String API_KEY = "pub_7d5740fbad644a58af2843c264e14cd0";
    private static final String BASE_URL = "https://newsdata.io/api/1/news";

    public static void main(String[] args) {
        try {
            String json = fetchNewsData("jp", "ja");
            displayNews(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ニュースデータをAPIから取得する
     */
    private static String fetchNewsData(String country, String language) throws Exception {
        String query = String.format("?apikey=%s&country=%s&language=%s", API_KEY, country, language);
        URI uri = new URI(BASE_URL + query);
        URL url = uri.toURL();

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder responseBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }

            return responseBuilder.toString();
        }
    }

    /**
     * 取得したJSON文字列を解析して記事を表示する
     */
    private static void displayNews(String json) {
        JSONObject jsonResponse = new JSONObject(json);
        JSONArray results = jsonResponse.getJSONArray("results");

        for (int i = 0; i < results.length(); i++) {
            JSONObject article = results.getJSONObject(i);
            String title = article.optString("title");
            String description = article.optString("description");
            String link = article.optString("link");

            System.out.println("■ 記事 " + (i + 1));
            System.out.println("タイトル: " + title);
            System.out.println("説明: " + description);
            System.out.println("URL: " + link);
            System.out.println();
        }
    }
}
