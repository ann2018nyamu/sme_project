package ke.co.eclectic.quickstore.rest.response;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import ke.co.eclectic.quickstore.models.Business;
import ke.co.eclectic.quickstore.models.BusinessRoles;
import ke.co.eclectic.quickstore.models.Category;

/**
 * The type Business response.
 */
public class BusinessResponse extends  BaseResponse {
    @SerializedName("data")
    @Expose
    private JsonElement data;

    /**
     * Gets data.
     *
     * @return the data
     */
    public Business getData() {
        return new Gson().fromJson(data, Business.class);
    }

    /**
     * Gets data.
     *
     * @return the data
     */
    public ArrayList<BusinessRoles> getBusinessRole() {
        return  new Gson().fromJson(data.getAsJsonObject().get("roles"),new TypeToken<List<BusinessRoles>>(){}.getType());
    }

}
