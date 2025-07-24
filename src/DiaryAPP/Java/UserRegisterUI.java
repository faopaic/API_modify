package DiaryAPP.Java;

import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.jline.keymap.BindingReader;
import java.io.PrintWriter;
import java.util.List;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
// import java.io.InputStreamReader;
// import java.io.BufferedReader;
import java.io.OutputStream;
import org.json.JSONObject;

public class UserRegisterUI {
    private static final List<String> INPUT_ITEMS = List.of("ユーザーID: ", "パスワード: ");
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
        out.println("              ユーザー登録画面 ");
        out.println("+------------------------------------------+");
        for (String label : INPUT_ITEMS) {
            out.print("　　" + label);
            out.println();
        }
        out.println("+==========================================+");
        out.println("↑Enterで決定、Escで戻る");
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

        String baseUrl = "https://teamf-6d71a-default-rtdb.asia-southeast1.firebasedatabase.app";
        URI uri = new URI(baseUrl + "/users/" + userId + ".json");
        URL url = uri.toURL();

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setDoOutput(true);

        // JSONデータ作成
        JSONObject json = new JSONObject();
        json.put("userId", userId);
        json.put("password", password);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = json.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            terminal.puts(InfoCmp.Capability.cursor_invisible);
            out.println();
            WriteDiaryUI.showMessage(terminal, out, "ユーザー登録が完了しました！", 2000);

            out.flush();
            while (terminal.reader().ready()) {
                terminal.reader().read();
            }
        } else {
            // エラーメッセージ
            // BufferedReader br = new BufferedReader(new
            // InputStreamReader(conn.getErrorStream(), "utf-8"));
            // StringBuilder errorResponse = new StringBuilder();
            // String line;
            // while ((line = br.readLine()) != null) {
            // errorResponse.append(line.trim());
            // }
            terminal.puts(InfoCmp.Capability.cursor_invisible);
            out.println();
            WriteDiaryUI.showMessage(terminal, out, "\"登録に失敗しました（HTTP \" + responseCode + \"）\"", 2000);
            out.flush();
            while (terminal.reader().ready()) {
                terminal.reader().read();
            }
        }
    }
}
//