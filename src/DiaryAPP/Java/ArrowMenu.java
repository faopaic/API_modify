package DiaryAPP.Java;

import org.jline.keymap.KeyMap;
import org.jline.keymap.BindingReader;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

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
        Terminal terminal = TerminalBuilder.terminal();
        BindingReader reader = new BindingReader(terminal.reader());
        KeyMap<String> keyMap = new KeyMap<>();

        // 矢印キーではなく、w/sキーで上下、enterで決定に変更
        keyMap.bind("up", "w");
        keyMap.bind("down", "s");
        keyMap.bind("enter", "\r"); // Enterキーはそのまま

        terminal.enterRawMode();

        while (true) {
            printMenu(terminal);
            String key = reader.readBinding(keyMap);

            if (key == null)
                continue;

            switch (key) {
                case "up":
                    selectedIndex = (selectedIndex - 1 + MENU_ITEMS.length) % MENU_ITEMS.length;
                    break;
                case "down":
                    selectedIndex = (selectedIndex + 1) % MENU_ITEMS.length;
                    break;
                case "enter":
                    terminal.writer().println("\n選択した項目: " + MENU_ITEMS[selectedIndex]);
                    terminal.flush();
                    return; // 必要ならメニュー遷移
                default:
                    // 他のキーは無視
                    break;
            }
        }
    }

    private void printMenu(Terminal terminal) {
        terminal.puts(org.jline.utils.InfoCmp.Capability.clear_screen);
        terminal.writer().println("=== メインメニュー ===\n");

        for (int i = 0; i < MENU_ITEMS.length; i++) {
            if (i == selectedIndex) {
                terminal.writer().print("\033[47;30m");
                terminal.writer().println(" > " + MENU_ITEMS[i] + " ");
                terminal.writer().print("\033[0m");
            } else {
                terminal.writer().println("   " + MENU_ITEMS[i]);
            }
        }
        terminal.flush();
    }
}
