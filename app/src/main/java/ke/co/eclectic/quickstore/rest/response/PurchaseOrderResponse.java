package ke.co.eclectic.quickstore.rest.response;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import ke.co.eclectic.quickstore.models.PurchaseOrder;
import timber.log.Timber;

/**
 * The type Business response.
 */
public class PurchaseOrderResponse extends  BaseResponse {
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
     * Parses single purchaseOrder Obj.
     *
     * @return the purchaseOrder
     */
    public PurchaseOrder getPurchaseOrder() {
        PurchaseOrder purchaseOrder =  new Gson().fromJson(data.getAsJsonObject().get("purchase_order"),PurchaseOrder.class);
        //purchaseOrder.setPurchase_items(new Gson().fromJson(data.getAsJsonObject().get("purchase order").getAsJsonObject().get("purchase_items"),new TypeToken<List<PurchaseItems>>(){}.getType()));
        try {
            purchaseOrder.setProductListStr(new Gson().toJson(purchaseOrder.getPurchase_items()));
        }catch(Exception e){
            Timber.v(e.getMessage());
        }

        return  purchaseOrder;
    }
    /**
     * Parses purchaseOrder lists data.
     *
     * @return the purchaseOrder list data
     */
    public List<PurchaseOrder> getPurchaseOrderList() {
        return  new Gson().fromJson(data.getAsJsonObject().get("purchase_orders"),new TypeToken<List<PurchaseOrder>>(){}.getType());
    }

}
