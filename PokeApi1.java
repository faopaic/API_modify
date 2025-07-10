import java.util.Scanner;
import java.util.Random;

public class PokeApi1 {
    public static void main(String[] args) {
        // ★try-with-resources を削除し、Scanner のインスタンス化のみを行う★
        // sougou1.java で管理されている System.in をそのまま利用するため、
        // ここでScannerを閉じる必要はありません。
        // また、このクラス内で新しくScannerを作成する必要もありません。
        // しかし、PokeApi.main(new String[]{}) で呼び出されるため、
        // 呼び出し元のScannerインスタンスを直接渡すことはできません。
        // そのため、ここでは一時的に新しいScannerを作成しますが、閉じないようにします。
        
        // ★★★注意：この方法だと、PokeApi内で新しいScannerインスタンスが生成されますが、
        // System.inは閉じられません。もし、PokeApiが直接System.inを読み取る必要がある場合は、
        // こうなります。理想的には、呼び出し元からScannerインスタンスを渡す方が良いですが、
        // mainメソッドのシグネチャを変更できないため、この対応になります。★★★

        @SuppressWarnings("resource")
        Scanner tempScanner = new Scanner(System.in); // ここで新しいScannerを作成
        
        try {
            System.out.print("ポケモンの番号を入力してください（ランダムの場合はEnterのみ）: ");
            String input = tempScanner.nextLine().toLowerCase();

            if (input.isEmpty()) {
                // 1〜1025のランダムな番号を生成（必要に応じて最大値を調整）
                int maxPokemon = 1025;
                int randomId = new Random().nextInt(maxPokemon) + 1;
                input = String.valueOf(randomId);
                System.out.println("ランダムで選ばれた番号: " + input);
            }

            PokeApiUtil.PokemonInfo info = PokeApiUtil.getPokemonInfo(input);

            System.out.println("名前: " + info.japaneseName);
            System.out.println("タイプ: " + info.types);
            System.out.printf("高さ: %.1f m%n", info.height / 10.0);
            System.out.printf("重さ: %.1f kg%n", info.weight / 10.0);

        } catch (Exception e) {
            System.out.println("情報の取得に失敗しました。入力が正しいか確認してください。");
            // e.printStackTrace(); // デバッグ用
        } finally {
            // tempScanner.close(); // ★この行をコメントアウトまたは削除★
            // sougou1.java のメインのScannerがSystem.inを閉じないようにするため、
            // ここで閉じてはいけません。
        }
    }
}