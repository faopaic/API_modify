package iroiro.java;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class LibrarySearchApp {

    private static final String API_BASE_URL = "https://api.calil.jp/library";
    private static final String APP_KEY = "3364770bfd19bc95456dc9d8afb68e3f";

    public static void main(String[] args) {
        Map<Integer, String> prefectures = createPrefectureMap();

        Scanner scanner = new Scanner(System.in);
        String selectedPrefecture = selectPrefecture(scanner, prefectures);

        try {
            searchAndDisplayLibraries(selectedPrefecture);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<Integer, String> createPrefectureMap() {
        Map<Integer, String> map = new LinkedHashMap<>();
        String[] names = {
                "北海道", "青森県", "岩手県", "宮城県", "秋田県", "山形県", "福島県",
                "茨城県", "栃木県", "群馬県", "埼玉県", "千葉県", "東京都", "神奈川県",
                "新潟県", "富山県", "石川県", "福井県", "山梨県", "長野県", "岐阜県",
                "静岡県", "愛知県", "三重県", "滋賀県", "京都府", "大阪府", "兵庫県",
                "奈良県", "和歌山県", "鳥取県", "島根県", "岡山県", "広島県", "山口県",
                "徳島県", "香川県", "愛媛県", "高知県", "福岡県", "佐賀県", "長崎県",
                "熊本県", "大分県", "宮崎県", "鹿児島県", "沖縄県"
        };
        for (int i = 0; i < names.length; i++) {
            map.put(i + 1, names[i]);
        }
        return map;
    }

    private static String selectPrefecture(Scanner scanner, Map<Integer, String> prefectures) {
        System.out.println("--- 都道府県を選択してください ---");
        int count = 0;
        for (Map.Entry<Integer, String> entry : prefectures.entrySet()) {
            System.out.printf("%2d: %-7s", entry.getKey(), entry.getValue());
            count++;
            if (count % 5 == 0)
                System.out.println();
            else
                System.out.print("  ");
        }
        if (count % 5 != 0)
            System.out.println();

        System.out.print("\n番号を入力してください: ");
        while (true) {
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                if (prefectures.containsKey(choice)) {
                    scanner.nextLine(); // 改行消費
                    return prefectures.get(choice);
                } else {
                    System.out.print("無効な番号です。もう一度入力してください: ");
                }
            } else {
                System.out.print("数字を入力してください。もう一度入力してください: ");
                scanner.next();
            }
        }
    }

    private static void searchAndDisplayLibraries(String prefecture) throws Exception {
        String encodedPref = URLEncoder.encode(prefecture, StandardCharsets.UTF_8.toString());
        String query = String.format("appkey=%s&format=json&pref=%s", APP_KEY, encodedPref);

        URI uri = new URI(API_BASE_URL + "?" + query);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept-Charset", "UTF-8")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() == 200) {
            parseAndDisplayResult(prefecture, response.body());
        } else {
            System.err.println("APIエラー [" + response.statusCode() + "]: " + response.body());
        }
    }

    private static void parseAndDisplayResult(String prefecture, String rawJson) {
        String jsonToParse = rawJson.trim();
        if (jsonToParse.startsWith("callback(") && jsonToParse.endsWith(");")) {
            jsonToParse = jsonToParse.substring("callback(".length(), jsonToParse.length() - 2);
        }

        if ("[]".equals(jsonToParse)) {
            System.out.println(prefecture + "の図書館情報は見つかりませんでした。");
        } else {
            try {
                JSONArray jsonArray = new JSONArray(jsonToParse);
                if (jsonArray.length() == 0) {
                    System.out.println(prefecture + "の図書館情報は見つかりませんでした。");
                    return;
                }

                System.out.println("\n--- " + prefecture + "の図書館情報 ---");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject library = jsonArray.getJSONObject(i);
                    System.out.println("------------------------------------");
                    System.out.println("図書館名: " + library.optString("formal", "N/A"));
                    System.out.println("住所: " + library.optString("address", "N/A"));
                    System.out.println("電話番号: " + library.optString("tel", "N/A"));
                    System.out.println("URL: " + library.optString("url_pc", "N/A"));
                }
            } catch (Exception e) {
                System.out.println("JSON解析エラー: " + e.getMessage());
                System.out.println("受信内容:\n" + jsonToParse);
            }
        }
    }
}