public class OpenWeatherMapMain {
    public static void main(String[] args) {
        String city = "Osaka"; // 取得したい都市名
        try {
            OpenWeatherMapUtil.printWeather(city);
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }
}