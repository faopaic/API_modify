package DiaryAPP.Java;

public class Main {
    public static void main(String[] args) {
        try {
            ArrowMenu menu = new ArrowMenu();
            menu.showMenu();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}