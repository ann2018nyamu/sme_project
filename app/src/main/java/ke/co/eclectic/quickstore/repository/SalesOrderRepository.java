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
import ke.co.eclectic.quickstore.dao.SalesOrderDao;
import ke.co.eclectic.quickstore.dao.QuickStoreRoomDb;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.inc.AppConfig;
import ke.co.eclectic.quickstore.models.SalesOrder;
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
public class SalesOrderRepository {
    private SalesOrderDao mSalesOrderDao;
    private LiveData<List<SalesOrder>> mAllSalesOrder;
    private MutableLiveData<MyApiResponses> myApiResponses = new MutableLiveData<MyApiResponses>();
    private CompositeDisposable disposable = new CompositeDisposable();
    /**
     * gets a handle to the database and initializes the member variables
     *
     * @param application the application
     */
    public SalesOrderRepository(Application application) {
        QuickStoreRoomDb db = QuickStoreRoomDb.getDatabase(application);
        mSalesOrderDao = db.salesOrderDao();
        mAllSalesOrder = mSalesOrderDao.getAllSalesOrders();

    }

    /**
     * Deletes all salesOrder form database
     */
    public void deleteAll(){
        deleteFromDb(new SalesOrder());
    }

    /**
     * Deletes all salesOrder attached to  unique business id from  database
     *
     * @param salesOrder the salesOrder
     */
    public void deleteSalesOrder(SalesOrder salesOrder){
        deleteApiSalesOrder(salesOrder);
    }
   
