package ke.co.eclectic.quickstore.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.dao.QuickStoreRoomDb;
import ke.co.eclectic.quickstore.dao.ProductDao;
import ke.co.eclectic.quickstore.models.Product;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.rest.ApiClient;
import ke.co.eclectic.quickstore.rest.response.ProductResponse;
import timber.log.Timber;

/**
 * STEP 4
 * A Repository manages query threads and allows you to use multiple backends.
 * the Repository implements the logic for deciding whether to fetch data from a
 * network or use results cached in a local database.
 */
public class ProductRepository {
    private ProductDao mProductDao;
    private LiveData<List<Product>> mAllProduct;
    private MutableLiveData<MyApiResponses> myApiResponses = new MutableLiveData<MyApiResponses>();
    private CompositeDisposable disposable = new CompositeDisposable();
    /**
     * gets a handle to the database and initializes the member variables
     *
     * @param application the application
     */
    public ProductRepository(Application application) {
        QuickStoreRoomDb db = QuickStoreRoomDb.getDatabase(application);
        mProductDao = db.productDao();

        mAllProduct = mProductDao.getAllProducts();

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
     * Deletes all product from database
     */
    public void deleteAll(){
        deleteFromDb(new Product());
    }

    /**
     * Deletes all product attached to  unique business id from  database
     *
     * @param product the product
     */
    public void deleteProduct(Product product){

        deleteFromDb(product);
    }



    /**
     * Gets all products.
     *
     * @return all products
     */
    public LiveData<List<Product>> getAllProducts(HashMap<String,Object> data) {
        refreshProducts(data);
        return mAllProduct;
    }

    /**
     * Get single product live data.
     *
     * @param pName name of the product
     * @return a single product instance/record
     */
    public LiveData<Product>  getSingleProductByName(String  pName){
            return  mProductDao.getSingleProductByName(pName);
    }
    /**
     * Get single product live data.
     *
     * @param pId unique id of the product
     * @return a single product instance/record
     */
    public LiveData<Product> getSingleProductById(Integer pId) {
        return  mProductDao.getSingleProductById(pId);
    }


    /**
     * sends product data to api.
     *
     * @param product inserts product detail inn the database
     */
    public void insert (Product product) {
        HashMap<String,Object> data = new HashMap<>();
        data.put("request_type","editproduct");
        if(product.getProductid() == 0){
            data.put("request_type","addproduct");
        }

        data.put("productname",product.getProductname());
        data.put("unitid",product.getUnitid());
        data.put("barcode",product.getBarcode());
        data.put("categoryid",product.getCategoryid());
        data.put("photo",product.getImage());
        data.put("businessid",product.getBusinessid());

        // sending request to api
        disposable.add(
                ApiClient.getApi().productApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ProductResponse>(){
                            @Override
                            public void onSuccess(ProductResponse productResponse) {
                                if(productResponse.getStatus().contentEquals("success")){
                                    myApiResponses.postValue(new MyApiResponses("apisave","success",productResponse));
                                    if(product.getProductid() == 0){
                                        product.setProductid(productResponse.getProduct().getProductid());
                                    }
                                    insertToDb(product);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apisave","fail",e));
                                //CodingMsg.t(getActivity(),e.getMessage());
                            }
                        })
        );

    }




    /**
     * fetches product infor from api
     * @param data  request data
     */
    private void refreshProducts(HashMap<String,Object> data) {
        //sending request to api
        disposable.add(
                ApiClient.getApi().productApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ProductResponse>(){
                            @Override
                            public void onSuccess(ProductResponse productResponse) {
                                if(productResponse.getStatus().contentEquals("success")){
                                    Timber.v("From refreshProducts ");
                                    deleteAll();
                                    for(Product p:productResponse.getProductList()){
                                        insertToDb(p);
                                    }
                                    myApiResponses.postValue(new MyApiResponses("apirefresh","success",productResponse));

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
     * inserts product to db
     *
     * @param product product to insert
     */
    private void insertToDb(Product product){
        disposable.add(Completable.fromAction(()->{
            mProductDao.insert(product) ;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        myApiResponses.postValue(new MyApiResponses("dbsave","success",product));
                    }

                    @Override
                    public void onError(Throwable e) {
                        myApiResponses.postValue(new MyApiResponses("dbsave","fail",e));
                    }
                }));
    }
    /**
     * deletes product to db
     *
     * @param product product to insert
     */
    private void deleteFromDb(Product product){
        disposable.add(Completable.fromAction(()->{
            mProductDao.deleteAll() ;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        myApiResponses.postValue(new MyApiResponses("dbsave","success",product));
                    }

                    @Override
                    public void onError(Throwable e) {
                        myApiResponses.postValue(new MyApiResponses("dbsave","fail",e));
                    }
                }));
    }

    /**
     * clears diposable memory
     */
    public void disposeQ() {
        disposable.clear();
    }
}
