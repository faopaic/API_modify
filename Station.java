import org.json.JSONObject;

public class Station {
    private final String code;
    private final String name;

    public Station(JSONObject stationObj){
        JSONObject info = stationObj.getJSONObject("info");
        if(info != null){
            this.code = info.optString("code", "");
            this.name = info.optString("name", "");
        } else {
            this.code = "";
            this.name = "";
        }
    }

    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
}
