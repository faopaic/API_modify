package DiaryAPP.Java;

import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.jline.keymap.BindingReader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.json.JSONObject;

public class WriteDiaryUI {

    public static void start(Terminal terminal, String userId, String date, WeatherInfo info) throws Exception {
        PrintWriter out = terminal.writer();
        BindingReader reader = new BindingReader(terminal.reader());
        Scanner scanner = new Scanner(System.in, "Shift_JIS");

        List<String> lines = new ArrayList<>();
        boolean firstWrite = true;
        String title = "";

        int selected = 0;
        final int MENU_SIZE = 3;

        while (true) {
            drawMenu(terminal, out, selected, firstWrite, userId, date, info);
            int ch = reader.readCharacter();

            if (ch == 'w' || ch == 'W' || ch == 65) { // ↑キー or w
                selected = (selected - 1 + MENU_SIZE) % MENU_SIZE;
            } else if (ch == 's' || ch == 'S' || ch == 66) { // ↓キー or s
                selected = (selected + 1) % MENU_SIZE;
            } else if (ch == 27) { // Escキー
                return;
            } else if (ch == 10 || ch == 13) { // Enterキー
                switch (selected) {
                    case 0 -> {
                        title = writeContent(terminal, out, scanner, title, lines, firstWrite);
                        firstWrite = false;
                    }
                    case 1 -> saveDiary(terminal, out, reader, userId, title, lines);
                    case 2 -> {
                        if (isDiarySaved(userId)) {
                            shareDiary(terminal, out, reader, userId);
                        } else {
                            showMessage(terminal, out, "まず日記を保存してください。", 2000);
                        }
                    }
                }
            }
        }
    }

    private static void drawMenu(Terminal terminal, PrintWriter out, int selected, boolean firstWrite, String userId,
            String date, WeatherInfo info)
            throws Exception {
        terminal.puts(InfoCmp.Capability.clear_screen);

        out.println("+==========================================+");
        out.printf(" ユーザー名： %s\n", userId);
        out.printf(" 　　　日付： %s \n", date);
        out.printf(" 　　　天気： %s %s°C \n", info.weather, info.temp);
        out.println("+==========================================+");
        out.println("               メインメニュー ");
        out.println("+------------------------------------------+");
        String[] options = {
                firstWrite ? "内容を書き込む" : "続きを書く",
                "日記を保存する",
                "日記を共有する"
        };
        for (int i = 0; i < options.length; i++) {
            String prefix = selected == i ? "▶ \033[47;30m" : "   ";
            String suffix = selected == i ? "\033[0m" : "";
            out.println(prefix + options[i] + suffix);
        }
        out.println("+==========================================+");
        out.println("↑/↓ または w/s で移動、Enterで選択、Escで戻る");
        out.flush();
    }

    private static String writeContent(Terminal terminal, PrintWriter out, Scanner scanner,
            String title, List<String> lines, boolean firstWrite) {
        terminal.puts(InfoCmp.Capability.clear_screen);
        if (firstWrite) {
            out.print("タイトル: ");
            out.flush();
            title = scanner.nextLine();
        }

        terminal.puts(InfoCmp.Capability.clear_screen);
        out.println("タイトル: " + title);
        out.println("本文（空行で終了）:");
        lines.forEach(out::println);
        out.flush();

        terminal.puts(InfoCmp.Capability.cursor_visible);
        terminal.flush();

        while (true) {
            String line = scanner.nextLine();
            if (line.trim().isEmpty())
                break;
            lines.add(line);
        }

        terminal.puts(InfoCmp.Capability.cursor_invisible);
        terminal.flush();

        return title;
    }

