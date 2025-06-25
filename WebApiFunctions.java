import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

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
}