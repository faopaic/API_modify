public class SpotifyApiMain {
    public static void main(String[] args) {
        try {
            // 日本の最新人気曲
            SpotifyApiUtil.printJapanTopTracks();
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }
}