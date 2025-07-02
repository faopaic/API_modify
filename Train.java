import org.json.JSONObject;

public class Train {
    private final String no;
    private final String posFrom;
    private final String posTo;
    private final String displayType;
    private final String destText;
    private final int delayMinutes;

    public Train(JSONObject trainObj) {
        this.no = trainObj.optString("no", "");
        String pos = trainObj.optString("pos", "");
        if (pos.contains("-")) {
            String[] parts = pos.split("-", 2);
            this.posFrom = parts.length > 0 ? parts[0] : "";
            this.posTo = parts.length > 1 ? parts[1] : "";
        } else {
            this.posFrom = pos;
            this.posTo = "";
        }
        this.displayType = trainObj.optString("displayType", "");
        JSONObject dest = trainObj.optJSONObject("dest");
        this.destText = dest != null ? dest.optString("text", "") : "";
        this.delayMinutes = trainObj.optInt("delayMinutes", 0);
    }

    public String getNo() { return no;}

    public String getPosFrom() {return posFrom;}

    public String getPosTo() {return posTo;}

    public String getDisplayType() {return displayType;}

    public String getDestText() {return destText;}

    public int getDelayMinutes() {return delayMinutes;}
}