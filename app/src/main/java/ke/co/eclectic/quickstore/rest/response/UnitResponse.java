package ke.co.eclectic.quickstore.rest.response;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import ke.co.eclectic.quickstore.models.Unit;

/**
 * The type Business response.
 */
public class UnitResponse extends  BaseResponse {
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
    public Unit getUnit() {
        return  new Gson().fromJson(data,Unit.class);
    }
    /**
     * Gets units lists data.
     *
     * @return the units list data
     */
    public List<Unit> getUnitList() {
        return  new Gson().fromJson(data.getAsJsonObject().get("units"),new TypeToken<List<Unit>>(){}.getType());
    }






}
