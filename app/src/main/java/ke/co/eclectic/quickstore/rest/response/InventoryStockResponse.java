package ke.co.eclectic.quickstore.rest.response;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import ke.co.eclectic.quickstore.models.InventoryStock;

/**
 * The type Business response.
 */
public class InventoryStockResponse extends  BaseResponse {
    @SerializedName("data")
    @Expose
    private JsonElement data = null;

    /**
     *
     *
     * @return the json element data
     */
    public JsonElement getData() {
        return data;
    }

    public void setData(JsonElement data) {
        this.data = data;
    }

    /**
     * Parses single supplier Obj.
     *
     * @return the supplier
     */
    public InventoryStock getSupplier() {
        return  new Gson().fromJson(data,InventoryStock.class);
    }
    /**
     * Parses supplier lists data.
     *
     * @return the supplier list data
     */
    public List<InventoryStock> getInventoryStockList() {
        return  new Gson().fromJson(data.getAsJsonObject().get("stock"),new TypeToken<List<InventoryStock>>(){}.getType());
    }

}
