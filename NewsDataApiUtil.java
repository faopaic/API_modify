import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;

public class NewsDataApiUtil {

    private static final String API_KEY = "pub_7d5740fbad644a58af2843c264e14cd0";
    private static final String API_URL = "https://newsdata.io/api/1/news?apikey=" + API_KEY
            + "&country=jp&language=ja";

    public static void printJapanNews() throws Exception {
        URI uri = new URI(API_URL);
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseBuilder.append(line);
        }
        reader.close();

        String response = responseBuilder.toString();
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray results = jsonResponse.getJSONArray("results");

        for (int i = 0; i < results.length(); i++) {
            JSONObject article = results.getJSONObject(i);
            String title = article.optString("title");
            String description = article.optString("description");
            String link = article.optString("link");

            System.out.println("■ 記事 " + (i + 1));
            System.out.println("タイトル: " + title);
            System.out.println("説明: " + description);
            System.out.println("URL: " + link);
            System.out.println();
        }
    }
}
