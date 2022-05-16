package ke.co.eclectic.quickstore.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.dao.QuickStoreRoomDb;
import ke.co.eclectic.quickstore.dao.PurchaseOrderDao;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.inc.AppConfig;
import ke.co.eclectic.quickstore.models.PurchaseOrder;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.rest.ApiClient;
import ke.co.eclectic.quickstore.rest.response.PurchaseOrderResponse;
import ke.co.eclectic.quickstore.rest.response.SalesOrderResponse;
import timber.log.Timber;

/**
 * STEP 4
 * A Repository manages query threads and allows you to use multiple backends.
 * the Repository implements the logic for deciding whether to fetch data from a
 * network or use results cached in a local database.
 */
public class PurchaseOrderRepository {
    private PurchaseOrderDao mPurchaseOrderDao;
    private LiveData<List<PurchaseOrder>> mAllPurchaseOrder;
    private MutableLiveData<MyApiResponses> myApiResponses = new MutableLiveData<MyApiResponses>();
    private CompositeDisposable disposable = new CompositeDisposable();
    /**
     * gets a handle to the database and initializes the member variables
     *
     * @param application the application
     */
    public PurchaseOrderRepository(Application application) {
        QuickStoreRoomDb db = QuickStoreRoomDb.getDatabase(application);
        mPurchaseOrderDao = db.purchaseOrderDao();
        mAllPurchaseOrder = mPurchaseOrderDao.getAllPurchaseOrders();
    }

    /**
     * Deletes all purchaseOrder form database
     */
    public void deleteAll(){
        deleteFromDb(new PurchaseOrder());
    }

    /**
     * Deletes all purchaseOrder attached to  unique business id from  database
     *
     * @param purchaseOrder the purchaseOrder
     */
    public void deletePurchaseOrder(PurchaseOrder purchaseOrder){
        deleteApiPurchaseOrder(purchaseOrder);
    }

