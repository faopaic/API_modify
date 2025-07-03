import java.util.Scanner;

public class NasaApodViewerMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("日付を入力してください（例: 2024-06-01）: ");
        String date = scanner.nextLine();
        scanner.close();

        NasaApodViewerWithDate apod = new NasaApodViewerWithDate();
        apod.showApod(date);
    }
}