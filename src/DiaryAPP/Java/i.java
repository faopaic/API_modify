package DiaryAPP.Java;

import java.io.IOException;

import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class i {
    public static void main(String[] args) throws IOException {
        Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .build();

        terminal.enterRawMode();
        terminal.echo(false);
        BindingReader reader = new BindingReader(terminal.reader());
        KeyMap<String> keyMap = new KeyMap<>();
        keyMap.bind("up", "w", "\033[A");
        keyMap.bind("down", "s", "\033[B");
        keyMap.bind("enter", "\r", "\n");

        int selected = 0;
        String[] menu = { "A", "B", "C" };
        boolean running = true;

        while (running) {
            terminal.writer().print("\033[H\033[2J");
            terminal.flush();

            for (int i = 0; i < menu.length; i++) {
                if (i == selected) {
                    terminal.writer().println("> " + menu[i]);
                } else {
                    terminal.writer().println("  " + menu[i]);
                }
            }
            terminal.flush();

            String key = reader.readBinding(keyMap);
            switch (key) {
                case "up":
                    selected = (selected - 1 + menu.length) % menu.length;
                    break;
                case "down":
                    selected = (selected + 1) % menu.length;
                    break;
                case "enter":
                    running = false;
                    break;
            }
        }

        terminal.echo(true);
        terminal.close();
    }

}