    /**
     * Delete from db.
     *
     * @param purchaseOrder the purchase order
     */
    public void deleteFromDb(PurchaseOrder purchaseOrder){
            disposable.add(Completable.fromAction(()->{
                if(purchaseOrder.getPurchaseorderid() == 0){
                    mPurchaseOrderDao.deleteAll();
                }else{
                    mPurchaseOrderDao.deleteSinglePurchaseOrder(purchaseOrder.getPurchaseorderid());
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() {
                            myApiResponses.postValue(new MyApiResponses("dbdelete","success",new JsonObject()));
                        }

                        @Override
                        public void onError(Throwable e) {
                            myApiResponses.postValue(new MyApiResponses("dbdelete","error",e));
                        }
                    }));
        }
    

    /**
     * Gets all purchaseOrders.
     *
     * @return all purchaseOrders
     */
    public LiveData<List<PurchaseOrder>> getAllPurchaseOrders(HashMap<String,Object> data) {
        refreshPurchaseOrders(data);
        return mAllPurchaseOrder;
    }

    /**
     * Gets all repository network reesponse.
     *
     * @return all purchaseOrders
     */
    public MutableLiveData<MyApiResponses> getApiResponse() {
        return myApiResponses;
    }

    /**
     * Get single purchaseOrder live data.
     *
     * @param purchaseOrder details/information
     * @return a single purchaseOrder instance/record
     */
    public LiveData<List<PurchaseOrder>>  getSinglePurchaseOrder(PurchaseOrder purchaseOrder){
            getPurchaseOrdersItems(purchaseOrder.getPurchaseorderid());
            return  mPurchaseOrderDao.getSinglePurchaseOrder(purchaseOrder.getPurchaseorderid());
    }


    /**
     * saves purchase order record to db/api.
     *
     * @param purchaseOrder inserts purchaseOrder detail inn the database
     */
    public void insert (PurchaseOrder purchaseOrder,String requsetType) {
        savePurchaseOrder(purchaseOrder,requsetType);
    }


    public void insertToDb(PurchaseOrder purchaseOrder){
        disposable.add(Completable.fromAction(()->{
            mPurchaseOrderDao.insert(purchaseOrder) ;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        myApiResponses.postValue(new MyApiResponses("dbsave","success",new JsonObject()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        myApiResponses.postValue(new MyApiResponses("dbsave","fail",e));
                    }
                }));
    }


    /**
     * @param productE product json element
     * @param purchaseorderid unique id of the purchase order to update
     */
    public void updateItemsToDb(Integer purchaseorderid, String productE){
        disposable.add(Completable.fromAction(()->{
            mPurchaseOrderDao.updateProductItems(purchaseorderid,productE);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        myApiResponses.postValue(new MyApiResponses("dbupdate","success",new JsonObject()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        myApiResponses.postValue(new MyApiResponses("dbupdate","fail",e));
                    }
                }));
    }

    /**
     * Send mpesa payment request.
     *
     * @param payData the pay data
     */
    public void sendPaymentRequest(HashMap<String, Object> payData) {
        //Todo move this code to backend otherwise it poses security breach
//        String paymentUrl = AppConfig.MPESA_URL;
//        if(((String)payData.get("payment_type")).toLowerCase().contains("mpesa")){
//            paymentUrl =  AppConfig.MPESA_URL;
//        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss",Locale.ENGLISH);

        HashMap<String, Object> data1 = new HashMap<>();
        data1.put("amount", payData.get("amount"));
        data1.put("username", "easy.rent");
        data1.put("password", "e794e56686bb0a92c1049191d2ddacd581f5ffe43ad0000a3e40429bf8c5fee35ba4389a553e3d884ce8cc3072d8d125f9774eb2a14b2bc50470ea3011c29724");
        data1.put("clientid", "5064");
        data1.put("accountno", payData.get("phone"));
        data1.put("msisdn", payData.get("phone"));
        data1.put("accountreference", "QuickStore");
        data1.put("narration", "QuickStore Test");
        data1.put("transactionid","QSB".concat(GlobalVariable.getCurrentUser().getBusinessid().toString()).concat("S").concat(GlobalVariable.getCurrentUser().getStoreid().toString()).concat(format.format(new Date())) );
        data1.put("serviceid", "5067");
        data1.put("shortcode", "174379");
        data1.put("type", "payment");

        // sending request to api
//        disposable.add(
//                ApiClient
//                        .getPaymentApi(paymentUrl)
//                        .salesOrderPaymentApi(new JsonParser().parse(new Gson().toJson(data1)))
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeWith(new DisposableSingleObserver<SalesOrderResponse>() {
//                            @Override
//                            public void onSuccess(SalesOrderResponse salesOrderResponse) {
//                                if (salesOrderResponse.getStatus().contentEquals("success")) {
//                                    Timber.v("salesOrderResponse");
//                                    salesOrderResponse.getData().getAsJsonObject().get("RESPONSECODE").toString();
//                                    myApiResponses.postValue(new MyApiResponses("apipayment","success",salesOrderResponse));
//                                    //GlobalMethod.goToActivity(AddSalesOrderActivity.this, MenuActivity.class);
//                                }
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                myApiResponses.postValue(new MyApiResponses("apidelete","success",e));
//                            }
//                        })
//        );


        HashMap<String,Object>  data = new HashMap<>();
        data.put("request_type","mpesastk");
        data.put("phonenumber", payData.get("phone"));
        data.put("amount", payData.get("amount"));
        //sending request to api
        disposable.add(
                ApiClient.getApi().purchaseOrderApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<PurchaseOrderResponse>(){
                            @Override
                            public void onSuccess(PurchaseOrderResponse purchaseOrderResponse) {
                                if(purchaseOrderResponse.getStatus().contentEquals("success")){
                                    insertToDb(purchaseOrderResponse.getPurchaseOrder());
                                    myApiResponses.postValue(new MyApiResponses("apipitems","success",purchaseOrderResponse));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apipitems","fail",e));
                            }

                        })
        );


    }

    /**
     * save purchase order data
     * @param purchaseOrder purchase order to save/update
     * @param requestType request type
     */
    private void savePurchaseOrder(PurchaseOrder purchaseOrder,String requestType){
        HashMap<String, Object> data = new HashMap<>();
        data.put("request_type", requestType);

        if(purchaseOrder.getPurchaseorderid() == 0){
            data.put("supplierid", purchaseOrder.getSupplierid());
            data.put("autoapproval", purchaseOrder.getAutoapporval());
            data.put("products", purchaseOrder.generatePOProduct());
            data.put("totalcost", purchaseOrder.getTotalcost());
            data.put("storeid", purchaseOrder.getStoreid());
        }else{
            data.put("purchaseorderid", purchaseOrder.getPurchaseorderid());

        }

        if(requestType.contentEquals("approvepurchase")){
            data.put("paymenttypeid", purchaseOrder.getPaymentType());
        }
        if(requestType.contentEquals("editpurchase")){
            data.put("supplierid", purchaseOrder.getSupplierid());
            data.put("totalcost", purchaseOrder.getTotalcost());
            data.put("products", purchaseOrder.generatePOProduct());
        }
        // sending request to api
        disposable.add(
                ApiClient.getApi().purchaseOrderApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<PurchaseOrderResponse>() {
                            @Override
                            public void onSuccess(PurchaseOrderResponse purchaseOrderResponse) {
                                if (purchaseOrderResponse.getStatus().contentEquals("success")) {
                                    Timber.v("purchaseOrderResponse");
                                    //myApiResponses.postValue(new MyApiResponses("apisave","success",purchaseOrderResponse.getData()));
                                    myApiResponses.postValue(new MyApiResponses("apisave","success",purchaseOrderResponse));
                                    try {
                                        if(requestType.contentEquals("approvepurchase")){
                                            purchaseOrder.setStatus("approved");
                                        }
                                        if(requestType.contentEquals("closepurchase")){
                                            purchaseOrder.setStatus("completed");
                                        }
                                        if(requestType.contentEquals("cancelpurchase")){
                                            purchaseOrder.setStatus("cancelled");
                                        }
                                        if (purchaseOrder.getPurchaseorderid() == 0) {
                                            purchaseOrder.setPurchaseorderid(purchaseOrderResponse.getPurchaseOrder().getPurchaseorderid());
                                        }
                                        insertToDb(purchaseOrder);
                                    }catch (Exception e){
                                        Timber.v(e.getMessage());
                                    }

                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apisave","fail",e));
                            }
                        })
        );
    }

    /**
     * fetches specific order id from api
     * @param purchaseorderid
     */
    private void getPurchaseOrdersItems(final Integer purchaseorderid) {
        HashMap<String,Object>  data = new HashMap<>();
        data.put("request_type","getpurchaseitems");
        data.put("purchaseorderid",purchaseorderid);
        //sending request to api
        disposable.add(
                ApiClient.getApi().purchaseOrderApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<PurchaseOrderResponse>(){
                            @Override
                            public void onSuccess(PurchaseOrderResponse purchaseOrderResponse) {
                                if(purchaseOrderResponse.getStatus().contentEquals("success")){
                                    insertToDb(purchaseOrderResponse.getPurchaseOrder());
                                    myApiResponses.postValue(new MyApiResponses("apipitems","success",purchaseOrderResponse));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apipitems","fail",e));
                            }

                        })
        );
    }

    /**
     * retrieves purchase order from api
     * @param data
     */
    private void refreshPurchaseOrders(HashMap<String,Object> data) {
        //sending request to api
        disposable.add(
                ApiClient.getApi().purchaseOrderApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<PurchaseOrderResponse>(){
                            @Override
                            public void onSuccess(PurchaseOrderResponse purchaseOrderResponse) {
                                if(purchaseOrderResponse.getStatus().contentEquals("success")){
                                    try {
                                        deleteAll();
                                        for (PurchaseOrder p : purchaseOrderResponse.getPurchaseOrderList()) {
                                            insertToDb(p);
                                        }
                                        myApiResponses.postValue(new MyApiResponses("apirefresh", "success", purchaseOrderResponse));
                                    }catch (Exception e){
                                        Timber.v(e.getMessage());
                                    }
                                }
                            }
                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apirefresh","fail",e));
                            }
                        })
        );
    }

    /**
     * deletes purchase order from api
     * @param purchaseOrder
     */
    private void deleteApiPurchaseOrder(PurchaseOrder purchaseOrder){
        HashMap<String, Object> data = new HashMap<>();
        data.put("request_type", "deletepurchaseOrder");
        data.put("purchaseOrderid", purchaseOrder.getPurchaseorderid());

        // sending request to api
        disposable.add(
                ApiClient.getApi().purchaseOrderApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<PurchaseOrderResponse>() {
                            @Override
                            public void onSuccess(PurchaseOrderResponse purchaseOrderResponse) {
                                if (purchaseOrderResponse.getStatus().contentEquals("success")) {
                                    Timber.v("purchaseOrderResponse");

                                    myApiResponses.postValue(new MyApiResponses("apidelete","success",purchaseOrderResponse));
                                    deleteFromDb(purchaseOrder);
                                    //GlobalMethod.goToActivity(AddPurchaseOrderActivity.this, MenuActivity.class);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apidelete","success",e));
                            }
                        })
        );
    }

    /**
     * Dispose memory used by disposable object
     */
    public void disposeQ() {
        disposable.dispose();
    }
}
