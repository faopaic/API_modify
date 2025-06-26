import java.util.Scanner;

public class PokeApi {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("ポケモン名またはIDを入力してください: ");
            String input = scanner.nextLine().toLowerCase();

            PokeApiUtil.PokemonInfo info = PokeApiUtil.getPokemonInfo(input);

            System.out.println("名前（日本語）: " + info.japaneseName);
            System.out.printf("身長: %.1f m%n", info.height / 10.0);
            System.out.printf("体重: %.1f kg%n", info.weight / 10.0);
            System.out.println("タイプ: " + info.types);

        } catch (Exception e) {
            System.out.println("情報の取得に失敗しました。入力が正しいか確認してください。");
            // e.printStackTrace();
        }
    }
}