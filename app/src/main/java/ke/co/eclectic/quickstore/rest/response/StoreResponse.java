package ke.co.eclectic.quickstore.rest.response;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import ke.co.eclectic.quickstore.models.Store;

/**
 * The type Store response.
 */
public class StoreResponse extends  BaseResponse {
    @SerializedName("data")
    @Expose
    private JsonElement data;

    /**
     * Gets data.
     *
     * @return the data
     */
    public  ArrayList<Store> getData() {
        return  new Gson().fromJson(data.getAsJsonObject().get("stores"),new TypeToken<List<Store>>(){}.getType());
    }

    /**
     * Parses supplier lists data.
     *
     * @return the supplier list data
     */
    public List<Store> getStoreList() {
        return  new Gson().fromJson(data.getAsJsonObject().get("stores"),new TypeToken<List<Store>>(){}.getType());
    }

    /**
     * Gets single data.
     *
     * @return the single data
     */
    public  Store getSingleData() {
        return  new Gson().fromJson(data,Store.class);
    }

}
