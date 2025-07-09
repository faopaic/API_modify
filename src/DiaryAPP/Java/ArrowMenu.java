package DiaryAPP.Java;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;

import java.io.IOException;

public class ArrowMenu {
    private static final String[] MENU_ITEMS = {
            "日記を書く",
            "過去の日記を読む",
            "他人の日記を受信する",
            "終了"
    };

    private int selectedIndex = 0;

    public void showMenu() throws IOException {
        Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .build();
        terminal.enterRawMode();
        terminal.echo(false);

        BindingReader reader = new BindingReader(terminal.reader());
        KeyMap<String> keyMap = new KeyMap<>();
        keyMap.bind("up", "w", "\033[A"); // wキーまたは↑キー
        keyMap.bind("down", "s", "\033[B"); // sキーまたは↓キー
        keyMap.bind("enter", "\r", "\n");

        boolean running = true;
        printMenu(terminal);

        while (running) {
            String key = reader.readBinding(keyMap);
            switch (key) {
                case "up":
                    selectedIndex = (selectedIndex - 1 + MENU_ITEMS.length) % MENU_ITEMS.length;
                    printMenu(terminal);
                    break;
                case "down":
                    selectedIndex = (selectedIndex + 1) % MENU_ITEMS.length;
                    printMenu(terminal);
                    break;
                case "enter":
                    terminal.writer().println("\n選択した項目: " + MENU_ITEMS[selectedIndex]);
                    terminal.flush();
                    running = false;
                    break;
                default:
                    break;
            }
        }

        terminal.echo(true);
        terminal.close();
    }

    private void printMenu(Terminal terminal) {
        // ANSIエスケープシーケンスで画面クリア（Windowsでも比較的動作安定）
        terminal.writer().print("\033[H\033[2J");
        terminal.flush();

        terminal.writer().println("=== メインメニュー ===\n");

        for (int i = 0; i < MENU_ITEMS.length; i++) {
            if (i == selectedIndex) {
                terminal.writer().print("\033[47;30m"); // 白背景＋黒文字
                terminal.writer().println(" > " + MENU_ITEMS[i] + " ");
                terminal.writer().print("\033[0m"); // 色リセット
            } else {
                terminal.writer().println("   " + MENU_ITEMS[i]);
            }
        }

        terminal.flush();
    }
}
