import java.io.*;
import java.net.*;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class AnimalTriviaApp{
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("知りたい動物を選んでください：");
        System.out.println("1. 猫");
        System.out.println("2. 犬");
        System.out.println("3. チーター");
        System.out.println("4. うさぎ");
        System.out.println("5. 鹿");
        System.out.println("6. カピバラ");
        System.out.print("番号を入力：");
        int choice = scanner.nextInt();
        scanner.nextLine();
        scanner.close();
        switch (choice) {
            case 1:
                fetchCatFact(); // 猫の雑学
                fetchAnimalFacts("cat"); // 猫の科学情報
                break;
            case 2:
                fetchAnimalFacts("dog");
                break;
            case 3:
                fetchAnimalFacts("cheetah");
                break;
            case 4:
                fetchAnimalFacts("rabbit");
                break;
            case 5:
                fetchAnimalFacts("deer");
                break;
            case 6:
                fetchAnimalFacts("capybara");
                break;
            default:
                System.out.println("無効な選択です。");
        }
    }

    public static void fetchCatFact() {
        try {
            URL url = new URL("https://catfact.ninja/fact");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                result.append(line);
            reader.close();
            String json = result.toString();
            String fact = json.split("\"fact\":\"")[1].split("\"")[0];
            System.out.println("猫の雑学（英語）:");
            System.out.println(fact);
            String translated = translateWithDeepL(fact);
            System.out.println("日本語訳:");
            System.out.println(translated);
        } catch (Exception e) {
            System.out.println("猫の豆知識取得エラー:");
            e.printStackTrace();
        }
    }

    public static void fetchAnimalFacts(String name) {
        try {
            String apiKey = "z2bXIF9o9aOVDsZ0gopmlQ==6jfhPiJdy2fbVWoR";
            URL url = new URL("https://api.api-ninjas.com/v1/animals?name=" + URLEncoder.encode(name, "UTF-8"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-Api-Key", apiKey);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                result.append(line);
            reader.close();
            JSONArray jsonArray = new JSONArray(result.toString());
            if (jsonArray.length() == 0) {
                System.out.println("データが見つかりませんでした。");
                return;
            }
            JSONObject obj = jsonArray.getJSONObject(0);
            JSONObject ch = obj.getJSONObject("characteristics");
            System.out.println(capitalize(name) + "の科学情報（翻訳済み）:");
            String nameJP = translateWithDeepL("Name: " + obj.optString("name", name));
            String lifespanJP = translateWithDeepL("Lifespan: " + ch.optString("lifespan", "Unknown"));
            String sizeJP = translateWithDeepL("Size: " + ch.optString("size", "Unknown"));
            String habitatJP = translateWithDeepL("Habitat: " + ch.optString("habitat", "Unknown"));
            String dietJP = translateWithDeepL("Diet: " + ch.optString("diet", "Unknown"));
            System.out.println(nameJP);
            System.out.println(lifespanJP);
            System.out.println(sizeJP);
            System.out.println(habitatJP);
            System.out.println(dietJP);
        } catch (Exception e) {
            System.out.println(name + "の豆知識取得エラー:");
            e.printStackTrace();
        }
    }

    public static String translateWithDeepL(String text) {
        try {
            String apiKey = "0c2a8955-4b13-425a-81ef-f53ed79da070:fx";
            URL url = new URL("https://api-free.deepl.com/v2/translate");
            String params = "auth_key=" + apiKey +
                    "&text=" + URLEncoder.encode(text, "UTF-8") +
                    "&target_lang=JA";
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

    private static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
