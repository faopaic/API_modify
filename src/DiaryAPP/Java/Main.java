package DiaryAPP.Java;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Main {
    public static void main(String[] args) throws Exception {
        Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .jna(true)
                .build();

        TopMenuUI.start(terminal); // アプリ起動時にトップメニューへ
        terminal.close(); // 最後にクローズ
    }
}