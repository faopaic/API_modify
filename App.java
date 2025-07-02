import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class App {

    // あなたのカーリル図書館APIキーを設定してください
    private static final String API_KEY = "3364770bfd19bc95456dc9d8afb68e3f";
    private static final String API_BASE_URL = "https://api.calil.jp/library";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("-------------------------------------");
        System.out.println("     カーリル図書館検索アプリ");
        System.out.println("-------------------------------------");

        // アプリケーションをループさせて、複数回検索できるようにする
        while (true) {
            System.out.println("\n検索したい都道府県名を入力してください (例: 埼玉県)。終了する場合は 'exit' と入力してください:");
            String prefecture = scanner.nextLine();

            if (prefecture.equalsIgnoreCase("exit")) {
                System.out.println("アプリケーションを終了します。");
                break; // ループを抜けて終了
            }

            if (prefecture.isEmpty()) {
                System.err.println("都道府県名が入力されていません。もう一度入力してください。");
                continue; // 再度入力を促す
            }

            try {
                // URLエンコード
                String encodedPrefecture = URLEncoder.encode(prefecture, StandardCharsets.UTF_8.toString());

                // APIリクエストURLを構築
                // format=json と callback= を明示的に指定し、JSON形式の応答を要求
                String apiUrl = String.format("%s?appkey=%s&pref=%s&format=json&callback=",
                                               API_BASE_URL, API_KEY, encodedPrefecture);

                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                // User-Agentを設定して、一般的なブラウザからのリクエストに見せかける
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
                connection.setConnectTimeout(5000); // 接続タイムアウトを5秒に設定 (ミリ秒)
                connection.setReadTimeout(5000);    // 読み込みタイムアウトを5秒に設定 (ミリ秒)

                int responseCode = connection.getResponseCode();
                System.out.println("HTTP Response Code: " + responseCode); // HTTPレスポンスコードを表示

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                    String inputLine;
                    StringBuilder content = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();
                    connection.disconnect();

                    String rawContent = content.toString();

                    // 受信した生の応答内容を表示 (デバッグ用)
                    System.out.println("\n--- RAW API Response Content (Start) ---");
                    System.out.println(rawContent);
                    System.out.println("--- RAW API Response Content (End) ---\n");

                    // JSON文字列が空であるか、有効なJSON形式でない場合のチェック
                    if (rawContent.trim().isEmpty() || (!rawContent.trim().startsWith("[") && !rawContent.trim().startsWith("{"))) {
                        System.out.println(prefecture + "の図書館情報: APIからの応答が不正な形式でした。");
                        continue;
                    }

                    JSONArray librariesArray = null;
                    try {
                        librariesArray = new JSONArray(rawContent);
                    } catch (JSONException e) {
                        System.err.println("JSONパースエラーが発生しました。受け取ったデータがJSON形式ではありません。");
                        System.err.println("エラーメッセージ: " + e.getMessage());
                        System.err.println("パースを試みた内容:\n" + rawContent); // エラー時の詳細な内容を出力
                        continue;
                    }

                    if (librariesArray.length() > 0) { // JSON配列に要素があるかチェック
                        System.out.println("\n-------------------------------------");
                        System.out.println(prefecture + "の図書館情報:");
                        System.out.println("-------------------------------------");
                        for (int i = 0; i < librariesArray.length(); i++) {
                            JSONObject lib = librariesArray.getJSONObject(i);
                            // 図書館の正式名称 (formal) を表示
                            System.out.println("図書館名: " + lib.optString("formal", "N/A"));
                            System.out.println("システムID: " + lib.optString("systemid", "N/A"));
                            System.out.println("住所: " + lib.optString("address", "N/A"));
                            System.out.println("URL: " + lib.optString("url_pc", "N/A"));
                            System.out.println("-------------------------------------");
                        }
                    } else {
                        // ライブラリが見つからなかった場合のメッセージ
                        System.out.println(prefecture + "の図書館情報は見つかりませんでした。");
                    }

                } else {
                    System.err.println("APIリクエストが失敗しました。HTTPエラーコード: " + responseCode);
                    // エラーレスポンスの内容を読み込む
                    try (BufferedReader errIn = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                        String errorLine;
                        StringBuilder errorContent = new StringBuilder();
                        while ((errorLine = errIn.readLine()) != null) {
                            errorContent.append(errorLine);
                        }
                        if (errorContent.length() > 0) {
                            System.err.println("エラーメッセージ: " + errorContent.toString());
                        } else {
                            System.err.println("エラーメッセージは提供されませんでした。");
                        }
                    }
                }

            } catch (Exception e) {
                System.err.println("予期せぬエラーが発生しました: " + e.getMessage());
                e.printStackTrace();
            }
        } // whileループの終わり
        scanner.close(); // アプリケーション終了時にScannerを閉じる
    }
}