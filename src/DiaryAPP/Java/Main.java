package DiaryAPP.Java;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Main {
    public static void main(String[] args) throws Exception {
        System.setProperty("https.protocols", "TLSv1.2");
        Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .jna(true)
                .encoding("shift-jis")
                .build();

        TopMenuUI.start(terminal); // アプリ起動時にトップメニューへ
        terminal.close(); // 最後にクローズ
    }
}
//