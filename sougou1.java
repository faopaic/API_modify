import java.util.InputMismatchException;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;

public class sougou1 {

    public static void main(String[] args) {
        // 標準入力から読み取るためのScannerオブジェクトを作成します。
        // 文字エンコーディングとしてUTF-8を指定し、日本語などのマルチバイト文字を正しく扱えるようにします。
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name()); 
        
        // ユーザーが「終了」を選択するまで無限にループします。
        while (true) { 
            // メニューオプションを表示します。
            System.out.println("\n--- 総合メニュー ---");
            System.out.println("1: 猫の豆知識を見る");
            System.out.println("2: 図書館検索をする");
            System.out.println("3: NASAの星画像を見る");
            System.out.println("4: 図鑑ナンバーからポケモンを調べる");
            System.out.println("5: 日・月の出入りを確認する");
            System.out.println("0: 終了");
            System.out.print("選択してください (番号を入力): ");

            try {
                // ユーザーの整数入力を読み取ります。
                int choice = scanner.nextInt();
                // nextInt()の後に残る改行文字を消費します。
                scanner.nextLine(); 

                // ユーザーの選択に基づいて適切なアクションを実行します。
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
                        PokeApi1.main(new String[]{});
                        break;
                    case 5:
                        System.out.println("日・月の出入りを確認中...");
                        RiseSetTimesByPrefecture.main(new String[]{});
                        break;
                    case 0:
                        System.out.println("プログラムを終了します。");
                        scanner.close(); 
                        return; 
                    default:
                        // 無効な入力があった場合にメッセージを表示します。
                        System.out.println("無効な選択です。0から5の番号を入力してください。");
                        break;
                }
            } catch (InputMismatchException e) {
                // ユーザーが数字以外のものを入力した場合のエラーを処理します。
                System.out.println("無効な入力です。数字を入力してください。");
                // 無効な入力を消費し、無限ループを防ぎます。
                scanner.next(); 
            } catch (Exception e) {
                // その他の予期せぬエラーを捕捉し、エラーメッセージとスタックトレースを出力します。
                System.err.println("予期せぬエラーが発生しました: " + e.getMessage());
                e.printStackTrace();
            }
            // finallyブロックは、プログラム終了時にのみScannerを閉じる必要があるため、ループ内からは削除されています。
            // ループ内でfinallyブロックがあると、最初の繰り返し後にScannerが閉じられ、その後の入力で問題が発生します。
            // 各Javaファイルの main メソッドの中に、scanner.close(); という行がないか確認し、もしあればその行を削除するかコメントアウトで実行可能
        }
    }
}