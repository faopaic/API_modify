import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OsakaLoopApp {
    public static void main(String[] args) {
        OsakaLoopClient client = new OsakaLoopClient();
        try {
            List<Train> trains = client.fetchTrains();
            List<Train> delayTrains = new ArrayList<>();
            for (Train train : trains) {
                if (train.getDelayMinutes() > 0) {
                    delayTrains.add(train);
                }
            }
            if (delayTrains.isEmpty()) {
                System.out.println("遅延している車両はありません。");
            } else {
                System.out.println("遅延している車両は以下の通りです。");
                for (Train train : delayTrains) {
                    if (train.getNo().isEmpty()) {
                        System.out.println(String.format("%s: %s→%s 遅れ%d分",
                                train.getDisplayType(),
                                train.getPosFrom(),
                                train.getDestText(),
                                train.getDelayMinutes()));
                    } else {
                        System.out.println(String.format("%s(%s): %s→%s 遅れ%d分",
                                train.getDisplayType(),
                                train.getNo(),
                                train.getPosFrom(),
                                train.getPosTo(),
                                train.getDelayMinutes()));
                    }
                }
            }
        } catch (IOException | java.net.URISyntaxException | org.json.JSONException e) {
            e.printStackTrace();
        }
    }
}