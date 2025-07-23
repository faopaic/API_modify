package DiaryAPP.Java;

import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;

import java.io.PrintWriter;
import java.util.List;

public class TopMenuUI {
    private static final List<String> MENU_ITEMS = List.of(
            "ユーザー登録",
            "ログイン",
            "終了");

    public static void start(Terminal terminal) throws Exception {
        terminal.puts(org.jline.utils.InfoCmp.Capability.cursor_invisible);
        PrintWriter out = terminal.writer();
        int selected = 0;

        while (true) {
            terminal.puts(InfoCmp.Capability.clear_screen);
            out.println("+==========================================+");
            out.println("             --交換日記アプリ--");
            out.println("+==========================================+");
            out.println("               トップメニュー ");
            out.println("+------------------------------------------+");
            for (int i = 0; i < MENU_ITEMS.size(); i++) {
                if (i == selected) {
                    out.println("▶ \033[47;30m" + MENU_ITEMS.get(i) + "\033[0m");
                } else {
                    out.println("   " + MENU_ITEMS.get(i));
                }
            }
            out.println("+==========================================+");
            out.println("↑/w ↓/s → Enter");
            out.flush();

            int ch = terminal.reader().read();
            if (ch == 'w' || ch == 'W' || ch == 65) {
                selected = (selected - 1 + MENU_ITEMS.size()) % MENU_ITEMS.size();
            } else if (ch == 's' || ch == 'S' || ch == 66) {
                selected = (selected + 1) % MENU_ITEMS.size();
            } else if (ch == 10 || ch == 13) {
                switch (selected) {
                    case 0 -> UserRegisterUI.start(terminal);
                    case 1 -> LoginUI.start(terminal); // ←ログイン画面へ
                    case 2 -> {
                        out.println("終了します...");
                        out.flush();
                        return;
                    }
                }
            }
        }
    }
}
//