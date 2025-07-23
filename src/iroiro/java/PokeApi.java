package iroiro.java;

import java.util.Scanner;
import java.util.Random;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Map;

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
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("ポケモンの番号を入力してください（ランダムの場合はEnterのみ）: ");
            String input = scanner.nextLine().toLowerCase();

            if (input.isEmpty()) {
                int maxPokemon = 1025;
                int randomId = new Random().nextInt(maxPokemon) + 1;
                input = String.valueOf(randomId);
                System.out.println("ランダムで選ばれた番号: " + input);
            }

            PokemonInfo info = getPokemonInfo(input);

            System.out.println("名前: " + info.japaneseName);
            System.out.println("タイプ: " + info.types);
            System.out.printf("高さ: %.1f m%n", info.height / 10.0);
            System.out.printf("重さ: %.1f kg%n", info.weight / 10.0);

        } catch (Exception e) {
            System.out.println("情報の取得に失敗しました。入力が正しいか確認してください。");
            // e.printStackTrace();
        }
    }

    public static PokemonInfo getPokemonInfo(String input) throws Exception {
        // ポケモン基本情報
        String apiUrl = "https://pokeapi.co/api/v2/pokemon/" + input;
        JSONObject json = getJsonFromUrl(apiUrl);
        int height = json.getInt("height");
        int weight = json.getInt("weight");

        // タイプを取得
        JSONArray typesArray = json.getJSONArray("types");
        StringBuilder typesStr = new StringBuilder();
        for (int i = 0; i < typesArray.length(); i++) {
            String enType = typesArray.getJSONObject(i).getJSONObject("type").getString("name");
            String jpType = typeNameMap.getOrDefault(enType, enType);
            if (typesStr.length() > 0)
                typesStr.append(", ");
            typesStr.append(jpType);
        }

        // 日本語名取得
        String speciesUrl = "https://pokeapi.co/api/v2/pokemon-species/" + input;
        JSONObject speciesJson = getJsonFromUrl(speciesUrl);
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

    private static JSONObject getJsonFromUrl(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return new JSONObject(response.toString());
        }
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