package DiaryAPP.Java;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
 
public class FirebaseAuth {
private static final String DATABASE_URL = "https://.firebaseio.com"; // ←修正必要
 
    // ログインチェック関数（true = 成功）
    public static boolean checkLogin(String userId, String inputPassword) {
        try {
            String endpoint = DATABASE_URL + "/users/" + userId + ".json";
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
 
            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
 
                String json = response.toString();
 
                // 超シンプルな方法（Gsonなし）
                return json.contains("\"password\":\"" + inputPassword + "\"");
            }
        } catch (Exception e) {
            System.out.println("認証エラー: " + e.getMessage());
        }
        return false;
    }
}