package ke.co.eclectic.quickstore.models;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by David Manduku on 08/10/2018.
 */
@Entity(tableName = "supplier",indices = {@Index(value = {"supplierid"}, unique = true)})
public class Supplier {


    @PrimaryKey
    @NonNull
    private Integer supplierid=0;
    private String companyname="";
    private String contactname="";
    private String identificationnumber="";
    private String country="";
    private String countrycode="";
    private String town="";
    private String phonenumber="";
    private String othernumber="";
    private String email="";
    private String otheremail="";
    @SerializedName("business_businessid")
    private Integer businessid;
    private Integer addedby;
    private Integer addedon;


    /**
     * Gets supplierid.
     *
     * @return the supplierid
     */
    @NonNull
    public Integer getSupplierid() {
        return supplierid;
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
     * Sets supplierid.
     *
     * @param supplierid the supplierid
     */
    public void setSupplierid(@NonNull Integer supplierid) {
        this.supplierid = supplierid;
    }

    /**
     * Gets companyname.
     *
     * @return the companyname
     */
    public String getCompanyname() {
        return companyname;
    }

    /**
     * Sets companyname.
     *
     * @param companyname the companyname
     */
    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    /**
     * Gets contactname.
     *
     * @return the contactname
     */
    public String getContactname() {
        return contactname;
    }

    /**
     * Sets contactname.
     *
     * @param contactname the contactname
     */
    public void setContactname(String contactname) {
        this.contactname = contactname;
    }

    /**
     * Gets identificationnumber.
     *
     * @return the identificationnumber
     */
    public String getIdentificationnumber() {
        return identificationnumber;
    }

    /**
     * Sets identificationnumber.
     *
     * @param identificationnumber the identificationnumber
     */
    public void setIdentificationnumber(String identificationnumber) {
        this.identificationnumber = identificationnumber;
    }

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
     * Gets town.
     *
     * @return the town
     */
    public String getTown() {
        return town;
    }

    /**
     * Sets town.
     *
     * @param town the town
     */
    public void setTown(String town) {
        this.town = town;
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
     * Gets othernumber.
     *
     * @return the othernumber
     */
    public String getOthernumber() {
        return othernumber;
    }

    /**
     * Sets othernumber.
     *
     * @param othernumber the othernumber
     */
    public void setOthernumber(String othernumber) {
        this.othernumber = othernumber;
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
     * Gets otheremail.
     *
     * @return the otheremail
     */
    public String getOtheremail() {
        return otheremail;
    }

    /**
     * Sets otheremail.
     *
     * @param otheremail the otheremail
     */
    public void setOtheremail(String otheremail) {
        this.otheremail = otheremail;
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

    /**
     * Gets addedby.
     *
     * @return the addedby
     */
    public Integer getAddedby() {
        return addedby;
    }

    /**
     * Sets addedby.
     *
     * @param addedby the addedby
     */
    public void setAddedby(Integer addedby) {
        this.addedby = addedby;
    }

    /**
     * Gets addedon.
     *
     * @return the addedon
     */
    public Integer getAddedon() {
        return addedon;
    }

    /**
     * Sets addedon.
     *
     * @param addedon the addedon
     */
    public void setAddedon(Integer addedon) {
        this.addedon = addedon;
    }



}
