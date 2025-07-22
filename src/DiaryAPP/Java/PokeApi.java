package DiaryAPP.Java;

import java.util.Scanner;
import java.util.Random;

public class PokeApi {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("ポケモンの番号を入力してください（ランダムの場合はEnterのみ）: ");
            String input = scanner.nextLine().toLowerCase();

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
            // e.printStackTrace();
        }
    }
}