package ke.co.eclectic.quickstore.rest.response;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import ke.co.eclectic.quickstore.models.User;


/**
 * The  User object response from the server.
 */
public class UserResponse  extends  BaseResponse{
    @SerializedName("data")
    @Expose
    private JsonElement data = null;
    private User user = new User();

    /**
     * Gets list of users.
     *
     * @return the list of users
     */
    public JsonElement getData() {
        return data;
    }

    /**
     * it prepares users object by parsing the json response
     *
     * @return user object
     */
    public User getUser() {
      //  user = new Gson().fromJson( data.getAsJsonObject().get("user"),User.class);
        user.setFirstname( data.getAsJsonObject().get("user").getAsJsonObject().get("firstname").getAsString());
        user.setPhonenumber( data.getAsJsonObject().get("user").getAsJsonObject().get("phonenumber").getAsString());
        user.setEmail( data.getAsJsonObject().get("user").getAsJsonObject().get("email").getAsString());
        user.setUserid( data.getAsJsonObject().get("user").getAsJsonObject().get("userid").getAsInt());
        user.setDateofbirth( data.getAsJsonObject().get("user").getAsJsonObject().get("dateofbirth").getAsString());
        user.setGender( data.getAsJsonObject().get("user").getAsJsonObject().get("gender").getAsString());
        user.setNationality( data.getAsJsonObject().get("user").getAsJsonObject().get("nationality").getAsString());
        user.setNationalidnumber( data.getAsJsonObject().get("user").getAsJsonObject().get("nationalidnumber").getAsString());

        user.setRolesAr( data.getAsJsonObject().get("user").getAsJsonObject().get("roles"));
        user.setRolesStr( data.getAsJsonObject().get("user").getAsJsonObject().get("roles").toString());

        user.setAuth_token(data.getAsJsonObject().get("auth_token").toString());
        return user;
    }

    /**
     * it prepares users object by parsing the json response
     *
     * @return user object
     */
    public User getRefreshedUserData() {
        user.setRolesAr( data.getAsJsonObject().get("user").getAsJsonObject().get("roles"));
        user.setRolesStr( data.getAsJsonObject().get("user").getAsJsonObject().get("roles").toString());

        return user;
    }

    /**
     * Gets staff list data.
     *
     * @return the staff list data
     */
    public  ArrayList<User> getStaffListData() {
        return  new Gson().fromJson(data.getAsJsonObject().get("staff"),new TypeToken<List<User>>(){}.getType());
    }
    /**
     * Gets staff list data.
     *
     * @return the staff list data
     */
    public User getStaffData() {
        User user = new Gson().fromJson(data.getAsJsonObject().get("storestaff"),User.class);

        return  user;
    }
}