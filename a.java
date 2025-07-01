
public class a {
    public static void main(String[] args) throws Exception {
        String result;
        try {
            // String catFact = WebApiFunctions.getCatFact();

            // result = WebApiFunctions.translateText(catFact, "JA");
            // System.err.println("【ネコ豆知識】" + result);
            String word = WebApiFunctions.getRandomWord();
            System.out.println(word);
            String mean = WebApiFunctions.getMeaning(word);
            result = WebApiFunctions.translateText(mean, "JA");
            System.out.println(result);
            System.out.println(WebApiFunctions.imageToAsciiArt("sample.jpg", 90));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
