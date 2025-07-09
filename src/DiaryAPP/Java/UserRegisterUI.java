package DiaryAPP.Java;

import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.jline.keymap.BindingReader;

import java.io.PrintWriter;
import java.util.List;

public class UserRegisterUI {

    private static final List<String> INPUT_ITEMS = List.of(
            "ユーザーID: ",
            "パスワード: "
    );

    private static final List<Integer> INPUT_COL_OFFSETS = List.of(
            12, // ユーザーIDの入力開始位置
            12  // パスワードの入力開始位置
    );

    public static void start(Terminal terminal) throws Exception {
        PrintWriter out = terminal.writer();
        BindingReader reader = new BindingReader(terminal.reader());

        StringBuilder[] inputs = {new StringBuilder(), new StringBuilder()};
        int focus = 0;

        // 初期画面表示
        out.print("\033[H\033[2J");
        out.println("=== ユーザー登録画面 ===");
        for (String label : INPUT_ITEMS) {
            out.print(label);
            out.println();
        }
        out.flush();

        while (true) {
            int targetRow = 2 + focus;
            int col = INPUT_COL_OFFSETS.get(focus) + inputs[focus].length();
            out.print(String.format("\033[%d;%dH", targetRow, col));
            terminal.puts(InfoCmp.Capability.cursor_visible);
            out.flush();

            int ch = reader.readCharacter();

            if ((ch == 10 || ch == 13) && inputs[focus].length() > 0) {
                if (focus == INPUT_ITEMS.size() - 1) {
                    break; // パスワードまで入力したら完了
                } else {
                    focus++;
                }
            } else if ((ch == 127 || ch == 8) && inputs[focus].length() > 0) {
                inputs[focus].deleteCharAt(inputs[focus].length() - 1);
                out.print("\b \b");
                out.flush();
            } else if (ch >= 32 && ch <= 126) {
                inputs[focus].append((char) ch);
                out.print(focus == 1 ? "*" : (char) ch);
                out.flush();
            }
        }

        terminal.puts(InfoCmp.Capability.cursor_visible);
        out.println();
        out.flush();

        // 本来はここで Firebase などに登録処理を行う
        terminal.puts(org.jline.utils.InfoCmp.Capability.cursor_invisible);
        out.println();
        out.println("ユーザー登録が完了しました！");
        out.flush();
        Thread.sleep(2000); // 2秒待ってトップメニューに戻る
    }
}
