import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * JR大阪環状線の駅状況
 *
 * 利用API: https://www.train-guide.westjr.co.jp/api/v3/osakaloop_st.json
 */
public class OsakaLoopStationClient {
    private static final String URL_STRING = "https://www.train-guide.westjr.co.jp/api/v3/osakaloop_st.json";

    public List<Station> fetchStations() throws IOException, java.net.URISyntaxException,
            org.json.JSONException {
        URL url = new URI(URL_STRING).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("データの取得に失敗しました。レスポンスコード: " + responseCode);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            JSONObject data = new JSONObject(response.toString());
            JSONArray stations = data.optJSONArray("stations");
            if (stations == null) {
                stations = new JSONArray();
            }

            List<Station> stationList = new ArrayList<>();
            for (int i = 0; i < stations.length(); i++) {
                stationList.add(new Station(stations.getJSONObject(i)));
            }
            return stationList;
        }
    }
}