        public void deleteFromDb(SalesOrder salesOrder){
            disposable.add(Completable.fromAction(()->{
                if(salesOrder.getSalesorderid() == 0){
                    mSalesOrderDao.deleteAll();
                }else{
                    mSalesOrderDao.deleteSingleSalesOrder(salesOrder.getSalesorderid());
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
     * Gets all salesOrders.
     *
     * @return all salesOrders
     */
    public LiveData<List<SalesOrder>> getAllSalesOrders(HashMap<String,Object> data) {
        refreshSalesOrders(data);
        return mAllSalesOrder;
    }

    /**
     * Gets all repository network reesponse.
     *
     * @return all salesOrders
     */
    public MutableLiveData<MyApiResponses> getApiResponse() {
        return myApiResponses;
    }

    /**
     * Get single salesOrder live data.
     *
     * @param salesOrder details/information
     * @return a single salesOrder instance/record
     */
    public LiveData<List<SalesOrder>>  getSingleSalesOrder(SalesOrder salesOrder){
            getSalesOrdersItems(salesOrder.getSalesorderid());
            return  mSalesOrderDao.getSingleSalesOrder(salesOrder.getSalesorderid());
    }

    /**
     * Insert.
     *
     * @param salesOrder inserts salesOrder detail inn the database
     */
    public void insert (SalesOrder salesOrder,String requestType) {
        saveSalesOrder(salesOrder,requestType);
    }

    /**
     * Insert to sales order to db.
     *
     * @param salesOrder the sales order
     */
    public void insertToDb(SalesOrder salesOrder){
        disposable.add(Completable.fromAction(()->{
            mSalesOrderDao.insert(salesOrder) ;
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
     * updates sales order product item
     *
     * @param productE product json element
     * @param salesorderid unique id of the purchase order to update
     */
    public void updateItemsToDb(Integer salesorderid, String productE){
        disposable.add(Completable.fromAction(()->{
            mSalesOrderDao.updateProductItems(salesorderid,productE);
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
     * Saves sales order to backend
     *
     * @param salesOrder sales order object
     * @param requestType request type
     */
    private void saveSalesOrder(SalesOrder salesOrder,String requestType){
        HashMap<String, Object> data = new HashMap<>();
        data.put("request_type", requestType);

        if(salesOrder.getSalesorderid() == 0){
            data.put("customerid", salesOrder.getCustomerid());
            data.put("products", salesOrder.generateSOProduct(false));
            data.put("totalcost", salesOrder.getTotalcost());
            data.put("totaldiscount", salesOrder.getTotaldiscount());
            data.put("storeid", salesOrder.getStoreid());
        }else{
            data.put("salesorderid", salesOrder.getSalesorderid());
        }

        if(requestType.contentEquals("approvesalesorder")){
            data.put("paymenttypeid", salesOrder.getPaymentType());
        }

        if(requestType.contentEquals("editsalesorder")){
            data.put("customerid", salesOrder.getCustomerid());
            data.put("products", salesOrder.generateSOProduct(false));
            data.put("totalcost", salesOrder.getTotalcost());
            data.put("totaldiscount", salesOrder.getTotaldiscount());
            data.put("storeid", salesOrder.getStoreid());
        }
        Timber.v("requestType ".concat(requestType).concat("   ").concat( data.toString()));

        // sending request to api
        disposable.add(
                ApiClient.getApi().salesOrderApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<SalesOrderResponse>() {
                            @Override
                            public void onSuccess(SalesOrderResponse salesOrderResponse) {
                                if (salesOrderResponse.getStatus().contentEquals("success")) {
                                    Timber.v("salesOrderResponse");
                                    //myApiResponses.postValue(new MyApiResponses("apisave","success",salesOrderResponse.getData()));
                                    myApiResponses.postValue(new MyApiResponses("apisave","success",salesOrderResponse));
                                    if(salesOrder.getSalesorderid() == 0){
                                        salesOrder.setSalesorderid(salesOrderResponse.getSalesOrder().getSalesorderid());
                                    }
                                    insertToDb(salesOrder);
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
     * Save pos order details.
     *
     * @param salesOrder the sales order
     */
    public void savePos(SalesOrder salesOrder){
        HashMap<String, Object> data = new HashMap<>();
        data.put("request_type", "pos");

            data.put("paidamount", salesOrder.getPaidamount());
            data.put("balance", salesOrder.getBalance());
            data.put("items", salesOrder.generateSOProduct(true));
            data.put("totalcost", salesOrder.getTotalcost());
            data.put("totaldiscount", salesOrder.getTotaldiscount());
            data.put("storeid", salesOrder.getStoreid());
            data.put("paymenttypeid", salesOrder.getPaymentType());


        // sending request to api
        disposable.add(
                ApiClient.getApi().salesOrderApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<SalesOrderResponse>() {
                            @Override
                            public void onSuccess(SalesOrderResponse salesOrderResponse) {

                                if (salesOrderResponse.getStatus().contentEquals("success")) {
                                    Timber.v(salesOrderResponse.getData().toString());
                                    myApiResponses.postValue(new MyApiResponses("apipossave","success",salesOrderResponse));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apipossave","fail",e));
                            }
                        })
        );
    }


    /**
     * fetches sales order by id
     *
     * @param salesorderid sales order id
     */
    private void getSalesOrdersItems(final Integer salesorderid) {
        HashMap<String,Object>  data = new HashMap<>();
        data.put("request_type","getsalesorderdetails");
        data.put("salesorderid",salesorderid);
        //sending request to api
        disposable.add(
                ApiClient.getApi().salesOrderApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<SalesOrderResponse>(){
                            @Override
                            public void onSuccess(SalesOrderResponse salesOrderResponse) {
                                if(salesOrderResponse.getStatus().contentEquals("success")){
                                    insertToDb(salesOrderResponse.getSalesOrder());
                                    myApiResponses.postValue(new MyApiResponses("apisitems","success",salesOrderResponse));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apisitems","fail",e));
                            }

                        })
        );
    }

    /**
     * fetches sales order by request data
     * @param data request data
     */
    private void refreshSalesOrders(HashMap<String,Object> data) {
        //sending request to api
        disposable.add(
                ApiClient.getApi().salesOrderApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<SalesOrderResponse>(){
                            @Override
                            public void onSuccess(SalesOrderResponse salesOrderResponse) {
                                if(salesOrderResponse.getStatus().contentEquals("success")){
                                    try {
                                        myApiResponses.postValue(new MyApiResponses("apirefresh", "success", salesOrderResponse));
                                        deleteAll();
                                        for (SalesOrder p : salesOrderResponse.getSalesOrderList()) {
                                            insertToDb(p);
                                        }
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
     * deletes sales order from api
     * @param salesOrder
     */
    private void deleteApiSalesOrder(SalesOrder salesOrder){
        HashMap<String, Object> data = new HashMap<>();
        data.put("request_type", "deletesalesOrder");
        data.put("salesorderid", salesOrder.getSalesorderid());

        // sending request to api
        disposable.add(
                ApiClient.getApi().salesOrderApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<SalesOrderResponse>() {
                            @Override
                            public void onSuccess(SalesOrderResponse salesOrderResponse) {
                                if (salesOrderResponse.getStatus().contentEquals("success")) {
                                    Timber.v("salesOrderResponse");

                                    myApiResponses.postValue(new MyApiResponses("apidelete","success",salesOrderResponse));
                                    deleteFromDb(salesOrder);
                                    //GlobalMethod.goToActivity(AddSalesOrderActivity.this, MenuActivity.class);
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
     * Dispose disposable used  memory
     */
    public void disposeQ() {
        disposable.dispose();
    }


    /**
     * Send mpesa payment request.
     *
     * @param payData the pay data
     */
    public void sendPaymentRequest(HashMap<String, Object> payData) {
       HashMap<String,Object> data = new HashMap<>();
        data.put("request_type","mpesastk");
        data.put("phonenumber", payData.get("phone"));
        data.put("amount", payData.get("amount"));
        data.put("orderid", payData.get("orderid"));
        data.put("paymenttype", payData.get("type"));
        disposable.add(
                ApiClient.getApi().salesOrderApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<SalesOrderResponse>(){
                            @Override
                            public void onSuccess(SalesOrderResponse salesOrderResponse) {
                                if(salesOrderResponse.getStatus().contentEquals("success")){
                                    myApiResponses.postValue(new MyApiResponses("apipitems","success",salesOrderResponse));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apipitems","fail",e));
                            }

                        })
        );
    }
}
