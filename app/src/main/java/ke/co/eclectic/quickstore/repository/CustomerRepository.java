package ke.co.eclectic.quickstore.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import ke.co.eclectic.quickstore.dao.CustomerDao;
import ke.co.eclectic.quickstore.models.Customer;
import ke.co.eclectic.quickstore.models.User;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.rest.ApiClient;
import ke.co.eclectic.quickstore.rest.response.CustomerResponse;
import timber.log.Timber;

/**
 * STEP 4
 * A Repository manages query threads and allows you to use multiple backends.
 * the Repository implements the logic for deciding whether to fetch data from a
 * network or use results cached in a local database.
 */

public class CustomerRepository {
    private CustomerDao mCustomerDao;
    private LiveData<List<Customer>> mAllCustomer;
    private MutableLiveData<MyApiResponses> myApiResponses = new MutableLiveData<MyApiResponses>();
    private CompositeDisposable disposable = new CompositeDisposable();

    /**
     * gets a handle to the database and initializes the member variables
     *
     * @param application the application
     */
    public CustomerRepository(Application application) {
        QuickStoreRoomDb db = QuickStoreRoomDb.getDatabase(application);
        mCustomerDao = db.customerDao();
        mAllCustomer = mCustomerDao.getAllCustomers();
    }

    /**
     * Deletes all customer form database
     */
    public void deleteAll() {
        deleteFromDb(new Customer());
    }

    /**
     * Deletes all customer attached to  unique business id from  database
     *
     * @param customer the customer
     */
    public void deleteCustomer(Customer customer) {
        deleteApiCustomer(customer);
    }

    /**
     * Delete from db.
     *
     * @param customer the customer
     */
    public void deleteFromDb(Customer customer) {
        disposable.add(Completable.fromAction(() -> {
            if (customer.getCustomerid() == 0) {
                mCustomerDao.deleteAll();
            } else {
                mCustomerDao.deleteSingleCustomer(customer.getCustomerid());
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        myApiResponses.postValue(new MyApiResponses("dbdelete", "success", new JsonObject()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        myApiResponses.postValue(new MyApiResponses("dbdelete", "error", e));
                    }
                }));
    }


    /**
     * Gets all customers.
     *
     * @return all customers
     */
    public LiveData<List<Customer>> getAllCustomers(HashMap<String, Object> data) {
        refreshCustomers(data);
        return mAllCustomer;
    }

    /**
     * Gets all repository network response.
     *
     * @return all customers
     */
    public MutableLiveData<MyApiResponses> getApiResponse() {
        return myApiResponses;
    }

    /**
     * Get single customer live data.
     *
     * @param customer details/information
     * @return a single customer instance/record
     */
    public LiveData<List<Customer>> getSingleCustomer(Customer customer) {
        return mCustomerDao.getSingleCustomer(customer.getCustomerid());
    }

    /**
     * saves customer to db/send save request to api
     *
     * @param customer inserts customer detail inn the database
     */
    public void insert(Customer customer) {
        saveCustomer(customer);
    }


    /**
     * Insert customer record to  local db.
     *
     * @param customer the customer
     */
    public void insertToDb(Customer customer) {
        disposable.add(Completable.fromAction(() -> {
            mCustomerDao.insert(customer);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        myApiResponses.postValue(new MyApiResponses("dbsave", "success", new JsonObject()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        myApiResponses.postValue(new MyApiResponses("dbsave", "fail", e));
                    }
                }));
    }

    /**
     * send customer save request to api
     *
     * @param customer customer record to save
     */
    private void saveCustomer(Customer customer) {
        HashMap<String, Object> data = new HashMap<>();
        if (customer.getCustomerid() == 0) {
            data.put("businessid", customer.getBusinessid());
            data.put("request_type", "addcustomer");
        } else {
            data.put("customerid", customer.getCustomerid());
            data.put("request_type", "editcustomer");
        }

        data.put("companyname", customer.getCompanyname());
        data.put("contactname", customer.getContactname());
        data.put("identificationnumber", customer.getIdentificationnumber());
        data.put("countrycode", customer.getCountrycode());
        data.put("country", customer.getCountry());
        data.put("town", customer.getTown());
        data.put("phonenumber", customer.getPhonenumber());
        data.put("othernumber", customer.getOthernumber());
        data.put("email", customer.getEmail());
        data.put("otheremail", customer.getOtheremail());


        // sending request to api
        disposable.add(
                ApiClient.getApi().customerApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<CustomerResponse>() {
                            @Override
                            public void onSuccess(CustomerResponse customerResponse) {
                                if (customerResponse.getStatus().contentEquals("success")) {
                                    //myApiResponses.postValue(new MyApiResponses("apisave","success",customerResponse.getData()));

                                    myApiResponses.postValue(new MyApiResponses("apisave", "success", customerResponse));
                                    if (customer.getCustomerid() == 0) {
                                        customer.setCustomerid(customerResponse.getCustomer().getCustomerid());
                                    }
                                    insertToDb(customer);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apisave", "fail", e));
                            }
                        })
        );
    }

    /**
     * fetches customer record from api
     * @param data request data to send
     */
    private void refreshCustomers(HashMap<String, Object> data) {
        //sending request to api
        disposable.add(
                ApiClient.getApi().customerApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<CustomerResponse>() {
                            @Override
                            public void onSuccess(CustomerResponse customerResponse) {

                                if (customerResponse.getStatus().contentEquals("success")) {
                                    deleteAll();
                                    for (Customer p : customerResponse.getCustomerList()) {
                                        insertToDb(p);
                                    }
                                    Timber.v("customerResponse :  " + customerResponse.getCustomerList().size());
                                    myApiResponses.postValue(new MyApiResponses("apirefresh", "success", customerResponse));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apirefresh", "fail", e));
                            }
                        })
        );
    }

    /**
     * send deletes customer request to api
     * @param customer
     */
    private void deleteApiCustomer(Customer customer) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("request_type", "deletecustomer");
        data.put("customerid", customer.getCustomerid());
        data.put("businessid", customer.getBusinessid());

        // sending request to api
        disposable.add(
                ApiClient.getApi().customerApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<CustomerResponse>() {
                            @Override
                            public void onSuccess(CustomerResponse customerResponse) {
                                if (customerResponse.getStatus().contentEquals("success")) {
                                    Timber.v("customerResponse");
                                    myApiResponses.postValue(new MyApiResponses("apidelete", "success", customerResponse));
                                    deleteFromDb(customer);
                                    //GlobalMethod.goToActivity(AddCustomerActivity.this, MenuActivity.class);
                                }
                            }
                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apidelete", "fail", e));
                            }
                        })
        );
    }

    /**
     * clears memory used by disposable/network request observable
     */
    public void disposeQ() {
        disposable.dispose();
    }
}
