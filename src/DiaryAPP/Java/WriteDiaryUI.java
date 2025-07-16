package DiaryAPP.Java;

import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.jline.keymap.BindingReader;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;

import org.json.JSONObject;

public class WriteDiaryUI {

    public static void start(Terminal terminal, String userId) throws Exception {
        PrintWriter out = terminal.writer();
        BindingReader reader = new BindingReader(terminal.reader());

        Scanner scanner = new Scanner(System.in, "Shift_JIS");

        try {
            List<String> lines = new ArrayList<>();
            boolean firstWrite = true;
            int selected = 0;
            String title = "";

            while (true) {
                terminal.puts(InfoCmp.Capability.clear_screen);
                out.println("=== 日記を書く ===");
                out.println((selected == 0 ? "▶ \033[47;30m" : "   ") + (firstWrite ? "内容を書き込む" : "続きを書く")
                        + (selected == 0 ? "\033[0m" : ""));
                out.println((selected == 1 ? "▶ \033[47;30m" : "   ") + "日記を保存する" + (selected == 1 ? "\033[0m" : ""));
                out.println((selected == 2 ? "▶ \033[47;30m" : "   ") + "キャンセル" + (selected == 2 ? "\033[0m" : ""));
                out.println("↑/w ↓/s → Enter");
                out.flush();

                int ch = reader.readCharacter();
                if (ch == 'w' || ch == 'W' || ch == 65) {
                    selected = (selected - 1 + 3) % 3;
                } else if (ch == 's' || ch == 'S' || ch == 66) {
                    selected = (selected + 1) % 3;
                } else if (ch == 10 || ch == 13) {
                    if (selected == 2)
                        return;

                    if (selected == 0) {
                        terminal.puts(InfoCmp.Capability.clear_screen);

                        if (firstWrite) {
                            out.print("タイトル: ");
                            out.flush();
                            title = scanner.nextLine();
                        }

                        // これまでの本文を表示
                        for (String line : lines) {
                            out.println(line);
                        }

                        // カーソルを最後の行の末尾に移動
                        int lastLineLength = lines.isEmpty() ? 0 : lines.get(lines.size() - 1).length();
                        int cursorRow = 1 + lines.size(); // タイトル1行 + 本文行数
                        int cursorCol = lastLineLength + 1;

                        out.print(String.format("\033[%d;%dH", cursorRow, cursorCol));
                        out.flush();

                        // ここでカーソル表示ON
                        terminal.puts(InfoCmp.Capability.cursor_visible);
                        terminal.flush();

                        scanner = new Scanner(System.in, "Shift_JIS");
                        while (true) {
                            String line = scanner.nextLine();
                            if (line.trim().isEmpty())
                                break;
                            lines.add(line);
                            out.println(line);
                        }

                        // 入力終了したらカーソル非表示に戻す
                        terminal.puts(InfoCmp.Capability.cursor_invisible);
                        terminal.flush();

                        firstWrite = false;
                    }

                    if (selected == 1) {
                        int confirmIndex = 0;
                        while (true) {
                            terminal.puts(InfoCmp.Capability.clear_screen);
                            out.println("=== 入力内容確認 ===");
                            out.println("タイトル: " + title);
                            out.println("本文:");
                            for (String line : lines) {
                                out.println(" " + line);
                            }
                            out.println();

                            out.print(confirmIndex == 0 ? "\033[47;30m▶ 保存する\033[0m" : "   保存する");
                            out.println("    " + (confirmIndex == 1 ? "\033[47;30m▶ キャンセル\033[0m" : "   キャンセル"));
                            out.flush();

                            int input = reader.readCharacter();
                            if (input == 'a' || input == 'A' || input == 68) {
                                confirmIndex = (confirmIndex - 1 + 2) % 2;
                            } else if (input == 'd' || input == 'D' || input == 67) {
                                confirmIndex = (confirmIndex + 1) % 2;
                            } else if (input == 10 || input == 13) {
                                if (confirmIndex == 1)
                                    return;
                                else
                                    break;
                            }
                        }

                        terminal.puts(InfoCmp.Capability.cursor_invisible);
                        out.println("保存処理に進みます...");
                        out.flush();
                        Thread.sleep(2000);

                        String date = getCurrentDate();

                        try {
                            String baseUrl = "https://teamf-6d71a-default-rtdb.asia-southeast1.firebasedatabase.app";
                            URL url = new URL(baseUrl + "/diaries/" + userId + ".json");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Content-Type", "application/json; utf-8");
                            conn.setDoOutput(true);

                            JSONObject json = new JSONObject();
                            json.put("userId", userId);
                            json.put("date", date);
                            json.put("title", title);
                            json.put("body", String.join("\n", lines));

                            try (OutputStream os = conn.getOutputStream()) {
                                byte[] input = json.toString().getBytes("utf-8");
                                os.write(input, 0, input.length);
                            }

                            int responseCode = conn.getResponseCode();
                            if (responseCode == 200) {
                                out.println("日記が保存されました。");
                            } else {
                                out.println("保存に失敗しました（HTTP " + responseCode + "）");
                            }
                            out.flush();
                            Thread.sleep(2000);
                        } catch (Exception e) {
                            out.println("エラーが発生しました: " + e.getMessage());
                            out.flush();
                            Thread.sleep(2000);
                        }
                        return;
                    }
                }
            }
        } finally {
            scanner.close();
        }
    }

    public static String getCurrentDate() {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return now.format(formatter);
    }
}
