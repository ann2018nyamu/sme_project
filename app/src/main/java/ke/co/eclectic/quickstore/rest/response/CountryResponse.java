package ke.co.eclectic.quickstore.rest.response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import ke.co.eclectic.quickstore.models.Country;

/**
 * The type Country response.
 */
public class CountryResponse extends  BaseResponse {
    @SerializedName("data")
    @Expose
    private JsonObject data;

    /**
     * Gets data.
     *
     * @return the data
     */
    public JsonObject getData() {
        return data;
    }

    /**
     * Get data array list list.
     *
     * @return the list
     */
    public List<Country> getDataArrayList(){

        return new Gson().fromJson(data.get("countries").toString(), new TypeToken<List<Country>>(){}.getType());

    }

//    @SerializedName("data")
//    @Expose
//    private List<Country> data = null;
//
//    public List<Country> getData() {
//        return data;
//    }

}
