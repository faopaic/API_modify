import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

public class AdviceApi {

    // DeepL APIキー（※セキュリティ上は環境変数での管理推奨）
    private static final String DEEPL_AUTH_KEY = "3b177dc2-f6b1-4808-8304-94786f62f920:fx";

    public static void main(String[] args) {
        try {
            String advice = fetchAdvice();
            System.out.println("Advice (EN): " + advice);

            String translated = translateToJapanese(advice);
            System.out.println("アドバイス (JA): " + translated);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * アドバイスAPI（https://api.adviceslip.com/advice）から
     * ランダムな英語のアドバイスを取得します。
     *
     * @return アドバイス文（英語）
     * @throws Exception 通信エラーなど
     */
    public static String fetchAdvice() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.adviceslip.com/advice"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject json = new JSONObject(response.body());
        return json.getJSONObject("slip").getString("advice");
    }

    /**
     * 指定された英語の文を DeepL API を使って日本語に翻訳します。
     *
     * @param text 翻訳対象の英語の文
     * @return 翻訳された日本語の文
     * @throws Exception 通信エラーなど
     */
    public static String translateToJapanese(String text) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String form = "auth_key=" + URLEncoder.encode(DEEPL_AUTH_KEY, StandardCharsets.UTF_8) +
                "&text=" + URLEncoder.encode(text, StandardCharsets.UTF_8) +
                "&target_lang=JA";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-free.deepl.com/v2/translate"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject json = new JSONObject(response.body());
        JSONArray translations = json.getJSONArray("translations");
        return translations.getJSONObject(0).getString("text");
    }
}
