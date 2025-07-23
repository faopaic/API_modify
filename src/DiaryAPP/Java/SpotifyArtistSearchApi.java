package DiaryAPP.Java;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class SpotifyArtistSearchApi {
    private static final String CLIENT_ID = "8970f04edef74a48a89938617c70caaf";
    private static final String CLIENT_SECRET = "e35e22f9f69c44088446ae6115c88d5b";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("検索したいアーティスト名を入力してください: ");
            String artistName = scanner.nextLine();

            if (artistName.trim().isEmpty()) {
                System.out.println("アーティスト名が空です。終了します。");
                return;
            }

            searchArtistAndShowTopTracks(artistName);
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void searchArtistAndShowTopTracks(String artistName) throws Exception {
        String token = getAccessToken();
        String encodedName = URLEncoder.encode(artistName, StandardCharsets.UTF_8);
        String searchUrl = "https://api.spotify.com/v1/search?q=" + encodedName + "&type=artist&limit=1";

        HttpRequest searchRequest = HttpRequest.newBuilder()
                .uri(URI.create(searchUrl))
                .header("Authorization", "Bearer " + token)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> searchResponse = client.send(searchRequest, HttpResponse.BodyHandlers.ofString());

        if (searchResponse.statusCode() != 200) {
            throw new RuntimeException("検索エラー: " + searchResponse.statusCode() + " " + searchResponse.body());
        }

        JSONObject json = new JSONObject(searchResponse.body());
        JSONArray items = json.getJSONObject("artists").getJSONArray("items");

        if (items.isEmpty()) {
            System.out.println("アーティストが見つかりませんでした。");
            return;
        }

        JSONObject artist = items.getJSONObject(0);
        String name = artist.getString("name");
        String id = artist.getString("id");
        int followers = artist.getJSONObject("followers").getInt("total");
        String spotifyUrl = artist.getJSONObject("external_urls").getString("spotify");

        System.out.println("\nアーティスト情報");
        System.out.println("名前       : " + name);
        System.out.println("Spotify ID : " + id);
        System.out.println("フォロワー : " + String.format("%,d", followers) + "人");
        System.out.println("Spotify URL: " + spotifyUrl);

        // トップトラック取得
        String topTracksUrl = "https://api.spotify.com/v1/artists/" + id + "/top-tracks?market=JP";

        HttpRequest topTracksRequest = HttpRequest.newBuilder()
                .uri(URI.create(topTracksUrl))
                .header("Authorization", "Bearer " + token)
                .build();

        HttpResponse<String> topTracksResponse = client.send(topTracksRequest, HttpResponse.BodyHandlers.ofString());

        if (topTracksResponse.statusCode() != 200) {
            throw new RuntimeException(
                    "トップトラック取得エラー: " + topTracksResponse.statusCode() + " " + topTracksResponse.body());
        }

        JSONObject tracksJson = new JSONObject(topTracksResponse.body());
        JSONArray tracks = tracksJson.getJSONArray("tracks");

        System.out.println("\n人気トップ5曲");
        for (int i = 0; i < Math.min(5, tracks.length()); i++) {
            JSONObject track = tracks.getJSONObject(i);
            String trackName = track.getString("name");
            String trackUrl = track.getJSONObject("external_urls").getString("spotify");
            System.out.printf("%d. %s - %s%n", i + 1, trackName, trackUrl);
        }
    }

    private static String getAccessToken() throws Exception {
        String auth = CLIENT_ID + ":" + CLIENT_SECRET;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://accounts.spotify.com/api/token"))
                .header("Authorization", "Basic " + encodedAuth)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("トークン取得エラー: " + response.statusCode() + " " + response.body());
        }

        JSONObject json = new JSONObject(response.body());
        return json.getString("access_token");
    }
}
