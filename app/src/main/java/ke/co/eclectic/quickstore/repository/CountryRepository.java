package ke.co.eclectic.quickstore.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.dao.CountryDao;
import ke.co.eclectic.quickstore.dao.QuickStoreRoomDb;
import ke.co.eclectic.quickstore.models.Category;
import ke.co.eclectic.quickstore.models.Country;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.rest.ApiClient;
import ke.co.eclectic.quickstore.rest.response.CountryResponse;
import timber.log.Timber;

/**
 * STEP 4
 * A Repository manages query threads and allows you to use multiple backends.
 * the Repository implements the logic for deciding whether to fetch data from a
 * network or use results cached in a local database.
 */
public class CountryRepository {
    private CountryDao mCountryDao;
    private LiveData<List<Country>> mAllCountries;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableLiveData<MyApiResponses> myApiResponses = new MutableLiveData<MyApiResponses>();



    /**
     * gets a handle to the database and initializes the member variables
     *
     * @param application the application
     */
    public CountryRepository(Application application) {
        QuickStoreRoomDb db = QuickStoreRoomDb.getDatabase(application);
        mCountryDao = db.countryDao();
        mAllCountries = mCountryDao.getAllCountryDatas();
    }

    /**
     * Update country flag image.
     *
     * @param bitmapStr  the bitmap str
     * @param code      the country code
     */
    public void updateImage(String bitmapStr,String code){
        Country country = new Country();
        country.setImagestr(bitmapStr);
        country.setCode(code);

        updateCountryDb(country);
    }

    /**
     * Deletes all country from database
     */
    public void deleteAll(){
        deleteFromDb();

    }

    /**
     * Gets all countries.
     *
     * @return all countrys
     */
    public LiveData<List<Country>> getAllCountries() {
        //fetch data only when it does not exist in local db
        if(null == mAllCountries.getValue() ){
            refreshCountry();
        }
        return mAllCountries;
    }

    /**
     * fetches country data from api
     */
    private void refreshCountry() {
        //sending request to api
        disposable.add(
                ApiClient.getApi().getCountryData()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<CountryResponse>(){
                            @Override
                            public void onSuccess(CountryResponse countryResponse) {
                                Timber.v("Refresh countries 1 "+countryResponse.getStatus());
                                if(countryResponse.getStatus().contentEquals("success")){
                                    Timber.v("Refresh countries 2");
                                    for(Country p:countryResponse.getDataArrayList()){
                                        insert(p);
                                    }
                                    myApiResponses.postValue(new MyApiResponses("apirefresh","success",countryResponse));
                                    disposable.clear();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apirefresh","fail",e));
                                Timber.v("Throwable Country(); "+e.getMessage());
                            }
                        })
        );
    }

    /**
     * Gets all repository network response.
     *
     * @return all suppliers
     */
    public MutableLiveData<MyApiResponses> getApiResponse() {
        return myApiResponses;
    }

    /**
     * Get single country live data.
     *
     * @param country details/information
     * @return a single country instance/record
     */
    public LiveData<List<Country>>  getSingleCountry(Country country){
            return  mCountryDao.getSingleCountryData(country.getName());
    }

    /**
     * Insert record to local db.
     *
     * @param country inserts country detail inn the database
     */
    public void insert (Country country) {
        insertToDb(country);
    }


    /**
     * Insert record to local db.
     *
     *@param country  country object
     */
    private void insertToDb(Country country){
        disposable.add(Completable.fromAction(()->{


            mCountryDao.insert(country);

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        myApiResponses.postValue(new MyApiResponses("dbsave","success",new JsonObject()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        myApiResponses.postValue(new MyApiResponses("dbsave","error",e));
                    }
                }));
    }

    /**
     * delete country records
     *
     *
     */
    private void deleteFromDb(){
        disposable.add(Completable.fromAction(()->{

            mCountryDao.deleteAll();

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
     * updates country image flag
     *
     * @param country
     */
    private void updateCountryDb(Country country){
        disposable.add(Completable.fromAction(()->{

            mCountryDao.updateImage(country.getImagestr(),country.getCode());

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        myApiResponses.postValue(new MyApiResponses("dbupdate","success",new JsonObject()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        myApiResponses.postValue(new MyApiResponses("dbupdate","error",e));
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
