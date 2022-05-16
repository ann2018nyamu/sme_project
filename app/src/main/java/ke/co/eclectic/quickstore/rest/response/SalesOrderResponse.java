package ke.co.eclectic.quickstore.rest.response;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import ke.co.eclectic.quickstore.models.SalesOrder;

/**
 * The type Business response.
 */
public class SalesOrderResponse extends  BaseResponse {
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
     * Parses single salesOrder Obj.
     *
     * @return the salesOrder
     */
    public SalesOrder getSalesOrder() {
        SalesOrder salesOrder =  new Gson().fromJson(data.getAsJsonObject().get("sales_order"),SalesOrder.class);
        salesOrder.setProductListStr(new Gson().toJson(salesOrder.getSalesorder_items()));
        return  salesOrder;
    }
    public Integer getPosOrderId(){
        return  data.getAsJsonObject().get("Invoice ID").getAsInt();
    }

    /**
     * Parses salesOrder lists data.
     *
     * @return the salesOrder list data
     */
    public List<SalesOrder> getSalesOrderList() {
        return  new Gson().fromJson(data.getAsJsonObject().get("sales_orders"),new TypeToken<List<SalesOrder>>(){}.getType());
    }



}
