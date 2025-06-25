import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Map;
import java.util.Scanner;

public class PokeApi {

    // 英語タイプ名 → 日本語タイプ名マップ
    private static final Map<String, String> typeNameMap = Map.ofEntries(
            Map.entry("normal", "ノーマル"),
            Map.entry("fire", "ほのお"),
            Map.entry("water", "みず"),
            Map.entry("electric", "でんき"),
            Map.entry("grass", "くさ"),
            Map.entry("ice", "こおり"),
            Map.entry("fighting", "かくとう"),
            Map.entry("poison", "どく"),
            Map.entry("ground", "じめん"),
            Map.entry("flying", "ひこう"),
            Map.entry("psychic", "エスパー"),
            Map.entry("bug", "むし"),
            Map.entry("rock", "いわ"),
            Map.entry("ghost", "ゴースト"),
            Map.entry("dragon", "ドラゴン"),
            Map.entry("dark", "あく"),
            Map.entry("steel", "はがね"),
            Map.entry("fairy", "フェアリー"));

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("ポケモン名またはIDを入力してください: ");
        String input = scanner.nextLine().toLowerCase();

        try {
            // ポケモン情報（英語名、身長、体重、タイプなど）
            String apiUrl = "https://pokeapi.co/api/v2/pokemon/" + input;
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject json = new JSONObject(response.toString());
            int height = json.getInt("height"); // dm単位
            int weight = json.getInt("weight"); // hg単位

            // タイプ取得
            JSONArray typesArray = json.getJSONArray("types");
            StringBuilder typesStr = new StringBuilder();
            for (int i = 0; i < typesArray.length(); i++) {
                String enType = typesArray.getJSONObject(i).getJSONObject("type").getString("name");
                String jpType = typeNameMap.getOrDefault(enType, enType);
                if (typesStr.length() > 0)
                    typesStr.append(", ");
                typesStr.append(jpType);
            }

            // 日本語名の取得（species API）
            String speciesUrl = "https://pokeapi.co/api/v2/pokemon-species/" + input;
            URL url2 = new URL(speciesUrl);
            HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
            conn2.setRequestMethod("GET");

            BufferedReader reader2 = new BufferedReader(
                    new InputStreamReader(conn2.getInputStream()));
            StringBuilder response2 = new StringBuilder();
            while ((line = reader2.readLine()) != null) {
                response2.append(line);
            }
            reader2.close();

            JSONObject speciesJson = new JSONObject(response2.toString());
            JSONArray namesArray = speciesJson.getJSONArray("names");

            String japaneseName = "(不明)";
            for (int i = 0; i < namesArray.length(); i++) {
                JSONObject nameObj = namesArray.getJSONObject(i);
                if (nameObj.getJSONObject("language").getString("name").equals("ja")) {
                    japaneseName = nameObj.getString("name");
                    break;
                }
            }

            // 表示
            System.out.println("名前（日本語）: " + japaneseName);
            System.out.printf("身長: %.1f m%n", height / 10.0);
            System.out.printf("体重: %.1f kg%n", weight / 10.0);
            System.out.println("タイプ: " + typesStr.toString());

        } catch (Exception e) {
            System.out.println("情報の取得に失敗しました。入力が正しいか確認してください。");
            // e.printStackTrace(); // 必要ならエラー詳細表示
        }
    }
}