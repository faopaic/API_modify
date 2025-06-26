import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;
import org.json.JSONArray;

public class WebApiFunctions {
    public static String getCatFact() throws Exception {
        URL url = new URL("https://catfact.ninja/fact");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        Scanner sc = new Scanner(conn.getInputStream());
        StringBuilder response = new StringBuilder();
        while (sc.hasNext()) {
            response.append(sc.nextLine());
        }
        sc.close();

        JSONObject json = new JSONObject(response.toString());
        return json.getString("fact");
    }

    public static String translateText(String text, String targetLang) throws Exception {
        // DeepL Free API のURL
        String apiKey = "3b177dc2-f6b1-4808-8304-94786f62f920:fx";
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

    public static String getRandomWord() throws Exception {
        String apiUrl = "https://random-word-api.herokuapp.com/word?number=1";

        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        Scanner sc = new Scanner(conn.getInputStream());
        StringBuilder response = new StringBuilder();
        while (sc.hasNext()) {
            response.append(sc.nextLine());
        }
        sc.close();

        JSONArray jsonArray = new JSONArray(response.toString());
        return jsonArray.getString(0);
    }

    public static String getMeaning(String word) {
        String apiUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Scanner sc = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (sc.hasNext()) {
                response.append(sc.nextLine());
            }
            sc.close();

            JSONArray jsonArray = new JSONArray(response.toString());
            JSONObject firstEntry = jsonArray.getJSONObject(0);
            JSONArray meanings = firstEntry.getJSONArray("meanings");
            JSONObject firstMeaning = meanings.getJSONObject(0);
            JSONArray definitions = firstMeaning.getJSONArray("definitions");
            JSONObject firstDefinition = definitions.getJSONObject(0);

            return firstDefinition.getString("definition");
        } catch (java.io.FileNotFoundException e) {
            try {
                return WebApiFunctions.translateText(word, "JA") + "（翻訳結果）";
            } catch (Exception ex) {
                ex.printStackTrace();
                return "意味が見つからず、翻訳も失敗しました";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "エラーが発生しました";
        }
    }

}