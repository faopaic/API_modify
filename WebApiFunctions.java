import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;
import org.json.JSONArray;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class WebApiFunctions {
    public static String getCatFact() throws Exception {
        URL url = new URL("https://catfact.ninja/fact");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        Scanner sc = new Scanner(conn.getInputStream());
        StringBuilder response = new StringBuilder();
        while (sc.hasNext()) {
            response.append(sc.nextLine());
        }
        sc.close();

        JSONObject json = new JSONObject(response.toString());
        return json.getString("fact");
    }

    public static String translateText(String text, String targetLang) throws Exception {
        // DeepL Free API のURL
        String apiKey = "3b177dc2-f6b1-4808-8304-94786f62f920:fx";
        URL url = new URL("https://api-free.deepl.com/v2/translate");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", "DeepL-Auth-Key " + apiKey);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // パラメータを作成
        String params = "text=" + text + "&target_lang=" + targetLang;

        try (OutputStream os = conn.getOutputStream()) {
            os.write(params.getBytes("UTF-8"));
        }

        // レスポンスを取得
        Scanner scanner = new Scanner(conn.getInputStream());
        StringBuilder response = new StringBuilder();
        while (scanner.hasNext()) {
            response.append(scanner.nextLine());
        }
        scanner.close();

        // JSONから翻訳文のみ取り出す
        JSONObject json = new JSONObject(response.toString());
        String translated = json.getJSONArray("translations")
                .getJSONObject(0)
                .getString("text");

        return translated;
    }

    public static String getRandomWord() throws Exception {
        String apiUrl = "https://random-word-api.herokuapp.com/word?number=1";

        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        Scanner sc = new Scanner(conn.getInputStream());
        StringBuilder response = new StringBuilder();
        while (sc.hasNext()) {
            response.append(sc.nextLine());
        }
        sc.close();

        JSONArray jsonArray = new JSONArray(response.toString());
        return jsonArray.getString(0);
    }

    public static String getMeaning(String word) {
        String apiUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Scanner sc = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (sc.hasNext()) {
                response.append(sc.nextLine());
            }
            sc.close();

            JSONArray jsonArray = new JSONArray(response.toString());
            JSONObject firstEntry = jsonArray.getJSONObject(0);
            JSONArray meanings = firstEntry.getJSONArray("meanings");
            JSONObject firstMeaning = meanings.getJSONObject(0);
            JSONArray definitions = firstMeaning.getJSONArray("definitions");
            JSONObject firstDefinition = definitions.getJSONObject(0);

            return firstDefinition.getString("definition");
        } catch (java.io.FileNotFoundException e) {
            try {
                return WebApiFunctions.translateText(word, "JA") + "（翻訳結果）";
            } catch (Exception ex) {
                ex.printStackTrace();
                return "意味が見つからず、翻訳も失敗しました";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "エラーが発生しました";
        }
    }

    /**
     * 指定した画像ファイルをアスキーアートに変換して返す
     * 
     * @param imagePath 画像ファイルのパス
     * @param width     アスキーアートの横幅（文字数）
     * @return アスキーアート文字列
     */
    public static String imageToAsciiArt(String imagePath, int width) {
        // ブロック体のみを濃い順に使用
        final String asciiChars = "█▓▒░ ";
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();
            int height = (int) ((double) originalHeight / originalWidth * width * 0.4);

            // 透過画像対応: 背景を白で塗りつぶす
            BufferedImage rgbImage = new BufferedImage(originalWidth, originalHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = rgbImage.createGraphics();
            g2d.setColor(java.awt.Color.WHITE);
            g2d.fillRect(0, 0, originalWidth, originalHeight);
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();

            Image scaled = rgbImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            BufferedImage colorImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2dGray = grayImage.createGraphics();
            g2dGray.drawImage(scaled, 0, 0, null);
            g2dGray.dispose();
            Graphics2D g2dColor = colorImage.createGraphics();
            g2dColor.drawImage(scaled, 0, 0, null);
            g2dColor.dispose();

            StringBuilder sb = new StringBuilder();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = grayImage.getRaster().getSample(x, y, 0);
                    int idx = (int) (pixel / 255.0 * (asciiChars.length() - 1));
                    int rgb = colorImage.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;
                    String color = String.format("\u001B[38;2;%d;%d;%dm", r, g, b);
                    sb.append(color).append(asciiChars.charAt(idx));
                }
                sb.append("\u001B[0m\n"); // 行末で色リセット
            }
            return sb.toString();
        } catch (IOException e) {
            return "画像の読み込みに失敗しました: " + e.getMessage();
        }
    }

}