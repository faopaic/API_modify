import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.json.JSONObject;

public class WikiPersonSearch {

    public static void main(String[] args) throws Exception {
        // 出力をShift_JISに設定（文字化け対策）
        System.setOut(new java.io.PrintStream(System.out, true, "Shift_JIS"));
        Scanner scanner = new Scanner(System.in, "Shift_JIS");

        System.out.print("人物名を入力してください（例: 織田信長）: ");
        String person = scanner.nextLine();

        // Wikipedia API URL（概要だけ取得）
        String apiUrl = "https://ja.wikipedia.org/w/api.php?action=query&prop=extracts&exintro&explaintext&format=json&titles="
                + URLEncoder.encode(person, "Shift_JIS");

        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept-Charset", "Shift_JIS");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        JSONObject json = new JSONObject(sb.toString());
        JSONObject pages = json.getJSONObject("query").getJSONObject("pages");

        boolean found = false;

        for (String key : pages.keySet()) {
            JSONObject page = pages.getJSONObject(key);
            if (!key.equals("-1") && page.has("extract")) {
                String extract = page.getString("extract");
                System.out.println("\n--- 概要 ---\n" + extract);
                found = true;
            }
        }

        if (!found) {
            System.out.println(" Wikipediaに「" + person + "」のページが見つかりました。");
        }

        System.out.println("\n関連ページURL: https://ja.wikipedia.org/wiki/" + URLEncoder.encode(person, "UTF-8"));
    }
}
