package ke.co.eclectic.quickstore.models;


import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ke.co.eclectic.quickstore.activities.auth.AuthActivity;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.viewModel.UserViewModel;
import timber.log.Timber;

/**
 * Created by David Manduku on 08/10/2018.
 */
@Entity(tableName = "users",indices = {@Index(value = {"userid","email", "nationalidnumber","phonenumber"}, unique = true)})
public class User {


    @PrimaryKey
    @NonNull
    @SerializedName("userid")
    private Integer userid=0;
    private String phonenumber="";
    private String firstname="";
    private String othernames="";
    private String lastname="";
    private String middlename="";
    private String dateofbirth="";
    private String gender="";
    private String nationality="";
    private String nationalidnumber="";
    private String email="";
    private String userimg="";
    private String jsonData="";
    private String isMerchant="";//doesnt exist in live db
    private String countrycode="";//doesnt exist in live db

    private String auth_token="";
    private String password="";
    private String rolename="";

    private Integer storeuserid=0;
    private String storename="";
    private Integer storeid=0;

    private Integer businessid=0;
    private String businessname="";

    @Expose(serialize = false)
    public double uCreatedTime;
    //doesn't exist in live db

    @Ignore
    private String request_type="";
    @Expose(serialize = false)
    private int userType;

    private String rolesStr="";

    //used in getting staff rules
    @Ignore
    private JsonElement rules;

    @Ignore
    private JsonElement rolesAr;


    public JsonElement getRules() {
        return rules;
    }

