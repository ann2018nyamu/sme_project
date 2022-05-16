package ke.co.eclectic.quickstore.rest.response;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import ke.co.eclectic.quickstore.models.Creditor;
import ke.co.eclectic.quickstore.models.Creditor;

/**
 * The type Business response.
 */
public class CreditorResponse extends  BaseResponse {
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
     * Parses single creditor Obj.
     *
     * @return the creditor
     */
    public Creditor getCreditor() {
        return  new Gson().fromJson(data, Creditor.class);
    }
    /**
     * Parses creditor lists data.
     *
     * @return the creditor list data
     */
    public List<Creditor> getCreditorList() {

        return  new Gson().fromJson(data.getAsJsonObject().get("creditors"),new TypeToken<List<Creditor>>(){}.getType());

    }






}
