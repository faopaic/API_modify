package iroiro.java;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

public class WebApiFunctions {

    public static String translateText(String text, String targetLang) throws Exception {
        // DeepL Free API のURLとキー
        String apiKey = "3b177dc2-f6b1-4808-8304-94786f62f920:fx";
        String urlStr = "https://api-free.deepl.com/v2/translate";
        URI uri = new URI(urlStr);
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", "DeepL-Auth-Key " + apiKey);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // パラメータをURLエンコードして送信
        String params = "text=" + URLEncoder.encode(text, StandardCharsets.UTF_8)
                + "&target_lang=" + URLEncoder.encode(targetLang, StandardCharsets.UTF_8);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(params.getBytes(StandardCharsets.UTF_8));
        }

        // HTTPステータスチェック
        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("翻訳APIリクエストに失敗しました: HTTP " + responseCode);
        }

        // レスポンス取得
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        // JSONパース
        JSONObject json = new JSONObject(response.toString());
        return json.getJSONArray("translations")
                .getJSONObject(0)
                .getString("text");
    }
}
