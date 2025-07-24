package iroiro.java;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class SpotifyArtistSearch {

    private static final String CLIENT_ID = "8970f04edef74a48a89938617c70caaf";
    private static final String CLIENT_SECRET = "e35e22f9f69c44088446ae6115c88d5b";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("検索したいアーティスト名を入力してください: ");
        String artistName = scanner.nextLine();
        // scanner.close();

        if (artistName.trim().isEmpty()) {
            System.out.println("アーティスト名が空です。終了します。");
            return;
        }

        try {
            searchAndDisplayArtistInfo(artistName);
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void searchAndDisplayArtistInfo(String artistName) throws Exception {
        String token = getAccessToken();
        JSONObject artist = searchArtist(artistName, token);
        if (artist == null) {
            System.out.println("アーティストが見つかりませんでした。");
            return;
        }

        displayArtistInfo(artist);
        displayTopTracks(artist.getString("id"), token);
    }

    private static JSONObject searchArtist(String name, String token) throws Exception {
        String query = URLEncoder.encode(name, StandardCharsets.UTF_8);
        String url = "https://api.spotify.com/v1/search?q=" + query + "&type=artist&limit=1";
        JSONObject response = getJson(url, token);
        JSONArray items = response.getJSONObject("artists").getJSONArray("items");
        return items.isEmpty() ? null : items.getJSONObject(0);
    }

    private static void displayArtistInfo(JSONObject artist) {
        String name = artist.getString("name");
        String id = artist.getString("id");
        int followers = artist.getJSONObject("followers").getInt("total");
        String url = artist.getJSONObject("external_urls").getString("spotify");

        System.out.println("\nアーティスト情報");
        System.out.println("名前       : " + name);
        System.out.println("Spotify ID : " + id);
        System.out.println("フォロワー : " + String.format("%,d", followers) + "人");
        System.out.println("Spotify URL: " + url);
    }

    private static void displayTopTracks(String artistId, String token) throws Exception {
        String url = "https://api.spotify.com/v1/artists/" + artistId + "/top-tracks?market=JP";
        JSONObject response = getJson(url, token);
        JSONArray tracks = response.getJSONArray("tracks");

        System.out.println("\n人気トップ5曲");
        for (int i = 0; i < Math.min(5, tracks.length()); i++) {
            JSONObject track = tracks.getJSONObject(i);
            String name = track.getString("name");
            String trackUrl = track.getJSONObject("external_urls").getString("spotify");
            System.out.printf("%d. %s - %s%n", i + 1, name, trackUrl);
        }
    }

    private static JSONObject getJson(String url, String token) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .build();

        HttpResponse<String> response = sendRequest(request);
        return new JSONObject(response.body());
    }

    private static HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("APIエラー: " + response.statusCode() + " - " + response.body());
        }
        return response;
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

        HttpResponse<String> response = sendRequest(request);
        return new JSONObject(response.body()).getString("access_token");
    }
}
