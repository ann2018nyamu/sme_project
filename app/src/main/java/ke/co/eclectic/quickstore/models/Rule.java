package ke.co.eclectic.quickstore.models;


import org.json.JSONObject;

public class Rule {
    String rulename = "";
    boolean cancreate=false;
    boolean canview=false;
    boolean candelete=false;

    public Rule() {

    }
    public Rule(JSONObject jsonObject) {

    }

    public String getRulename() {
        return rulename;
    }

    public void setRulename(String rulename) {
        this.rulename = rulename;
    }



    public boolean isCancreate() {
        return cancreate;
    }

    public void setCancreate(boolean cancreate) {
        this.cancreate = cancreate;
    }

    public boolean isCanview() {
        return canview;
    }

    public void setCanview(boolean canview) {
        this.canview = canview;
    }

    public boolean isCandelete() {
        return candelete;
    }

    public void setCandelete(boolean candelete) {
        this.candelete = candelete;
    }
}
