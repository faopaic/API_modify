package DiaryAPP.Java;

import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ReceiveDiaryUI {

    private static final String FIREBASE_URL = "https://teamf-6d71a-default-rtdb.asia-southeast1.firebasedatabase.app/diaries.json";

    public static void start(Terminal terminal, String currentUserId) throws Exception {
        terminal.puts(InfoCmp.Capability.clear_screen);
        terminal.puts(InfoCmp.Capability.cursor_invisible);
        PrintWriter out = terminal.writer();

        Map<String, Map<String, JSONObject>> othersDiaries = fetchOthersDiaries(currentUserId);

        if (othersDiaries.isEmpty()) {
            out.println("受信できる日記はありません。");
            out.println("Enterキーでメインメニューに戻ります...");
            out.flush();
            terminal.reader().read();
            return;
        }

        List<String> userList = new ArrayList<>(othersDiaries.keySet());
        int userIndex = 0;

        while (true) {
            terminal.puts(InfoCmp.Capability.clear_screen);
            out.println("=== 他のユーザーの選択 ===");
            for (int i = 0; i < userList.size(); i++) {
                if (i == userIndex) {
                    out.println("▶ \033[47;30m" + userList.get(i) + "\033[0m");
                } else {
                    out.println("   " + userList.get(i));
                }
            }
            out.println("↑/w ↓/s → Enter, Esc で戻る");
            out.flush();

            int ch = terminal.reader().read();
            if (ch == 27) { // Esc
                return;
            } else if (ch == 'w' || ch == 'W' || ch == 65) {
                userIndex = (userIndex - 1 + userList.size()) % userList.size();
            } else if (ch == 's' || ch == 'S' || ch == 66) {
                userIndex = (userIndex + 1) % userList.size();
            } else if (ch == 10 || ch == 13) {
                String selectedUser = userList.get(userIndex);
                selectDiaryOfUser(terminal, out, othersDiaries.get(selectedUser));
            }
        }
    }

    private static Map<String, Map<String, JSONObject>> fetchOthersDiaries(String currentUserId) throws Exception {
        Map<String, Map<String, JSONObject>> othersDiaries = new HashMap<>();

        URL url = new URL(FIREBASE_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (InputStream is = conn.getInputStream();
                Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name()).useDelimiter("\\A")) {

            String jsonText = scanner.hasNext() ? scanner.next() : "";

            if (!jsonText.isEmpty()) {
                JSONObject root = new JSONObject(jsonText);

                for (String userId : root.keySet()) {
                    if (userId.equals(currentUserId))
                        continue;

                    JSONObject userDiaries = root.getJSONObject(userId);
                    Map<String, JSONObject> dateMap = new TreeMap<>(Collections.reverseOrder());
                    for (String date : userDiaries.keySet()) {
                        JSONObject diary = userDiaries.getJSONObject(date);
                        if (diary.has("title") && diary.has("body")) {
                            dateMap.put(date, diary);
                        }
                    }

                    if (!dateMap.isEmpty()) {
                        othersDiaries.put(userId, dateMap);
                    }
                }
            }
        }

        return othersDiaries;
    }

    private static void selectDiaryOfUser(Terminal terminal, PrintWriter out, Map<String, JSONObject> diaryMap)
            throws Exception {
        List<String> dateList = new ArrayList<>(diaryMap.keySet());
        int dateIndex = 0;

        while (true) {
            terminal.puts(InfoCmp.Capability.clear_screen);
            out.println("=== 日付の選択 ===");
            for (int i = 0; i < dateList.size(); i++) {
                if (i == dateIndex) {
                    out.println("▶ \033[47;30m" + dateList.get(i) + "\033[0m");
                } else {
                    out.println("   " + dateList.get(i));
                }
            }
            out.println("↑/w ↓/s → Enter, Esc で戻る");
            out.flush();

            int ch = terminal.reader().read();
            if (ch == 27) { // Esc
                return;
            } else if (ch == 'w' || ch == 'W' || ch == 65) {
                dateIndex = (dateIndex - 1 + dateList.size()) % dateList.size();
            } else if (ch == 's' || ch == 'S' || ch == 66) {
                dateIndex = (dateIndex + 1) % dateList.size();
            } else if (ch == 10 || ch == 13) {
                String selectedDate = dateList.get(dateIndex);
                JSONObject diary = diaryMap.get(selectedDate);
                showDiary(terminal, out, selectedDate, diary);
            }
        }
    }

    private static void showDiary(Terminal terminal, PrintWriter out, String date, JSONObject diary) throws Exception {
        terminal.puts(InfoCmp.Capability.clear_screen);
        String title = diary.getString("title");
        String body = diary.getString("body");

        out.println("=== " + date + " の日記 ===");
        out.println("タイトル: " + title);
        out.println("本文:");
        out.println(body);
        out.println("\nEscキーで戻る");
        out.flush();

        int ch;
        do {
            ch = terminal.reader().read();
        } while (ch != 27); // 27 = Escキー
    }
}