    private static void saveDiary(Terminal terminal, PrintWriter out, BindingReader reader,
            String userId, String title, List<String> lines) throws Exception {
        int index = 0;
        while (true) {
            terminal.puts(InfoCmp.Capability.clear_screen);
            out.println("=== 保存確認 ===");
            out.println("タイトル: " + title);
            out.println("本文:");
            for (String line : lines)
                out.println(" " + line);
            out.println();

            out.println((index == 0 ? "▶ \033[47;30m保存する\033[0m" : "  保存する ")
                    + (index == 1 ? "▶ \033[47;30mキャンセル\033[0m" : "  キャンセル "));
            out.flush();

            int ch = reader.readCharacter();
            if (ch == 'a' || ch == 'A' || ch == 68)
                index = (index - 1 + 2) % 2;
            else if (ch == 'd' || ch == 'D' || ch == 67)
                index = (index + 1) % 2;
            else if (ch == 10 || ch == 13) {
                if (index == 1)
                    return;

                out.println("保存中...");
                out.flush();

                String date = getToday();
                String baseUrl = "https://teamf-6d71a-default-rtdb.asia-southeast1.firebasedatabase.app";
                URL url = new URL(baseUrl + "/diaries/" + userId + "/" + date + ".json");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("title", title);
                json.put("body", String.join("\n", lines));
                json.put("shared", false);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.toString().getBytes("utf-8"));
                }

                int code = conn.getResponseCode();
                if (code == 200)
                    showMessage(terminal, out, "保存しました。", 1500);
                else
                    showMessage(terminal, out, "保存失敗（HTTP " + code + "）", 2000);
                return;
            }
        }
    }

    private static boolean isDiarySaved(String userId) {
        try {
            String date = getToday();
            String baseUrl = "https://teamf-6d71a-default-rtdb.asia-southeast1.firebasedatabase.app";
            URL url = new URL(baseUrl + "/diaries/" + userId + "/" + date + ".json");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            try (InputStream is = conn.getInputStream();
                    Scanner s = new Scanner(is).useDelimiter("\\A")) {
                if (s.hasNext()) {
                    String result = s.next();
                    JSONObject json = new JSONObject(result);
                    return json.has("title") && json.has("body");
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private static void shareDiary(Terminal terminal, PrintWriter out, BindingReader reader, String userId) {
        try {
            String date = getToday();
            String baseUrl = "https://teamf-6d71a-default-rtdb.asia-southeast1.firebasedatabase.app";
            URL getUrl = new URL(baseUrl + "/diaries/" + userId + "/" + date + ".json");

            HttpURLConnection getConn = (HttpURLConnection) getUrl.openConnection();
            getConn.setRequestMethod("GET");
            getConn.setRequestProperty("Accept", "application/json");

            JSONObject diaryJson;
            try (InputStream is = getConn.getInputStream();
                    Scanner s = new Scanner(is).useDelimiter("\\A")) {
                if (!s.hasNext()) {
                    showMessage(terminal, out, "共有失敗: データが存在しません", 2000);
                    return;
                }
                String result = s.next();
                diaryJson = new JSONObject(result);
            }

            diaryJson.put("shared", true);

            HttpURLConnection putConn = (HttpURLConnection) getUrl.openConnection();
            putConn.setRequestMethod("PUT");
            putConn.setRequestProperty("Content-Type", "application/json; utf-8");
            putConn.setDoOutput(true);

            try (OutputStream os = putConn.getOutputStream()) {
                os.write(diaryJson.toString().getBytes("utf-8"));
            }

            int code = putConn.getResponseCode();
            if (code == 200)
                showMessage(terminal, out, "共有しました。", 1500);
            else
                showMessage(terminal, out, "共有失敗（HTTP " + code + "）", 2000);

        } catch (Exception e) {
            try {
                showMessage(terminal, out, "エラー: " + e.getMessage(), 2000);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public static void showMessage(Terminal terminal, PrintWriter out, String msg, int millis)
            throws InterruptedException {
        terminal.puts(InfoCmp.Capability.clear_screen);
        out.println(msg);
        out.flush();
        Thread.sleep(millis);
    }

    private static String getToday() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
