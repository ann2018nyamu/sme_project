package ke.co.eclectic.quickstore.models;

import org.json.JSONObject;

/**
 * Created by David Manduku on 08/10/2018.
 */
public class BusinessRoles {
    Integer roleid;
    String name;
    JSONObject rulesObj;

    /**
     * Gets roleid.
     *
     * @return the roleid
     */
    public Integer getRoleid() {
        return roleid;
    }

    /**
     * Sets roleid.
     *
     * @param roleid the roleid
     */
    public void setRoleid(Integer roleid) {
        this.roleid = roleid;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets rules obj.
     *
     * @return the rules obj
     */
    public JSONObject getRulesObj() {
        return rulesObj;
    }

    /**
     * Sets rules obj.
     *
     * @param rulesObj the rules obj
     */
    public void setRulesObj(JSONObject rulesObj) {
        this.rulesObj = rulesObj;
    }
}
