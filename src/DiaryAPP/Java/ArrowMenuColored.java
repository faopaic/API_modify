package DiaryAPP.Java;

import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.terminal.*;
import org.jline.utils.InfoCmp;

import java.util.Arrays;
import java.util.List;

public class ArrowMenuColored {
    public static void main(String[] args) throws Exception {
        Terminal terminal = TerminalBuilder.builder()
                .system(true)
    .dumb(false)
    .jna(true)
    .jansi(true)
    .build();

        LineReader reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .parser(new DefaultParser())
                .build();

        // LineReader を LineReaderImpl にキャスト
        LineReaderImpl implReader = (LineReaderImpl) reader;
        reader.readLine("", null, (MaskingCallback) null, null);

        List<String> menuItems = Arrays.asList("日記を書く", "過去の日記を読む", "他人の日記を受信する", "ログアウト");
        int selected = 0;

        // ANSIカラー定義
        final String RESET = "\u001B[0m";
        final String REVERSE = "\u001B[7m";
        final String CYAN = "\u001B[36m";
        final String BOLD = "\u001B[1m";

        while (true) {
            terminal.puts(InfoCmp.Capability.clear_screen);
            terminal.flush();

            System.out.println(BOLD + CYAN + "=== メインメニュー ===" + RESET);

            for (int i = 0; i < menuItems.size(); i++) {
                if (i == selected) {
                    System.out.println(REVERSE + " > " + menuItems.get(i) + " " + RESET);
                } else {
                    System.out.println("   " + menuItems.get(i));
                }
            }

            // 👇 キー入力を受け取ってバインディングを取得する（矢印キー対応）
            Binding binding = implReader.readBinding(implReader.getKeys());

            // バインディング名を取得（例: "up", "down", "enter" など）
            String keySeq = (binding instanceof Reference)
                    ? ((Reference) binding).name()
                    : "";

            switch (keySeq) {
                case "up":
                    selected = (selected - 1 + menuItems.size()) % menuItems.size();
                    break;
                case "down":
                    selected = (selected + 1) % menuItems.size();
                    break;
                case "enter":
                    System.out.println("\n" + BOLD + "[" + menuItems.get(selected) + "] を選択しました。" + RESET);
                    if (menuItems.get(selected).equals("ログアウト")) {
                        System.out.println("終了します。");
                        return;
                    }
                    reader.readLine("続けるにはEnterを押してください...");
                    break;
                default:
                    break;
            }
        }
    }
}
