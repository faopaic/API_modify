package iroiro.java;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.json.JSONObject;

public class WikiPersonSearch {

    private static final String WIKI_API_BASE = "https://ja.wikipedia.org/w/api.php";
    private static final String CHARSET = "UTF-8";

    private static Scanner scanner; // 事前に宣言しておく

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, "Shift_JIS"));

        try {
            scanner = new Scanner(System.in, "Shift_JIS"); // 再代入形式に変更

            System.out.print("人物名を入力してください（例: 織田信長）: ");
            String person = scanner.nextLine();

            String apiUrl = WIKI_API_BASE
                    + "?action=query&prop=extracts&exintro&explaintext&format=json&titles="
                    + URLEncoder.encode(person, CHARSET);

            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept-Charset", CHARSET);

            try (InputStream is = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

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
                    if (page.has("extract") && !page.getString("extract").isBlank()) {
                        System.out.println("\n--- Wikipedia概要 ---");
                        System.out.println(page.getString("extract"));
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    System.out.println("Wikipediaに「" + person + "」のページが見つかりませんでした。");
                }

                System.out.println("\n関連ページURL: https://ja.wikipedia.org/wiki/" + URLEncoder.encode(person, CHARSET));
            }
        } finally {
            if (scanner != null) {
                //scanner.close();
            }
        }
    }
}
