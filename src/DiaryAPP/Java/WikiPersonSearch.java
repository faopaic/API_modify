package DiaryAPP.Java;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.json.JSONObject;

public class WikiPersonSearch {

    public static void main(String[] args) throws Exception {
        System.setOut(new java.io.PrintStream(System.out, true, "Shift_JIS"));

        try (Scanner scanner = new Scanner(System.in, "Shift_JIS")) {

            System.out.print("人物名を入力してください（例: 織田信長）: ");
            String person = scanner.nextLine();

            String apiUrl = "https://ja.wikipedia.org/w/api.php?action=query&prop=extracts"
                    + "&exintro&explaintext&format=json&titles=" + URLEncoder.encode(person, "UTF-8");

            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept-Charset", "UTF-8");

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
                    if (!key.equals("-1") && page.has("extract")) {
                        String extract = page.getString("extract");
                        System.out.println("\n--- Wikipedia概要 ---\n" + extract);
                        found = true;
                    }
                }

                if (!found) {
                    System.out.println(" Wikipediaに「" + person + "」のページが見つかりませんでした。");
                }

                System.out.println("\n関連ページURL: https://ja.wikipedia.org/wiki/" + URLEncoder.encode(person, "UTF-8"));
            }
        }
    }
}
