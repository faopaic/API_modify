import java.util.Scanner;

public class PokeApi {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("ポケモンの番号を入力してください: ");
            String input = scanner.nextLine().toLowerCase();

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