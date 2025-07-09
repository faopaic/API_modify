import java.util.InputMismatchException;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;

public class sougou {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name()); 
        
        // ★while (true) ループを削除★
        System.out.println("\n--- 総合メニュー ---");
        System.out.println("1: 猫の豆知識を見る");
        System.out.println("2: 図書館検索をする");
        System.out.println("3: NASAの星画像を見る");
        System.out.println("4: 図鑑ナンバーからポケモンを調べる");
        System.out.println("5: 日・月の出入りを確認する");
        System.out.println("0: 終了");
        System.out.print("選択してください (番号を入力): ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // 改行文字を消費

            switch (choice) {
                case 1:
                    System.out.println("猫の豆知識を取得中...");
                    AnimalTriviaApp.main(new String[]{}); 
                    break;
                case 2:
                    System.out.println("図書館情報を検索中...");
                    LibrarySearchApp.main(new String[]{}); 
                    break;
                case 3:
                    System.out.println("NASAの星画像...");
                    NasaApodViewerMain.main(new String[]{}); 
                    break;
                case 4:
                    System.out.println("ポケモンを確認中...");
                    PokeApi.main(new String[]{});
                    break;
                case 5:
                    System.out.println("日・月の出入りを確認中...");
                    RiseSetTimesByPrefecture.main(new String[]{});
                    break;
                case 0:
                    System.out.println("プログラムを終了します。");
                    break; // breakでswitchを抜け、mainメソッドが終了する
                default:
                    System.out.println("無効な選択です。0, 1, または 2 を入力してください。");
                    break;
            }
        } catch (InputMismatchException e) {
            System.out.println("無効な入力です。数字を入力してください。");
            // scanner.next(); // ここでは不要、プログラムが終了するため
        } catch (Exception e) {
            System.err.println("予期せぬエラーが発生しました: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // mainメソッドの最後に到達したので、System.inを閉じます
            // これでプログラム終了時にリソースが解放されます
            scanner.close(); 
        }
    }
}