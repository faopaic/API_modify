package iroiro.java;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

public class PoetryTranslate {

    public static void main(String[] args) {
        String author = "William Blake"; // 詩人名を指定
        getAndTranslatePoems(author);
    }

    public static void getAndTranslatePoems(String author) {
        try {
            // 空白を %20 に正しく変換
            String encodedAuthor = URLEncoder.encode(author, "UTF-8").replace("+", "%20");
            String apiUrl = "https://poetrydb.org/author/" + encodedAuthor;

            System.out.println("API URL: " + apiUrl);

            // HTTP リクエスト送信
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.out.println("⚠️ HTTPエラーコード: " + responseCode);
                return;
            }

            // レスポンス取得
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            reader.close();
            conn.disconnect();

            String jsonResponse = responseBuilder.toString().trim();

            // 詩データが配列で返された場合のみ処理
            if (jsonResponse.startsWith("[")) {
                JSONArray poems = new JSONArray(jsonResponse);

                if (poems.length() > 0) {
                    JSONObject poem = poems.getJSONObject(0); // 最初の1つだけ取得
                    String title = poem.getString("title");
                    JSONArray lines = poem.getJSONArray("lines");

                    // 最初の5行だけ表示（必要なら行数を変更可能）
                    int maxLinesToShow = Math.min(5, lines.length());

                    StringBuilder poemText = new StringBuilder();
                    for (int i = 0; i < maxLinesToShow; i++) {
                        poemText.append(lines.getString(i)).append("\n");
                    }

                    System.out.println("📜 タイトル: " + title);
                    System.out.println("原文（一部抜粋）:");
                    System.out.println(poemText.toString());
                    System.out.println("（※詩の全文は省略されています）");
                    System.out.println("-----------------------------\n");
                } else {
                    System.out.println("⚠️ 詩が見つかりませんでした。");
                }
            } else {
                JSONObject error = new JSONObject(jsonResponse);
                String reason = error.optString("reason", "不明なエラー");
                System.out.println("⚠️ APIエラー: " + reason);
            }

        } catch (Exception e) {
            System.out.println("❌ エラーが発生しました:");
            System.out.println(e.getMessage()); // ここで詳細な英語ログではなくメッセージ1行のみ表示
        }
    }
}
