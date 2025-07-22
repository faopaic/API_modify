import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import org.json.JSONObject;

public class SpotifyApi {
    private static final String CLIENT_ID = "8970f04edef74a48a89938617c70caaf";
    private static final String CLIENT_SECRET = "e35e22f9f69c44088446ae6115c88d5b";

    public static void main(String[] args) {
        try {
            printJapanTopTracks();
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }

    public static ArtistInfo searchArtist(String artist) throws Exception {
        String token = getAccessToken();
        String query = java.net.URLEncoder.encode(artist, "UTF-8");
        String searchUrl = "https://api.spotify.com/v1/search?q=" + query + "&type=artist&limit=1";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(searchUrl))
                .header("Authorization", "Bearer " + token)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject json = new JSONObject(response.body());
        System.out.println("APIレスポンス: " + response.body());

        if (!json.getJSONObject("artists").has("items")) {
            throw new RuntimeException("itemsが取得できませんでした: " + response.body());
        }

        JSONObject artistObj = json.getJSONObject("artists").getJSONArray("items").getJSONObject(0);
        String name = artistObj.getString("name");
        String id = artistObj.getString("id");

        return new ArtistInfo(name, id);
    }

    private static String getAccessToken() throws Exception {
        String auth = CLIENT_ID + ":" + CLIENT_SECRET;
        String encoded = Base64.getEncoder().encodeToString(auth.getBytes());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://accounts.spotify.com/api/token"))
                .header("Authorization", "Basic " + encoded)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject json = new JSONObject(response.body());
        return json.getString("access_token");
    }

    public static class ArtistInfo {
        public final String name;
        public final String id;

        public ArtistInfo(String name, String id) {
            this.name = name;
            this.id = id;
        }
    }

    public static void printJapanTopTracks() throws Exception {
        String token = getAccessToken();
        String playlistId = "37i9dQZEVXbKXQ4mDTEBXq";
        String url = "https://api.spotify.com/v1/playlists/" + playlistId + "/tracks?market=JP&limit=50";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject json = new JSONObject(response.body());
        var items = json.getJSONArray("items");

        System.out.println("日本の最新人気曲（Spotify Japan Top 50 より）:");
        for (int i = 0; i < items.length(); i++) {
            JSONObject track = items.getJSONObject(i).getJSONObject("track");
            String name = track.getString("name");
            String artist = track.getJSONArray("artists").getJSONObject(0).getString("name");
            System.out.printf("%2d. %s / %s%n", i + 1, name, artist);
        }
    }
}
