package ke.co.eclectic.quickstore.rest.response;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import ke.co.eclectic.quickstore.models.OfferType;

/**
 * The type Business response.
 */
public class OfferTypeResponse extends  BaseResponse {
    @SerializedName("data")
    @Expose
    private JsonElement data = null;

    /**
     * Gets data.
     *
     * @return the data
     */
    public JsonElement getData() {
        return data;
    }
    /**
     * Gets single unit.
     *
     * @return the data
     */
    public OfferType getUnit() {
        return  new Gson().fromJson(data,OfferType.class);
    }
    /**
     * Gets units lists data.
     *
     * @return the units list data
     */
    public List<OfferType> getOfferTypeList() {
        return  new Gson().fromJson(data,new TypeToken<List<OfferType>>(){}.getType());
    }

}
