package DiaryAPP.Java;

import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.jline.keymap.BindingReader;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import org.json.JSONObject;

public class ReadDiaryUI {

    public static void start(Terminal terminal, String userId) throws Exception {
        PrintWriter out = terminal.writer();
        BindingReader reader = new BindingReader(terminal.reader());

        // Firebaseから過去の日記一覧を取得
        Map<String, String> diaryList = fetchDiaryList(userId);

        if (diaryList.isEmpty()) {
            terminal.puts(InfoCmp.Capability.clear_screen);
            out.println("過去の日記はありません。");
            out.flush();
            Thread.sleep(1500);
            return;
        }

        List<String> dates = new ArrayList<>(diaryList.keySet());
        Collections.sort(dates, Collections.reverseOrder()); // 新しい順に並べ替え

        int selected = 0;

        while (true) {
            terminal.puts(InfoCmp.Capability.clear_screen);
            out.println("=== 過去の日記一覧 ===");
            out.println("日付       | タイトル");
            out.println("---------------------------");
            for (int i = 0; i < dates.size(); i++) {
                String date = dates.get(i);
                String title = diaryList.get(date);
                String prefix = (i == selected) ? "▶ \033[47;30m" : "   ";
                String suffix = (i == selected) ? "\033[0m" : "";
                out.printf("%s%s | %s%s%n", prefix, date, title, suffix);
            }
            out.println("\n↑/w ↓/s → Enter  ESCで戻る");
            out.flush();

            int ch = reader.readCharacter();
            if (ch == 'w' || ch == 'W' || ch == 65) {
                selected = (selected - 1 + dates.size()) % dates.size();
            } else if (ch == 's' || ch == 'S' || ch == 66) {
                selected = (selected + 1) % dates.size();
            } else if (ch == 27) { // ESCキーで戻る
                return;
            } else if (ch == 10 || ch == 13) {
                showDiaryDetail(terminal, out, reader, userId, dates.get(selected));
            }
        }
    }

    private static Map<String, String> fetchDiaryList(String userId) {
        Map<String, String> diaryMap = new HashMap<>();
        try {
            String baseUrl = "https://teamf-6d71a-default-rtdb.asia-southeast1.firebasedatabase.app";
            URL url = new URL(baseUrl + "/diaries/" + userId + ".json");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            try (InputStream is = conn.getInputStream();
                    Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A")) {
                if (!scanner.hasNext())
                    return diaryMap;
                String result = scanner.next();
                JSONObject json = new JSONObject(result);

                for (String date : json.keySet()) {
                    JSONObject diary = json.getJSONObject(date);
                    String title = diary.optString("title", "（タイトルなし）");
                    diaryMap.put(date, title);
                }
            }
        } catch (Exception e) {
            // エラー時は空のまま返す
        }
        return diaryMap;
    }

    private static void showDiaryDetail(Terminal terminal, PrintWriter out, BindingReader reader, String userId,
            String date) throws Exception {
        String baseUrl = "https://teamf-6d71a-default-rtdb.asia-southeast1.firebasedatabase.app";
        URL url = new URL(baseUrl + "/diaries/" + userId + "/" + date + ".json");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        String title = "";
        String body = "";

        try (InputStream is = conn.getInputStream();
                Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A")) {
            if (scanner.hasNext()) {
                String result = scanner.next();
                JSONObject json = new JSONObject(result);
                title = json.optString("title", "（タイトルなし）");
                body = json.optString("body", "");
            }
        } catch (Exception e) {
            title = "取得エラー";
            body = "";
        }

        while (true) {
            terminal.puts(InfoCmp.Capability.clear_screen);
            out.println("=== 日記詳細 ===");
            out.println("日付: " + date);
            out.println("タイトル: " + title);
            out.println("本文:");
            out.println(body);
            out.println("\nESCで戻る");
            out.flush();

            int ch = reader.readCharacter();
            if (ch == 27) { // ESCキーで戻る
                return;
            }
        }
    }
}
