package ke.co.eclectic.quickstore.rest.response;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import ke.co.eclectic.quickstore.models.Customer;

/**
 * The type Business response.
 */
public class CustomerResponse extends  BaseResponse {
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
     * Parses single customer Obj.
     *
     * @return the customer
     */
    public Customer getCustomer() {
        return  new Gson().fromJson(data,Customer.class);
    }
    /**
     * Parses customer lists data.
     *
     * @return the customer list data
     */
    public List<Customer> getCustomerList() {
        return  new Gson().fromJson(data.getAsJsonObject().get("customers"),new TypeToken<List<Customer>>(){}.getType());
    }






}
