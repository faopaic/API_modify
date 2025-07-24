package iroiro.java;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class AnimalTriviaApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] animals = { "猫", "犬", "チーター", "うさぎ", "鹿", "カピバラ" };
        String[] apiNames = { "cat", "dog", "cheetah", "rabbit", "deer", "capybara" };

        System.out.println("知りたい動物を選んでください：");
        for (int i = 0; i < animals.length; i++) {
            System.out.printf("%d. %s%n", i + 1, animals[i]);
        }

        System.out.print("番号を入力：");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > animals.length) {
            System.out.println("無効な選択です。");
            return;
        }

        String selectedAnimal = apiNames[choice - 1];

        if (selectedAnimal.equals("cat")) {
            fetchCatFact();
        }

        fetchAnimalFacts(selectedAnimal);
    }

    public static void fetchCatFact() {
        try {
            String response = sendGet("https://catfact.ninja/fact");
            String fact = new JSONObject(response).getString("fact");
            System.out.println("猫の雑学（英語）:");
            System.out.println(fact);
            System.out.println("日本語訳:");
            System.out.println(translateWithDeepL(fact));
        } catch (Exception e) {
            System.out.println("猫の豆知識取得エラー:");
            e.printStackTrace();
        }
    }

    public static void fetchAnimalFacts(String name) {
        try {
            String url = "https://api.api-ninjas.com/v1/animals?name=" + URLEncoder.encode(name, "UTF-8");
            String response = sendGet(url, "X-Api-Key", "z2bXIF9o9aOVDsZ0gopmlQ==6jfhPiJdy2fbVWoR");

            JSONArray array = new JSONArray(response);
            if (array.isEmpty()) {
                System.out.println("データが見つかりませんでした。");
                return;
            }

            JSONObject ch = array.getJSONObject(0).getJSONObject("characteristics");

            System.out.println(capitalize(name) + "の科学情報（翻訳済み）:");
            System.out.println(translateWithDeepL("Name: " + name));
            System.out.println(translateWithDeepL("Lifespan: " + ch.optString("lifespan", "Unknown")));
            System.out.println(translateWithDeepL("Size: " + ch.optString("size", "Unknown")));
            System.out.println(translateWithDeepL("Habitat: " + ch.optString("habitat", "Unknown")));
            System.out.println(translateWithDeepL("Diet: " + ch.optString("diet", "Unknown")));
        } catch (Exception e) {
            System.out.println(name + "の豆知識取得エラー:");
            e.printStackTrace();
        }
    }

    public static String translateWithDeepL(String text) {
        try {
            String params = "auth_key=" + "0c2a8955-4b13-425a-81ef-f53ed79da070:fx" +
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
            return new JSONObject(json).getJSONArray("translations").getJSONObject(0).getString("text");
        } catch (Exception e) {
            System.out.println("翻訳エラー: " + e.getMessage());
            return "（翻訳失敗）" + text;
        }
    }

    private static String sendGet(String urlStr) throws IOException {
        return sendGet(urlStr, null, null);
    }

    private static String sendGet(String urlStr, String headerKey, String headerValue) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");
        if (headerKey != null && headerValue != null) {
            conn.setRequestProperty(headerKey, headerValue);
        }
        return readResponse(conn);
    }

    private static String readResponse(HttpURLConnection conn) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                result.append(line);
            return result.toString();
        }
    }

    private static String capitalize(String s) {
        return s.isEmpty() ? s : s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
