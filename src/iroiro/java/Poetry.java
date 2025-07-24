package iroiro.java;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

public class Poetry {

    public static void main(String[] args) {
        String author = "William Blake"; // 詩人名を指定（例: "Emily Dickinson", "Robert Frost" なども可）
        getPoems(author);
    }

    public static void getPoems(String author) {
        try {
            // 詩人名をURLエンコード（空白を %20 に）
            String encodedAuthor = URLEncoder.encode(author, "UTF-8").replace("+", "%20");
            String apiUrl = "https://poetrydb.org/author/" + encodedAuthor;

            System.out.println("API URL: " + apiUrl);

            // HTTP GETリクエスト
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.out.println("HTTPエラーコード: " + responseCode);
                return;
            }

            // レスポンス読み取り
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            reader.close();
            conn.disconnect();

            String jsonResponse = responseBuilder.toString().trim();

            // 詩データが配列として返された場合のみ処理
            if (jsonResponse.startsWith("[")) {
                JSONArray poems = new JSONArray(jsonResponse);

                if (poems.length() > 0) {
                    // ランダムに1つ詩を選ぶ
                    int randomIndex = (int) (Math.random() * poems.length());
                    JSONObject poem = poems.getJSONObject(randomIndex);
                    String title = poem.getString("title");
                    JSONArray lines = poem.getJSONArray("lines");

                    // 全文を連結
                    StringBuilder poemText = new StringBuilder();
                    for (int i = 0; i < lines.length(); i++) {
                        poemText.append(lines.getString(i)).append("\n");
                    }

                    // 日本語に翻訳（DeepL API）
                    String tspoem = WebApiFunctions.translateText(poemText.toString(), "JA");

                    // 表示
                    System.out.println("タイトル: " + title);
                    System.out.println("原文（全文）:\n" + poemText.toString());
                    System.out.println("和訳（全文）:\n" + tspoem);
                    System.out.println("-----------------------------\n");
                } else {
                    System.out.println("詩が見つかりませんでした。");
                }
            } else {
                JSONObject error = new JSONObject(jsonResponse);
                String reason = error.optString("reason", "不明なエラー");
                System.out.println("APIエラー: " + reason);
            }

        } catch (Exception e) {
            System.out.println("エラーが発生しました:");
            System.out.println(e.getMessage());
        }
    }
}
