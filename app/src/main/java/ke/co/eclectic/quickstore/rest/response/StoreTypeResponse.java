package ke.co.eclectic.quickstore.rest.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ke.co.eclectic.quickstore.models.StoreType;

/**
 * The type Store type response.
 */
public class StoreTypeResponse extends  BaseResponse {
    @SerializedName("data")
    @Expose
    private List<StoreType> data;

    /**
     * Gets data.
     *
     * @return the data
     */
    public  List<StoreType> getData() {
        return data;
    }


}
