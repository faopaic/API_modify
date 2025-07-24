package DiaryAPP.Java;

import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;

import iroiro.java.DateTimeApiUtil;
import iroiro.java.DateTimeInfo;
import iroiro.java.OpenWeatherMapUtil;
import iroiro.java.WeatherInfo;

import java.io.PrintWriter;
import java.util.List;

public class MainMenuUI {

    private static final List<String> MENU_ITEMS = List.of(
            "日記を書く",
            "過去の日記を読む",
            "他人の日記を受信する",
            "話題を探す",
            "ログアウト");

    public static void start(Terminal terminal, String userId) throws Exception {
        terminal.puts(InfoCmp.Capability.cursor_invisible);
        PrintWriter out = terminal.writer();
        int selected = 0;

        String city = "Osaka";
        WeatherInfo info = OpenWeatherMapUtil.getWeatherInfo(city);
        DateTimeInfo dtInfo = DateTimeApiUtil.getDateTime("Asia/Tokyo");
        String datetime = dtInfo.datetime;
        String date = datetime.substring(0, 10); // "2025-07-23"

        while (true) {
            terminal.puts(InfoCmp.Capability.clear_screen);

            out.println("+==========================================+");
            out.printf(" ユーザー名： %s\n", userId);
            out.printf(" 　　　日付： %s \n", date);
            out.printf(" 　　　天気： %s %s°C \n", info.weather, info.temp);
            out.println("+==========================================+");
            out.println("               メインメニュー ");
            out.println("+------------------------------------------+");

            // === メニュー出力 ===

            for (int i = 0; i < MENU_ITEMS.size(); i++) {
                if (i == selected) {
                    out.println("　▶ \033[47;30m" + MENU_ITEMS.get(i) + "\033[0m");
                } else {
                    out.println(" 　  " + MENU_ITEMS.get(i));
                }
            }

            out.println("+==========================================+");
            out.println("↑/↓ または w/s で移動、Enterで選択");
            out.flush();

            int ch = terminal.reader().read();
            if (ch == 'w' || ch == 'W' || ch == 65) {
                selected = (selected - 1 + MENU_ITEMS.size()) % MENU_ITEMS.size();
            } else if (ch == 's' || ch == 'S' || ch == 66) {
                selected = (selected + 1) % MENU_ITEMS.size();
            } else if (ch == 10 || ch == 13) {
                String choice = MENU_ITEMS.get(selected);
                if (choice.equals("ログアウト")) {
                    return;
                } else if (choice.equals("日記を書く")) {
                    WriteDiaryUI.start(terminal, userId, date, info);
                } else if (choice.equals("過去の日記を読む")) {
                    ReadDiaryUI.start(terminal, userId);
                } else if (choice.equals("他人の日記を受信する")) {
                    ReceiveDiaryUI.start(terminal, userId);
                } else if (choice.equals("話題を探す")) {
                    TopicSearchUI.start(terminal, userId);
                } else {
                    out.println("「" + choice + "」機能はまだ実装されていません。");
                    out.flush();
                    Thread.sleep(1500);
                    while (terminal.reader().ready()) {
                        terminal.reader().read();
                    }
                }
            }
        }
    }
}
