import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class RiseSetTimesByPrefectureMain {
    public static void main(String[] args) {
        // 都道府県のマップを定義
        Map<Integer, String> prefectures = new LinkedHashMap<>();
        prefectures.put(1, "北海道");
        prefectures.put(2, "青森県");
        prefectures.put(3, "岩手県");
        prefectures.put(4, "宮城県");
        prefectures.put(5, "秋田県");
        prefectures.put(6, "山形県");
        prefectures.put(7, "福島県");
        prefectures.put(8, "茨城県");
        prefectures.put(9, "栃木県");
        prefectures.put(10, "群馬県");
        prefectures.put(11, "埼玉県");
        prefectures.put(12, "千葉県");
        prefectures.put(13, "東京都");
        prefectures.put(14, "神奈川県");
        prefectures.put(15, "新潟県");
        prefectures.put(16, "富山県");
        prefectures.put(17, "石川県");
        prefectures.put(18, "福井県");
        prefectures.put(19, "山梨県");
        prefectures.put(20, "長野県");
        prefectures.put(21, "岐阜県");
        prefectures.put(22, "静岡県");
        prefectures.put(23, "愛知県");
        prefectures.put(24, "三重県");
        prefectures.put(25, "滋賀県");
        prefectures.put(26, "京都府");
        prefectures.put(27, "大阪府");
        prefectures.put(28, "兵庫県");
        prefectures.put(29, "奈良県");
        prefectures.put(30, "和歌山県");
        prefectures.put(31, "鳥取県");
        prefectures.put(32, "島根県");
        prefectures.put(33, "岡山県");
        prefectures.put(34, "広島県");
        prefectures.put(35, "山口県");
        prefectures.put(36, "徳島県");
        prefectures.put(37, "香川県");
        prefectures.put(38, "愛媛県");
        prefectures.put(39, "高知県");
        prefectures.put(40, "福岡県");
        prefectures.put(41, "佐賀県");
        prefectures.put(42, "長崎県");
        prefectures.put(43, "熊本県");
        prefectures.put(44, "大分県");
        prefectures.put(45, "宮崎県");
        prefectures.put(46, "鹿児島県");
        prefectures.put(47, "沖縄県");

        String selectedPrefecture = "";
        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name())) {

            System.out.println("--- 日の出・日の入り時刻や、月の出・月の入り時刻を出力したい都道府県を、以下のリストから番号で選択してください。---");
            int count = 0;
            for (Map.Entry<Integer, String> entry : prefectures.entrySet()) {
                System.out.printf("%2d: %-7s", entry.getKey(), entry.getValue());
                count++;
                if (count % 5 == 0) {
                    System.out.println();
                } else {
                    System.out.print("   ");
                }
            }
            if (count % 5 != 0) {
                System.out.println();
            }

            System.out.print("\n番号を入力してください: ");

            int choice = -1;
            while (true) {
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    if (prefectures.containsKey(choice)) {
                        selectedPrefecture = prefectures.get(choice);
                        break;
                    } else {
                        System.out.print("無効な番号です。もう一度入力してください: ");
                    }
                } else {
                    System.out.print("数字を入力してください。もう一度入力してください: ");
                    scanner.next();
                }
            }
            scanner.nextLine();

            // 1. 国土地理院APIで選択された都道府県の緯度・経度を取得
            double[] coordinates = RiseSetTimesByPrefectureUtil.getCoordinatesFromAddress(selectedPrefecture);

            if (coordinates == null) {
                System.out.println("選択された都道府県（" + selectedPrefecture + "）の緯度・経度を見つけることができませんでした。");
                return;
            }

            double latitude = coordinates[1];
            double longitude = coordinates[0];

            System.out.println("\n**選択された都道府県:** " + selectedPrefecture);
            System.out.println("緯度: " + latitude + ", 経度: " + longitude);

            java.time.LocalDate today = java.time.LocalDate.now();
            int year = today.getYear();
            int month = today.getMonthValue();
            int day = today.getDayOfMonth();

            System.out.println("\n--- " + year + "年" + month + "月" + day + "日の情報 ---");
            RiseSetTimesByPrefectureUtil.getRiseSetTimes(year, month, day, latitude, longitude);

        } catch (Exception e) {
            System.err.println("エラーが発生しました: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
