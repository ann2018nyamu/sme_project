package ke.co.eclectic.quickstore.models;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import timber.log.Timber;

public class Roles {
    String businessname;
    Integer businessid;
    String storename;
    String storelocation;
    Integer storeid;
    Integer storeuserid;
    String rolename;
    Integer createdby;
    Integer storecount;
    JsonObject rules;


    /**
     * Gets storelocation.
     *
     * @return the storelocation
     */
    public String getStorelocation() {
        return storelocation;
    }

    /**
     * Sets storelocation.
     *
     * @param storelocation the storelocation
     */
    public void setStorelocation(String storelocation) {
        this.storelocation = storelocation;
    }

    /**
     * Gets createdby.
     *
     * @return the createdby
     */
    public Integer getCreatedby() {
        return createdby;
    }

    /**
     * Gets storecount.
     *
     * @return the storecount
     */
    public Integer getStorecount() {
        return storecount;
    }

    /**
     * Gets businessname.
     *
     * @return the businessname
     */
    public String getBusinessname() {
         return businessname;
    }

    /**
     * Gets businessid.
     *
     * @return the businessid
     */
    public Integer getBusinessid() {
        return businessid;
    }

    /**
     * Gets storename.
     *
     * @return the storename
     */
    public String getStorename() {
        return storename;
    }

    /**
     * Gets storeid.
     *
     * @return the storeid
     */
    public Integer getStoreid() {
        return storeid;
    }

    /**
     * Gets storeuserid.
     *
     * @return the storeuserid
     */
    public Integer getStoreuserid() {
        return storeuserid;
    }

    /**
     * Gets rolename.
     *
     * @return the rolename
     */
    public String getRolename() {
        return rolename;
    }

    /**
     * Gets rules.
     *
     * @return the rules
     */
    public JsonObject getRules() {
        return rules;
    }

    /**
     * Sets businessname.
     *
     * @param businessname the businessname
     */
    public void setBusinessname(String businessname) {
        this.businessname = businessname;
    }

    /**
     * Sets businessid.
     *
     * @param businessid the businessid
     */
    public void setBusinessid(Integer businessid) {
        this.businessid = businessid;
    }

    /**
     * Sets storename.
     *
     * @param storename the storename
     */
    public void setStorename(String storename) {
        this.storename = storename;
    }

    /**
     * Sets storeid.
     *
     * @param storeid the storeid
     */
    public void setStoreid(Integer storeid) {
        this.storeid = storeid;
    }

    /**
     * Sets storeuserid.
     *
     * @param storeuserid the storeuserid
     */
    public void setStoreuserid(Integer storeuserid) {
        this.storeuserid = storeuserid;
    }

    /**
     * Sets rolename.
     *
     * @param rolename the rolename
     */
    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    /**
     * Sets createdby.
     *
     * @param createdby the createdby
     */
    public void setCreatedby(Integer createdby) {
        this.createdby = createdby;
    }

    /**
     * Sets storecount.
     *
     * @param storecount the storecount
     */
    public void setStorecount(Integer storecount) {
        this.storecount = storecount;
    }

    /**
     * Sets rules.
     *
     * @param rules the rules
     */
    public void setRules(JsonObject rules) {
        this.rules = rules;
    }

    public ArrayList<Rule>  getRuleList(){
        ArrayList<Rule> ruleList =new ArrayList<>();
        Rule rule=null;
        for(Iterator<String> iter = rules.keySet().iterator(); iter.hasNext();) {
            String key = iter.next();
            rule = new Rule();
            rule.setRulename(key);
            if(rules.get(key).toString().contains("cancreate")){
                rule.setCancreate(true);
            }
            if(rules.get(key).toString().contains("canview")){
                rule.setCanview(true);
            }
            if(rules.get(key).toString().contains("candelete")){
                rule.setCandelete(true);
            }
            ruleList.add(rule);
        }

        return ruleList;
    }
}
