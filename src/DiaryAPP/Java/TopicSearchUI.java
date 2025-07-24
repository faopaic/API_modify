package DiaryAPP.Java;

import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;

import iroiro.java.AdviceApi;
import iroiro.java.AnimalTriviaApp;
import iroiro.java.LibrarySearchApp;
import iroiro.java.NasaApodViewerWithDate;
import iroiro.java.NewsDataApiExample;
import iroiro.java.Poetry;
import iroiro.java.PokeApi;
import iroiro.java.RecipeSearchApp;
import iroiro.java.RiseSetTimesByPrefecture;
import iroiro.java.SpotifyArtistSearch;
import iroiro.java.WikiPersonSearch;

import org.jline.keymap.BindingReader;

import java.io.PrintWriter;
import java.util.List;

public class TopicSearchUI {

    private static final List<String> MENU_ITEMS = List.of(
            "アドバイスを取得する",
            "動物の生体情報を見る",
            "図書館検索をする",
            "NASAの星画像を見る",
            "ニュース記事を取得する",
            "ポエムを取得する",
            "図鑑ナンバーからポケモンを調べる",
            "食材から料理レシピを探す",
            "日・月の出入りを確認する",
            "Spotifyでアーティストを検索する",
            "WikiPediaで人物を検索する",
            "ランダムで詩を取得する",
            "戻る");

    public static void start(Terminal terminal, String userId) throws Exception {
        terminal.puts(InfoCmp.Capability.cursor_invisible);
        PrintWriter out = terminal.writer();
        BindingReader reader = new BindingReader(terminal.reader());

        int selected = 0;

        while (true) {
            terminal.puts(InfoCmp.Capability.clear_screen);
            out.println("+==========================================+");
            out.println("                 話題を探す ");
            out.println("+------------------------------------------+");
            for (int i = 0; i < MENU_ITEMS.size(); i++) {
                if (i == selected) {
                    out.println("▶ \033[47;30m" + MENU_ITEMS.get(i) + "\033[0m");
                } else {
                    out.println("   " + MENU_ITEMS.get(i));
                }
            }
            out.println("+==========================================+");
            out.println("\n↑/w ↓/s → Enter  ESCで戻る");
            out.flush();

            int ch = reader.readCharacter();

            if (ch == 'w' || ch == 'W' || ch == 65) {
                selected = (selected - 1 + MENU_ITEMS.size()) % MENU_ITEMS.size();
            } else if (ch == 's' || ch == 'S' || ch == 66) {
                selected = (selected + 1) % MENU_ITEMS.size();
            } else if (ch == 27) { // ESCで戻る
                return;
            } else if (ch == 10 || ch == 13) { // Enter
                if (MENU_ITEMS.get(selected).equals("戻る")) {
                    return;
                }
                handleSelection(terminal, out, MENU_ITEMS.get(selected));
            }
        }
    }

    private static void handleSelection(Terminal terminal, PrintWriter out, String choice) throws Exception {
        terminal.puts(InfoCmp.Capability.clear_screen);
        out.println("=== " + choice + " ===");
        out.flush();

        switch (choice) {
            case "アドバイスを取得する":
                AdviceApi.main(new String[] {});
                break;
            case "動物の生体情報を見る":
                AnimalTriviaApp.main(new String[] {});
                break;
            case "図書館検索をする":
                LibrarySearchApp.main(new String[] {});
                break;
            case "NASAの星画像を見る":
                NasaApodViewerWithDate.main(new String[] {});
                break;
            case "ニュース記事を取得する":
                NewsDataApiExample.main(new String[] {});
                break;
            case "ポエムを取得する":
                Poetry.main(new String[] {});
                break;
            case "図鑑ナンバーからポケモンを調べる":
                PokeApi.main(new String[] {});
                break;
            case "食材から料理レシピを探す":
                RecipeSearchApp.main(new String[] {});
                break;
            case "日・月の出入りを確認する":
                RiseSetTimesByPrefecture.main(new String[] {});
                break;
            case "Spotifyでアーティストを検索する":
                SpotifyArtistSearch.main(new String[] {});
                break;
            case "WikiPediaで人物を検索する":
                WikiPersonSearch.main(new String[] {});
                break;
            case "ランダムで詩を取得する":
                Poetry.main(new String[] {});
                break;
            default:
                out.println("未実装の項目です。");
                out.flush();
                Thread.sleep(1500);
                break;
        }

        out.println("\n処理が完了しました。Enterキーで戻る...");
        out.flush();

        // Enterキー待ち
        int ch;
        do {
            ch = terminal.reader().read();
        } while (ch != 10 && ch != 13);
    }
}
