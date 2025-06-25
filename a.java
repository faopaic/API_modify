
public class a {
    public static void main(String[] args) throws Exception {
        String catFact = WebApiFunctions.getCatFact();
        
        try {
            String result = Translate.translateText(catFact, "JA");
            System.err.println("【ネコ豆知識】" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
