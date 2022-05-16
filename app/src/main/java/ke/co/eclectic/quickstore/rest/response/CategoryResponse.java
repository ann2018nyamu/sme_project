package ke.co.eclectic.quickstore.rest.response;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import ke.co.eclectic.quickstore.models.Category;

/**
 * The type Business response.
 */
public class CategoryResponse extends  BaseResponse {
    @SerializedName("data")
    @Expose
    private JsonElement data;


    /**
     * Gets single category item.
     *
     * @return the data
     */
    public Category getData() {
        return new Gson().fromJson(data,Category.class);
    }
    /**
     * Gets data.
     *
     * @return the data
     */
    public ArrayList<Category> getCategoryList() {
        return new Gson().fromJson(data.getAsJsonObject().get("categories"),new TypeToken<List<Category>>(){}.getType());
    }

}
