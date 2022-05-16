package ke.co.eclectic.quickstore.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


/**
 * Created by David Manduku on 08/10/2018.
 */
@Entity(tableName = "business")

public class Business {
    @PrimaryKey
    @NonNull
    public Integer businessid = 0;
    public String bName;
    public String bCountry;
    public String bTown;
    public String bDialCode;
    public String bTelephone;
    public String bPostalAddr;
    public String bLocation;
    public String bEmail;
    public String bWebsite;
    public String bOwnerId;
    public String jsonData;
    public double bCreatedTime;


    /**
     * Instantiates a new Business.
     */
    public Business() {

    }

    /**
     * Gets telephone.
     *
     * @return the telephone
     */
    public String getbTelephone() {
        return bTelephone;
    }

    /**
     * Sets telephone.
     *
     * @param bTelephone the b telephone
     */
    public void setbTelephone(String bTelephone) {
        this.bTelephone = bTelephone;
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
     * Gets created time.
     *
     * @return the created time
     */
    public double getbCreatedTime() {
        return bCreatedTime;
    }

    /**
     * Sets created time.
     *
     * @param bCreatedTime the b created time
     */
    public void setbCreatedTime(double bCreatedTime) {
        this.bCreatedTime = bCreatedTime;
    }

    /**
     * Gets owner id.
     *
     * @return the owner id
     */
    public String getbOwnerId() {
        return bOwnerId;
    }

    /**
     * Sets owner id.
     *
     * @param bOwnerId the b owner id
     */
    public void setbOwnerId(String bOwnerId) {
        this.bOwnerId = bOwnerId;
    }

    /**
     * Gets businessid.
     *
     * @return the businessid
     */
    @NonNull
    public Integer getBusinessid() {
        return businessid;
    }

    /**
     * Sets businessid.
     *
     * @param businessid the businessid
     */
    public void setBusinessid(@NonNull Integer businessid) {
        this.businessid = businessid;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getbName() {
        if(bName == null){
            bName = "";
        }
        return bName;
    }

    /**
     * Sets name.
     *
     * @param bName the b name
     */
    public void setbName(String bName) {
        this.bName = bName;
    }

    /**
     * Gets country.
     *
     * @return the country
     */
    public String getbCountry() {
        return bCountry;
    }

    /**
     * Sets country.
     *
     * @param bCountry the b country
     */
    public void setbCountry(String bCountry) {
        this.bCountry = bCountry;
    }

    /**
     * Gets town.
     *
     * @return the town
     */
    public String getbTown() {
        return bTown;
    }

    /**
     * Sets town.
     *
     * @param bTown the b town
     */
    public void setbTown(String bTown) {
        this.bTown = bTown;
    }

    /**
     * Gets dial code.
     *
     * @return the dial code
     */
    public String getbDialCode() {
        return bDialCode;
    }

    /**
     * Sets dial code.
     *
     * @param bDialCode the b dial code
     */
    public void setbDialCode(String bDialCode) {
        this.bDialCode = bDialCode;
    }


    /**
     * Gets postal addr.
     *
     * @return the postal addr
     */
    public String getbPostalAddr() {
        return bPostalAddr;
    }

    /**
     * Sets postal addr.
     *
     * @param bPostalAddr the b postal addr
     */
    public void setbPostalAddr(String bPostalAddr) {
        this.bPostalAddr = bPostalAddr;
    }

    /**
     * Gets location.
     *
     * @return the location
     */
    public String getbLocation() {
        return bLocation;
    }

    /**
     * Sets location.
     *
     * @param bLocation the b location
     */
    public void setbLocation(String bLocation) {
        this.bLocation = bLocation;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getbEmail() {
        return bEmail;
    }

    /**
     * Sets email.
     *
     * @param bEmail the b email
     */
    public void setbEmail(String bEmail) {
        this.bEmail = bEmail;
    }

    /**
     * Gets website.
     *
     * @return the website
     */
    public String getbWebsite() {
        return bWebsite;
    }

    /**
     * Sets website.
     *
     * @param bWebsite the b website
     */
    public void setbWebsite(String bWebsite) {
        this.bWebsite = bWebsite;
    }
}
