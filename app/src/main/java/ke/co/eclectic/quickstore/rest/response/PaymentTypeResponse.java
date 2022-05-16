package ke.co.eclectic.quickstore.rest.response;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import ke.co.eclectic.quickstore.models.PaymentType;

/**
 * The type Business response.
 */
public class PaymentTypeResponse extends  BaseResponse {
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
    public PaymentType getPaymentType() {
        return  new Gson().fromJson(data,PaymentType.class);
    }
    /**
     * Gets units lists data.
     *
     * @return the units list data
     */
    public List<PaymentType> getPaymentTypeList() {
        return  new Gson().fromJson(data.getAsJsonObject().get("types"),new TypeToken<List<PaymentType>>(){}.getType());
    }






}