    public void setRules(JsonElement rules) {
        this.rules = rules;
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
     * Gets businessid.
     *
     * @param context the context
     * @return the businessid
     */
    public Integer getBusinessid(Context context) {
        if(businessid == 0){
            ArrayList<Roles>  rolesL = getRoles();
            if(rolesL.size() >0){
                businessid = rolesL.get(0).getBusinessid();
                businessname = rolesL.get(0).getBusinessname();
                setDefaultStore();
            }else {
                logoutuser(context);
            }
        }
        return businessid;
    }

    /**
     * Sets businessid.
     *
     * @param businessid the businessid
     */
    public void setBusinessid(Integer businessid) {
        this.businessid = businessid;
        //setting default store info if the user has one store assigned to him
        setDefaultStore();
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
     * Gets business h.
     *
     * @param context activity reference
     * @return business in hasmap
     */
    public HashMap<String,Business> getBusinessH(Context context) {
        HashMap<String,Business>  bsHash = new HashMap<>();

            ArrayList<Roles>  rolesL = getRoles();
            if(rolesL.size() >0){
                Business  business;
                for(Roles r:rolesL){
                        business = new Business();
                        business.setBusinessid(r.getBusinessid());
                        business.setbName(r.getBusinessname());
                        bsHash.put(r.getBusinessname(),business);
                }

            }else {
                logoutuser(context);
            }

        return bsHash;
    }

    /**
     * Gets business stores h.
     *
     * @return the business stores h
     */
    public HashMap<String,Store> getBusinessStoresH() {
        HashMap<String,Store>  bsHash = new HashMap<>();
            ArrayList<Roles>  rolesL = getRoles();
            if(rolesL.size() >0){
                Store  store;
                for(Roles r:rolesL){
                    if(businessid.intValue() == r.getBusinessid().intValue()){
                        Timber.v(r.getStorename() +" // "+businessid.intValue());
                        store = new Store();
                        store.setStoreid(r.getStoreid());
                        store.setStorename(r.getStorename());
                        store.setBusinessname(r.getBusinessname());
                        store.setBusinessid(r.getBusinessid());
                        store.setStoreuserid(r.getStoreuserid());
                        store.setRolename(r.getRolename());
                        bsHash.put(r.getStorename(),store);
                    }
                }
            }

        return bsHash;
    }

    /**
     * This method is to filter out accessibility of the application feature
     * to the current user
     *
     *
     * @param feature features(pos,inventory product) as defined by the backend
     * @param action features(cancreate,candelete,canview) as defined by the backend
     * @return true if user has been given access to the feature
     */
    public boolean getCurrentBsRule(String feature,String action){
        HashMap<String, JsonObject>  bsHash = new HashMap<>();
        ArrayList<Roles>  rolesL = getRoles();
        Business   business;
        for(Roles r:rolesL){
            business = new Business();
            business.setBusinessid(r.getBusinessid());
            business.setbName(r.getBusinessname());
            bsHash.put(r.getBusinessname(),r.getRules());
        }
        try {
            if (bsHash.get(businessname).get(feature).toString().contains(action)) {
                return true;
            }
        }catch (Exception e){
            Timber.v("".concat(e.getMessage()));
        }


        return false;
    }


    /**
     * Get current business rule in json object.
     *
     * @return the json object
     */
    public Roles getCurrentBsRole(){
        ArrayList<Roles>  rolesL = getRoles();
        Timber.v(getBusinessid().toString());
        Roles roles = new Roles();
        for(Roles r:rolesL){
            Timber.v("getbusinessid "+getBusinessid().toString()+" "+ r.getBusinessid().toString());
            if(getBusinessid().doubleValue() == r.getBusinessid().doubleValue()){
                Timber.v(r.getRules().toString());
                roles = r;
            }
        }

        //when accessing staff rules due to api json structure change
        if(roles.getRules() == null ){
            roles.setRules(getRules().getAsJsonObject());
        }
       return roles;
    }


    private void setDefaultStore(){
        HashMap<String,Store>  bsHash = getBusinessStoresH();
        if(bsHash.size() == 1){
            for(Map.Entry<String,Store> entry: bsHash.entrySet()){
                storeid = entry.getValue().getStoreid();
                storename = entry.getKey();
                storeuserid = entry.getValue().getStoreuserid();
                setRolename(entry.getValue().getRolename());
            }
        }
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
     * Gets storeid.
     *
     * @param context the context
     * @return the storeid
     */
    public Integer getStoreid(Context context) {
        if(storeid == 0){
            ArrayList<Roles>  rolesL = getRoles();
            if(rolesL.size() >0){
                storeid = rolesL.get(0).getStoreid();
            }else {
                logoutuser(context);
            }
        }
        return storeid;
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
     * Save user info.
     *
     * @param user     the user
     * @param activity the activity
     */
    public void  saveUserInfo(User user,FragmentActivity activity){
        UserViewModel userViewModel =  ViewModelProviders.of(activity).get(UserViewModel.class);
        userViewModel.insertToDb(user);
    }

    /**
     * Refresh user info.
     *
     * @param activity the activity
     */
    public void  refreshUserInfo(FragmentActivity activity){
        UserViewModel userViewModel =  ViewModelProviders.of(activity).get(UserViewModel.class);
        userViewModel.refreshUserDetails();
    }

    private void logoutuser(Context context){
        HashMap<String,String> data= new HashMap<>();
        data.put("action","deleteuser");
        GlobalMethod.goToActivity(context,AuthActivity.class,data);
        CodingMsg.tl(context,"User data expired");
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
     * Gets othernames.
     *
     * @return the othernames
     */
    public String getOthernames() {
        return othernames;
    }

    /**
     * Sets othernames.
     *
     * @param othernames the othernames
     */
    public void setOthernames(String othernames) {
        this.othernames = othernames;
    }

    /**
     * Sets roles ar.
     *
     * @param rolesAr the roles ar
     */
    public void setRolesAr(JsonElement rolesAr) {
        this.rolesAr = rolesAr;
    }

    /**
     * Gets roles str.
     *
     * @return the roles str
     */
    public String getRolesStr() {
        return rolesStr;
    }


    /**
     * Sets roles str.
     *
     * @param rolesStr the roles str
     */
    public void setRolesStr(String rolesStr) {
        this.rolesStr = rolesStr;
    }

    /**
     * Sets userid.
     *
     * @param userid the userid
     */
    public void setUserid(@NonNull Integer userid) {
        this.userid = userid;
    }

    /**
     * Gets roles.
     *
     * @return the roles
     */
    public ArrayList<Roles> getRoles() {
        ArrayList<Roles> rolesList =  new ArrayList<>();
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonParser jsonParser = new JsonParser();
        if(rolesAr == null){
            rolesList = gson.fromJson(rolesStr, new TypeToken<List<Roles>>(){}.getType());
        }else{
            rolesList = gson.fromJson(jsonParser.parse(rolesAr.toString()), new TypeToken<List<Roles>>(){}.getType());
        }


        if(rolesList == null){
            rolesList =  new ArrayList<>();
        }

        return rolesList;
    }


    /**
     * Gets auth token.
     *
     * @return the auth token
     */
    public String getAuth_token() {
        return auth_token;
    }

    /**
     * Sets auth token.
     *
     * @param auth_token the auth token
     */
    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
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
     * Gets userimg.
     *
     * @return the userimg
     */
    public String getUserimg() {
        return userimg;
    }

    /**
     * Sets userimg.
     *
     * @param userimg the userimg
     */
    public void setUserimg(String userimg) {
        this.userimg = userimg;
    }

    /**
     * Gets request type.
     *
     * @return the request type
     */
    public String getRequest_type() {
        return request_type;
    }

    /**
     * Sets request type.
     *
     * @param request_type the request type
     */
    public void setRequest_type(String request_type) {
        this.request_type = request_type;
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
     * Gets firstname.
     *
     * @return the firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Sets firstname.
     *
     * @param firstname the firstname
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * Gets lastname.
     *
     * @return the lastname
     */
    public String getLastname() {
        String names[] = othernames.split(" ");
        try {
            if (!names[1].contentEquals("")) {
                return names[1].trim();
            }
        }catch (Exception e){
           Timber.v(e.getMessage());
        }
        return lastname;
    }

    /**
     * Sets lastname.
     *
     * @param lastname the lastname
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * Gets middlename.
     *
     * @return the middlename
     */
    public String getMiddlename() {
       String names[] = othernames.split(" ");
       if(!names[0].contentEquals("")){
           return names[0].trim();
       }

        return middlename;
    }

    /**
     * Sets middlename.
     *
     * @param middlename the middlename
     */
    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    /**
     * Gets dateofbirth.
     *
     * @return the dateofbirth
     */
    public String getDateofbirth() {
        return dateofbirth;
    }

    /**
     * Sets dateofbirth.
     *
     * @param dateofbirth the dateofbirth
     */
    public void setDateofbirth(String dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    /**
     * Gets nationalidnumber.
     *
     * @return the nationalidnumber
     */
    public String getNationalidnumber() {
        return nationalidnumber;
    }

    /**
     * Sets nationalidnumber.
     *
     * @param nationalidnumber the nationalidnumber
     */
    public void setNationalidnumber(String nationalidnumber) {
        this.nationalidnumber = nationalidnumber;
    }

    /**
     * Gets created time.
     *
     * @return the created time
     */
    public double getuCreatedTime() {
        return uCreatedTime;
    }

    /**
     * Sets created time.
     *
     * @param uCreatedTime the u created time
     */
    public void setuCreatedTime(double uCreatedTime) {
        this.uCreatedTime = uCreatedTime;
    }


    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets userid.
     *
     * @return the userid
     */
    @NonNull
    public Integer getUserid() {
        return userid;
    }

    /**
     * Gets is merchant.
     *
     * @return the is merchant
     */
    public String getIsMerchant() {
        return isMerchant;
    }

    /**
     * Sets is merchant.
     *
     * @param isMerchant the is merchant
     */
    public void setIsMerchant(String isMerchant) {
        this.isMerchant = isMerchant;
    }


    /**
     * Gets gender.
     *
     * @return the gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets gender.
     *
     * @param gender the gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Gets nationality.
     *
     * @return the nationality
     */
    public String getNationality() {
        return nationality;
    }

    /**
     * Sets nationality.
     *
     * @param nationality the nationality
     */
    public void setNationality(String nationality) {
        this.nationality = nationality;
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
     * Gets user type.
     *
     * @return the user type
     */
    public int getUserType() {
        return userType;
    }

    /**
     * Sets user type.
     *
     * @param userType the user type
     */
    public void setUserType(int userType) {
        this.userType = userType;
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
     * Gets storename.
     *
     * @param context the context
     * @return the storename
     */
    public String getStorename(Context context) {
        if(storename.contentEquals("")){
            ArrayList<Roles>  rolesL = getRoles();
            if(rolesL.size() >0){
                storename = rolesL.get(0).getStorename();
            }else {
                logoutuser(context);
            }
        }

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
}
