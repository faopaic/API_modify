package DiaryAPP.Java;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecipeSearchApp {
    static final String API_KEY = "b3d7218cc22e40f7b9a51c1d2fe44b81"; // Spoonacular API
    static final String DEEPL_API_KEY = "0c2a8955-4b13-425a-81ef-f53ed79da070:fx"; // DeepL API

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("使いたい材料をカンマ区切りで入力してください（例: tomato,cucumber）: ");
        String ingredients = scanner.nextLine().trim();
        scanner.close();
        try {
            // 材料からレシピ検索
            String urlString = "https://api.spoonacular.com/recipes/findByIngredients"
                    + "?ingredients=" + URLEncoder.encode(ingredients, "UTF-8")
                    + "&number=3"
                    + "&apiKey=" + API_KEY;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() != 200) {
                System.out.println("レシピの取得に失敗しました。");
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                result.append(line);
            reader.close();
            JSONArray recipes = new JSONArray(result.toString());
            System.out.println("\n見つかったレシピ:");
            for (int i = 0; i < recipes.length(); i++) {
                JSONObject recipe = recipes.getJSONObject(i);
                int id = recipe.getInt("id");
                String title = recipe.getString("title");
                String image = recipe.getString("image");
                System.out.println("\n レシピ名: " + title);
                String translated = translateWithDeepL(title);
                System.out.println(" 日本語訳: " + translated);
                System.out.println(" 画像URL: " + image);
                fetchRecipeInstructions(id);
            }
        } catch (Exception e) {
            System.out.println("エラーが発生しました:");
            e.printStackTrace();
        }
    }

    // 🔻 DeepL翻訳関数
    public static String translateWithDeepL(String text) {
        try {
            String urlStr = "https://api-free.deepl.com/v2/translate";
            String params = "auth_key=" + DEEPL_API_KEY +
                    "&text=" + URLEncoder.encode(text, "UTF-8") +
                    "&target_lang=JA";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            OutputStream os = conn.getOutputStream();
            os.write(params.getBytes());
            os.flush();
            os.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null)
                response.append(line);
            in.close();
            JSONObject json = new JSONObject(response.toString());
            return json.getJSONArray("translations").getJSONObject(0).getString("text");
        } catch (Exception e) {
            System.out.println("翻訳エラー: " + e.getMessage());
            return "（翻訳失敗）" + text;
        }
    }

    // 🔻 手順を取得＆翻訳して表示
    public static void fetchRecipeInstructions(int id) {
        try {
            String urlStr = "https://api.spoonacular.com/recipes/" + id + "/analyzedInstructions?apiKey=" + API_KEY;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                result.append(line);
            reader.close();
            JSONArray instructionsArray = new JSONArray(result.toString());
            if (instructionsArray.length() == 0) {
                System.out.println(" レシピ手順は見つかりませんでした。");
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
            System.out.println("手順取得エラー:");
            e.printStackTrace();
        }
    }
}
