import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class LibrarySearchApp {


    private static final String API_BASE_URL = "https://api.calil.jp/library";
    private static final String APP_KEY = "3364770bfd19bc95456dc9d8afb68e3f"; // あなたのCalil APIキー

    public static void main(String[] args) {
        Map<Integer, String> prefectures = new LinkedHashMap<>();
        prefectures.put(1, "北海道");
        prefectures.put(2, "青森県");
        prefectures.put(3, "岩手県");
        prefectures.put(4, "宮城県");
        prefectures.put(5, "秋田県");
        prefectures.put(6, "山形県");
        prefectures.put(7, "福島県");
        prefectures.put(8, "茨城県");
        prefectures.put(9, "栃木県");
        prefectures.put(10, "群馬県");
        prefectures.put(11, "埼玉県");
        prefectures.put(12, "千葉県");
        prefectures.put(13, "東京都");
        prefectures.put(14, "神奈川県");
        prefectures.put(15, "新潟県");
        prefectures.put(16, "富山県");
        prefectures.put(17, "石川県");
        prefectures.put(18, "福井県");
        prefectures.put(19, "山梨県");
        prefectures.put(20, "長野県");
        prefectures.put(21, "岐阜県");
        prefectures.put(22, "静岡県");
        prefectures.put(23, "愛知県");
        prefectures.put(24, "三重県");
        prefectures.put(25, "滋賀県");
        prefectures.put(26, "京都府");
        prefectures.put(27, "大阪府");
        prefectures.put(28, "兵庫県");
        prefectures.put(29, "奈良県");
        prefectures.put(30, "和歌山県");
        prefectures.put(31, "鳥取県");
        prefectures.put(32, "島根県");
        prefectures.put(33, "岡山県");
        prefectures.put(34, "広島県");
        prefectures.put(35, "山口県");
        prefectures.put(36, "徳島県");
        prefectures.put(37, "香川県");
        prefectures.put(38, "愛媛県");
        prefectures.put(39, "高知県");
        prefectures.put(40, "福岡県");
        prefectures.put(41, "佐賀県");
        prefectures.put(42, "長崎県");
        prefectures.put(43, "熊本県");
        prefectures.put(44, "大分県");
        prefectures.put(45, "宮崎県");
        prefectures.put(46, "鹿児島県");
        prefectures.put(47, "沖縄県");

        String selectedPrefecture = "";
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("--- 都道府県を選択してください ---");
            int count = 0;
            for (Map.Entry<Integer, String> entry : prefectures.entrySet()) {
                System.out.printf("%2d: %-7s", entry.getKey(), entry.getValue());
                count++;
                if (count % 5 == 0) {
                    System.out.println();
                } else {
                    System.out.print("  ");
                }
            }
            if (count % 5 != 0) {
                System.out.println();
            }

            System.out.print("\n番号を入力してください: ");

            int choice = -1;
            while (true) {
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    if (prefectures.containsKey(choice)) {
                        selectedPrefecture = prefectures.get(choice);
                        break;
                    } else {
                        System.out.print("無効な番号です。もう一度入力してください: ");
                    }
                } else {
                    System.out.print("数字を入力してください。もう一度入力してください: ");
                    scanner.next();
                }
            }
            scanner.nextLine();

            // int limit = 10; // ★この行は不要になるので削除またはコメントアウト★

            String encodedPref = URLEncoder.encode(selectedPrefecture, StandardCharsets.UTF_8.toString());
            // ★API URLからlimitパラメータを削除★
            String apiUrl = String.format("%s?appkey=%s&format=json&pref=%s",
                                        API_BASE_URL, APP_KEY, encodedPref);

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept-Charset", "UTF-8");

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                conn.disconnect();

                String rawJsonResponse = content.toString();

                String jsonToParse = rawJsonResponse.trim();
                if (jsonToParse.startsWith("callback(") && jsonToParse.endsWith(");")) {
                    jsonToParse = jsonToParse.substring("callback(".length(), jsonToParse.length() - 2);
                }

                if (jsonToParse.equals("[]")) {
                    System.out.println(selectedPrefecture + "の図書館情報は見つかりませんでした。");
                    System.out.println("※ Calil APIにその都道府県の図書館データがないか、APIキーが正しくない可能性があります。");
                } else {
                    try {
                        JSONArray jsonArray = new JSONArray(jsonToParse);
                        if (jsonArray.length() > 0) {
                            // 表示メッセージから「(最大XX件)」を削除
                            System.out.println("\n--- " + selectedPrefecture + "の図書館情報 ---");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject library = jsonArray.getJSONObject(i);
                                System.out.println("------------------------------------");
                                System.out.println("図書館名: " + library.optString("formal", "N/A"));
                                System.out.println("住所: " + library.optString("address", "N/A"));
                                System.out.println("電話番号: " + library.optString("tel", "N/A"));
                                System.out.println("URl: " + library.optString("url_pc", "N/A"));
                            }
                        } else {
                            System.out.println(selectedPrefecture + "の図書館情報は見つかりませんでした。");
                            System.out.println("※ Calil APIにその都道府県の図書館データがないか、APIキーが正しくない可能性があります。");
                        }
                    } catch (Exception e) {
                        System.out.println("JSONパースエラーが発生しました。受け取ったデータがJSON配列形式ではありません。");
                        System.out.println("エラーメッセージ: " + e.getMessage());
                        System.out.println("パースを試みた内容:\n" + jsonToParse);
                    }
                }

            } else {
                System.err.println("Calil APIからのデータ取得に失敗しました。レスポンスコード: " + responseCode);
                BufferedReader err = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                String errorLine;
                StringBuilder errorContent = new StringBuilder();
                while ((errorLine = err.readLine()) != null) {
                    errorContent.append(errorLine);
                }
                err.close();
                System.err.println("エラーメッセージ: " + errorContent.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //scanner.close();
        }
    }
}