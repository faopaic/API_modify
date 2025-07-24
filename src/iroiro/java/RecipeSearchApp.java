package iroiro.java;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecipeSearchApp {
    private static final String API_KEY = "b3d7218cc22e40f7b9a51c1d2fe44b81"; // Spoonacular API
    private static final String DEEPL_API_KEY = "0c2a8955-4b13-425a-81ef-f53ed79da070:fx"; // DeepL API

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("使いたい材料をカンマ区切りで入力してください（例: tomato,cucumber）: ");
        String ingredients = scanner.nextLine().trim();
        // scanner.close();

        try {
            JSONArray recipes = fetchRecipesByIngredients(ingredients);
            if (recipes.isEmpty()) {
                System.out.println("レシピが見つかりませんでした。");
                return;
            }

            System.out.println("\n見つかったレシピ:");
            for (int i = 0; i < recipes.length(); i++) {
                JSONObject recipe = recipes.getJSONObject(i);
                int id = recipe.getInt("id");
                String title = recipe.getString("title");
                String image = recipe.getString("image");

                System.out.println("\n レシピ名: " + title);
                System.out.println(" 日本語訳: " + translateWithDeepL(title));
                System.out.println(" 画像URL: " + image);

                fetchAndDisplayInstructions(id);
            }

        } catch (Exception e) {
            System.out.println("エラーが発生しました: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 🔻 材料からレシピ取得
    private static JSONArray fetchRecipesByIngredients(String ingredients) throws IOException {
        String url = "https://api.spoonacular.com/recipes/findByIngredients"
                + "?ingredients=" + URLEncoder.encode(ingredients, "UTF-8")
                + "&number=3"
                + "&apiKey=" + API_KEY;
        String response = sendGetRequest(url);
        return new JSONArray(response);
    }

    // 🔻 指定レシピIDの手順を取得＆翻訳表示
    private static void fetchAndDisplayInstructions(int id) {
        try {
            String url = "https://api.spoonacular.com/recipes/" + id + "/analyzedInstructions?apiKey=" + API_KEY;
            String response = sendGetRequest(url);

            JSONArray instructionsArray = new JSONArray(response);
            if (instructionsArray.isEmpty()) {
                System.out.println(" 手順情報は見つかりませんでした。");
                return;
            }

            JSONArray steps = instructionsArray.getJSONObject(0).getJSONArray("steps");
            System.out.println(" 手順（日本語訳付き）:");
            for (int i = 0; i < steps.length(); i++) {
                String stepText = steps.getJSONObject(i).getString("step");
                String stepTranslated = translateWithDeepL(stepText);
                System.out.println((i + 1) + ". " + stepTranslated);
            }

        } catch (Exception e) {
            System.out.println(" レシピ手順取得エラー: " + e.getMessage());
        }
    }

    // 🔻 DeepL翻訳
    private static String translateWithDeepL(String text) {
        try {
            String params = "auth_key=" + DEEPL_API_KEY +
                    "&text=" + URLEncoder.encode(text, "UTF-8") +
                    "&target_lang=JA";

            HttpURLConnection conn = (HttpURLConnection) new URL("https://api-free.deepl.com/v2/translate")
                    .openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(params.getBytes());
            }

            String json = readResponse(conn);
            return new JSONObject(json)
                    .getJSONArray("translations")
                    .getJSONObject(0)
                    .getString("text");

        } catch (Exception e) {
            System.out.println(" 翻訳失敗: " + e.getMessage());
            return "（翻訳失敗）" + text;
        }
    }

    // 🔻 共通GETリクエスト
    private static String sendGetRequest(String urlStr) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");
        return readResponse(conn);
    }

    // 🔻 共通レスポンス読み込み
    private static String readResponse(HttpURLConnection conn) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                result.append(line);
            return result.toString();
        }
    }
}
