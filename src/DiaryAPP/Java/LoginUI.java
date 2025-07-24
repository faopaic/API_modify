package DiaryAPP.Java;

import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.jline.keymap.BindingReader;

import java.io.PrintWriter;
import java.util.List;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import org.json.JSONObject;

public class LoginUI {
    private static final List<String> INPUT_ITEMS = List.of("ログインID: ", "パスワード: ");
    private static final List<Integer> INPUT_COL_OFFSETS = List.of(12, 12);

    public static void start(Terminal terminal) throws Exception {
        PrintWriter out = terminal.writer();
        BindingReader reader = new BindingReader(terminal.reader());
        StringBuilder[] inputs = { new StringBuilder(), new StringBuilder() };
        int focus = 0;

        out.print("\033[H\033[2J");
        out.println("+==========================================+");
        out.println("             --交換日記アプリ--");
        out.println("+==========================================+");
        out.println("                ログイン画面 ");
        out.println("+------------------------------------------+");

        for (String label : INPUT_ITEMS) {
            out.print("　　" + label);
            out.println();
        }
        out.println("+==========================================+");
        out.println("Escで戻る");
        out.flush();

        while (true) {
            int targetRow = 6 + focus;
            int col = INPUT_COL_OFFSETS.get(focus) + inputs[focus].length() + 4;
            out.print(String.format("\033[%d;%dH", targetRow, col));
            terminal.puts(InfoCmp.Capability.cursor_visible);
            out.flush();

            int ch = reader.readCharacter();
            if ((ch == 10 || ch == 13) && inputs[focus].length() > 0) {
                if (focus == INPUT_ITEMS.size() - 1)
                    break;
                else
                    focus++;
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

        String userId = inputs[0].toString();
        String password = inputs[1].toString();

        // Firebase Realtime Database のURL
        String baseUrl = "https://teamf-6d71a-default-rtdb.asia-southeast1.firebasedatabase.app";
        URI uri = new URI(baseUrl + "/users/" + userId + ".json");
        URL url = uri.toURL();

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
            br.close();

            String respStr = response.toString();
            boolean loginFailed = false;

            if (respStr.equals("null") || respStr.isEmpty()) {
                loginFailed = true;
            } else {
                JSONObject json = new JSONObject(respStr);
                if (!json.has("password") || !json.getString("password").equals(password)) {
                    loginFailed = true;
                } else {
                    // 認証成功

                    terminal.puts(InfoCmp.Capability.cursor_invisible);
                    WriteDiaryUI.showMessage(terminal, out, "ログインに成功しました", 0);

                    MainMenuUI.start(terminal, userId);
                    return; // 成功したらここで戻る
                }
            }

            if (loginFailed) {
                terminal.puts(InfoCmp.Capability.cursor_invisible);
                WriteDiaryUI.showMessage(terminal, out, "ログイン失敗：IDまたはパスワードが間違っています", 2000);
                out.flush();
                while (terminal.reader().ready()) {
                    terminal.reader().read();
                }
            }

            conn.disconnect();
        }
    }
}
//