package ke.co.eclectic.quickstore.rest;




import com.google.gson.JsonElement;

import java.util.HashMap;

import io.reactivex.Completable;
import io.reactivex.Single;
import ke.co.eclectic.quickstore.models.User;
import ke.co.eclectic.quickstore.rest.response.BusinessResponse;
import ke.co.eclectic.quickstore.rest.response.BusinessRolesResponse;
import ke.co.eclectic.quickstore.rest.response.CategoryResponse;
import ke.co.eclectic.quickstore.rest.response.CountryResponse;
import ke.co.eclectic.quickstore.rest.response.CreditorResponse;
import ke.co.eclectic.quickstore.rest.response.CustomerResponse;
import ke.co.eclectic.quickstore.rest.response.InventoryStockResponse;
import ke.co.eclectic.quickstore.rest.response.OfferTypeResponse;
import ke.co.eclectic.quickstore.rest.response.PaymentTypeResponse;
import ke.co.eclectic.quickstore.rest.response.ProductResponse;
import ke.co.eclectic.quickstore.rest.response.PurchaseOrderResponse;
import ke.co.eclectic.quickstore.rest.response.SalesOrderResponse;
import ke.co.eclectic.quickstore.rest.response.StoreResponse;
import ke.co.eclectic.quickstore.rest.response.StoreTypeResponse;
import ke.co.eclectic.quickstore.rest.response.SupplierResponse;
import ke.co.eclectic.quickstore.rest.response.UnitResponse;
import ke.co.eclectic.quickstore.rest.response.UserResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by David Manduku on 08/10/2018.
 */
public interface ApiInterface {
    @POST("api")
    Single<CustomerResponse> customerApi(@Body JsonElement jsonElement);

    /**
     * Gets payment types data.
     *
     * @return the payment  type data
     */
    @GET("api/getpaymenttypes")
    Single<PaymentTypeResponse> getPaymentTypes();
    /**
     * Gets country data.
     *
     * @return the country data
     */
    @GET("api/countries")
    Single<CountryResponse> getCountryData();

    /**
     * Gets store type data.
     *
     * @return the store type data
     */
    @GET("api/storetypes")
    Single<StoreTypeResponse> getStoreTypeData();
    /**
     * Gets sales offer type data.
     *
     * @return the store type data
     */
    @GET("api/offertypes")
    Single<OfferTypeResponse> getOfferTypeData();


    /**
     * Api single.
     *
     * @param user the user
     * @return the single
     */
    @POST("api")
    Single<UserResponse> api(@Body User user);

    /**
     * Api single.
     *
     * @param jsonElement as the request body
     * @return the single
     */
    @POST("api")
    Single<UserResponse> api(@Body JsonElement jsonElement);

    /**
     * Business api single.
     *
     * @param jsonElement as the request body
     * @return the single
     */
    @POST("api")
    Single<BusinessResponse> businessApi(@Body JsonElement jsonElement);

    /**
     * Store api single.
     *
     * @param jsonElement as the request body
     * @return the store objects
     */
    @POST("api")
    Single<StoreResponse> storeApi(@Body JsonElement jsonElement);

    /**
     * Gets business roles.
     *
     * @param jsonElement as the request body
     * @return the business roles
     */
    @POST("api")
    Single<BusinessRolesResponse> getBusinessRoles(@Body JsonElement jsonElement);
    /**
     * Gets Category .
     *
     * @param jsonElement as the request body
     * @return the cateogry object/s list/s
     */
    @POST("api")
    Single<CategoryResponse> categoryApi(@Body JsonElement jsonElement);
    /**
     * put/get unit requests .
     *
     * @param jsonElement as the request body
     * @return the unit object/s list/s
     */
    @POST("api")
    Single<UnitResponse> unitApi(@Body JsonElement jsonElement);

    /**
     * put/get product requests .
     *
     * @param jsonElement as the request body
     * @return the product object/s list/s
     */
    @POST("api")
    Single<ProductResponse> productApi(@Body JsonElement jsonElement);

    @POST("api")
    Single<SupplierResponse> supplierApi(@Body JsonElement jsonElement);



    @POST("api")
    Single<PurchaseOrderResponse> purchaseOrderApi(@Body JsonElement jsonElement);

    @POST("api")
    Single<InventoryStockResponse> inventoryStockApi(@Body JsonElement jsonElement);

    @POST("api")
    Single<SalesOrderResponse> salesOrderApi(@Body JsonElement jsonElement);
    @POST("api")
    Single<CreditorResponse> creditorApi(@Body JsonElement jsonElement);


    /**
     * Notify call.
     *
     * @param data the data
     * @return the call
     */
//sending notification
    @FormUrlEncoded
    @POST("notify")
    Call<UserResponse> notify(@FieldMap HashMap<String, String> data);

    /**
     * Send error single.
     *
     * @param data the data
     * @return the single
     */
    @FormUrlEncoded
    @POST("sendError")
    Single<UserResponse> sendError(@FieldMap HashMap<String, Object> data);


}
