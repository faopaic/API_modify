import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

public class Translate {

    // DeepL API を使って翻訳する関数
    public static String translateText(String text, String targetLang, String apiKey) throws Exception {
        // DeepL Free API のURL
        URL url = new URL("https://api-free.deepl.com/v2/translate");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", "DeepL-Auth-Key " + apiKey);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // パラメータを作成
        String params = "text=" + text + "&target_lang=" + targetLang;

        try (OutputStream os = conn.getOutputStream()) {
            os.write(params.getBytes("UTF-8"));
        }

        // レスポンスを取得
        Scanner scanner = new Scanner(conn.getInputStream());
        StringBuilder response = new StringBuilder();
        while (scanner.hasNext()) {
            response.append(scanner.nextLine());
        }
        scanner.close();

        // JSONから翻訳文のみ取り出す
        JSONObject json = new JSONObject(response.toString());
        String translated = json.getJSONArray("translations")
                .getJSONObject(0)
                .getString("text");

        return translated;
    }
}

