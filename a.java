
public class a {
    public static void main(String[] args) throws Exception {
        String catFact = WebApiFunctions.getCatFact();
        ;
        try {
            String apiKey = "3b177dc2-f6b1-4808-8304-94786f62f920:fx"; // ←ここだけ書き換える
            String result = Translate.translateText(catFact, "JA", apiKey);
            System.err.println("【ネコ豆知識】" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
