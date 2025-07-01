import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Map;

public class PokeApiUtil {

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

    public static PokemonInfo getPokemonInfo(String input) throws Exception {
        // ポケモン情報（英語名、身長、体重、タイプなど）
        String apiUrl = "https://pokeapi.co/api/v2/pokemon/" + input;
        URL url = java.net.URI.create(apiUrl).toURL();
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
        URL url2 = java.net.URI.create(speciesUrl).toURL();
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

        return new PokemonInfo(japaneseName, height, weight, typesStr.toString());
    }

    // 情報をまとめるクラス
    public static class PokemonInfo {
        public final String japaneseName;
        public final String types;
        public final int height;
        public final int weight;
        

        public PokemonInfo(String japaneseName, int height, int weight, String types) {
            this.japaneseName = japaneseName;
            this.types = types;
            this.height = height;
            this.weight = weight;
            
        }
    }
}