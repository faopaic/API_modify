package DiaryAPP.Java;

import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.jline.keymap.BindingReader;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WriteDiaryUI {

    public static void start(Terminal terminal, String userId) throws Exception {
        PrintWriter out = terminal.writer();
        BindingReader reader = new BindingReader(terminal.reader());
        Scanner scanner = new Scanner(System.in, "Shift_JIS");

        terminal.puts(InfoCmp.Capability.clear_screen);
        out.println("=== 日記を書く ===");

        // タイトル入力
        out.print("タイトル: ");
        out.flush();
        String title = scanner.nextLine();

        // 本文入力
        out.println("本文（終わるには「:w」、中止するには「:q」と入力してEnter）:");
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        while (true) {
            int ch = reader.readCharacter();

            if (ch == 27) { // ESC キー
                out.println("\nメインメニューに戻ります...");
                out.flush();
                Thread.sleep(1000);
                return;
            } else if (ch == 10 || ch == 13) { // Enter キー
                String line = currentLine.toString();
                if (line.equals(":w"))
                    break;
                if (line.equals(":q")) {
                    out.println("\n入力を中止してメインメニューに戻ります...");
                    out.flush();
                    Thread.sleep(1000);
                    return;
                }
                lines.add(line);
                out.println();
                currentLine = new StringBuilder();
            } else if ((ch == 127 || ch == 8) && currentLine.length() > 0) {
                currentLine.deleteCharAt(currentLine.length() - 1);
                out.print("\b \b");
            } else if (ch >= 32 && ch <= 126) {
                currentLine.append((char) ch);
                out.print((char) ch);
            } else {
                // 日本語入力のようなマルチバイトは Scanner で取得（確定後）
                if (System.in.available() > 0) {
                    String fallbackLine = scanner.nextLine();
                    currentLine.append(fallbackLine);
                    out.print(fallbackLine);
                }
            }
            out.flush();
        }

        // 入力確認
        terminal.puts(InfoCmp.Capability.cursor_invisible);
        out.println();
        out.println("=== 入力内容確認 ===");
        out.println("タイトル: " + title);
        out.println("本文:");
        for (String line : lines) {
            out.println(" " + line);
        }
        out.println("\nこの内容で保存するにはEnterを、キャンセルするにはCtrl+Cを押してください");
        out.flush();

        while (true) {
            int ch = reader.readCharacter();
            if (ch == 10 || ch == 13)
                break;
        }

        terminal.puts(InfoCmp.Capability.cursor_invisible);
        out.println("保存処理に進みます...");
        out.flush();
        Thread.sleep(2000);

        // TODO: Firebaseへの保存処理をここに実装
    }
}
