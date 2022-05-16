package ke.co.eclectic.quickstore.rest.response;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import ke.co.eclectic.quickstore.models.BusinessRoles;

/**
 * The type Business roles response.
 */
public class BusinessRolesResponse extends  BaseResponse {
    @SerializedName("data")
    @Expose
    private JsonElement data;

    /**
     * Gets data.
     *
     * @return the data
     */
    public  ArrayList<BusinessRoles> getData() {
        return  new Gson().fromJson(data.getAsJsonObject().get("roles"),new TypeToken<List<BusinessRoles>>(){}.getType());
    }

    /**
     * Gets single data.
     *
     * @return the single data
     */
    public  BusinessRoles getSingleData() {
        return  new Gson().fromJson(data,BusinessRoles.class);
    }
}
