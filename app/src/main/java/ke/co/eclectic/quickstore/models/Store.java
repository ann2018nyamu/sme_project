package ke.co.eclectic.quickstore.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by David Manduku on 08/10/2018.
 */
@Entity(tableName = "store")
public class Store {
    @PrimaryKey
    @NonNull
    public Integer storeid=0;
    public Integer storeuserid=0;
    public Integer storetypeid=0;
    public String name = "";
    public String rolename= "";
    public String storename= "";
    public String storetype= "";
    public String location= "";
    public String country= "";
    public String countrycode= "";
    public String phonenumber= "";
    public String postaladdress="";
    public String email= "";
    public String website= "";
    public String jsonData= "";
    public Integer businessid=0;
    public String businessname= "";
    public double sCreatedTime =0.00;


    /**
     * Gets country.
     *
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets country.
     *
     * @param country the country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets location.
     *
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets location.
     *
     * @param location the location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets countrycode.
     *
     * @return the countrycode
     */
    public String getCountrycode() {
        return countrycode;
    }

    /**
     * Sets countrycode.
     *
     * @param countrycode the countrycode
     */
    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    /**
     * Gets phonenumber.
     *
     * @return the phonenumber
     */
    public String getPhonenumber() {
        return phonenumber;
    }

    /**
     * Sets phonenumber.
     *
     * @param phonenumber the phonenumber
     */
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    /**
     * Gets postaladdress.
     *
     * @return the postaladdress
     */
    public String getPostaladdress() {
        return postaladdress;
    }

    /**
     * Sets postaladdress.
     *
     * @param postaladdress the postaladdress
     */
    public void setPostaladdress(String postaladdress) {
        this.postaladdress = postaladdress;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets website.
     *
     * @return the website
     */
    public String getWebsite() {
        return website;
    }

    /**
     * Sets website.
     *
     * @param website the website
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     * Gets storetypeid.
     *
     * @return the storetypeid
     */
    public Integer getStoretypeid() {
        return storetypeid;
    }

    /**
     * Sets storetypeid.
     *
     * @param storetypeid the storetypeid
     */
    public void setStoretypeid(Integer storetypeid) {
        this.storetypeid = storetypeid;
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
     * Sets businessname.
     *
     * @param businessname the businessname
     */
    public void setBusinessname(String businessname) {
        this.businessname = businessname;
    }

    /**
     * Gets created time.
     *
     * @return the created time
     */
    public double getsCreatedTime() {
        return sCreatedTime;
    }

    /**
     * Sets created time.
     *
     * @param sCreatedTime the s created time
     */
    public void setsCreatedTime(double sCreatedTime) {
        this.sCreatedTime = sCreatedTime;
    }

    /**
     * Instantiates a new Store.
     */
    public Store() {

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
     * Gets storetype.
     *
     * @return the storetype
     */
    public String getStoretype() {
        return storetype;
    }

    /**
     * Sets storetype.
     *
     * @param storetype the storetype
     */
    public void setStoretype(String storetype) {
        this.storetype = storetype;
    }


    /**
     * Gets json data.
     *
     * @return the json data
     */
    public String getJsonData() {
        return jsonData;
    }

    /**
     * Sets json data.
     *
     * @param jsonData the json data
     */
    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
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
     * Sets storename.
     *
     * @param storename the storename
     */
    public void setStorename(String storename) {
        this.storename = storename;
    }

    /**
     * Gets storeid.
     *
     * @return the storeid
     */
    @NonNull
    public Integer getStoreid() {
        return storeid;
    }

    /**
     * Sets storeid.
     *
     * @param storeid the storeid
     */
    public void setStoreid(@NonNull Integer storeid) {
        this.storeid = storeid;
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
     * Sets storeuserid.
     *
     * @param storeuserid the storeuserid
     */
    public void setStoreuserid(Integer storeuserid) {
        this.storeuserid = storeuserid;
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
     * Sets rolename.
     *
     * @param rolename the rolename
     */
    public void setRolename(String rolename) {
        this.rolename = rolename;
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
     * Sets businessid.
     *
     * @param businessid the businessid
     */
    public void setBusinessid(Integer businessid) {
        this.businessid = businessid;
    }

}
