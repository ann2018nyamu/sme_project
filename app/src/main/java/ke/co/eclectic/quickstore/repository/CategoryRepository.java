package ke.co.eclectic.quickstore.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

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
import ke.co.eclectic.quickstore.dao.CategoryDao;
import ke.co.eclectic.quickstore.dao.QuickStoreRoomDb;
import ke.co.eclectic.quickstore.models.Business;
import ke.co.eclectic.quickstore.models.Category;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.rest.ApiClient;
import ke.co.eclectic.quickstore.rest.response.CategoryResponse;
import timber.log.Timber;

/**
 * STEP 4
 * A Repository manages query threads and allows you to use multiple backends.
 * the Repository implements the logic for deciding whether to fetch data from a
 * network or use results cached in a local database.
 */
public class CategoryRepository {
    private CategoryDao mCategoryDao;
    private LiveData<List<Category>> mAllCategory;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableLiveData<MyApiResponses> myApiResponses = new MutableLiveData<MyApiResponses>();


    /**
     * gets a handle to the database and initializes the member variables
     *
     * @param application the application
     */
    public CategoryRepository(Application application) {
        QuickStoreRoomDb db = QuickStoreRoomDb.getDatabase(application);
        mCategoryDao = db.categoryDao();
        mAllCategory = mCategoryDao.getAllCategories();
    }
    /**
     * Gets all repository network reesponse.
     *
     * @return all MutableLiveData
     */
    public MutableLiveData<MyApiResponses> getApiResponse() {
        return myApiResponses;
    }
   


    /**
     * Deletes all category from database
     */
    public void deleteAll(){
        deleteFromDb(new Category());

    }
    /**
     * deletes category record from local db
     *
     * @param category
     */
    private void deleteFromDb(Category category){
        disposable.add(Completable.fromAction(()->{
            if(category.getCategoryid() == 0){
                mCategoryDao.deleteAll();
            }else{
                mCategoryDao.deleteSingleCategory(category.getCategoryname());
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
     * Deletes all category attached to  unique category id from  database
     *
     * @param category the category
     */
    public void deleteCategory(Category category){
        deleteFromDb(category);
    }

    /**
     * Gets all category from db then api.
     *
     * @return all categories
     * @param data request data
     */
    public LiveData<List<Category>> getAllCategories(HashMap<String, Object> data) {
        refreshCategory(data);
        return mAllCategory;
    }

    /**
     * fetches category data from api
     *
     * @param data request data
     */
    private void refreshCategory(HashMap<String, Object> data){
        // sending request to api
        disposable.add(
                ApiClient.getApi().categoryApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<CategoryResponse>(){
                            @Override
                            public void onSuccess(CategoryResponse categoryResponse) {
                                if(categoryResponse.getStatus().contentEquals("success")){

                                    for(Category c:categoryResponse.getCategoryList()){
                                        insert(c);
                                    }
                                    myApiResponses.postValue(new MyApiResponses("apirefresh","success",categoryResponse));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                myApiResponses.postValue(new MyApiResponses("apirefresh","fail",e));
                                //CodingMsg.t(getActivity(),e.getMessage());
                            }
                        })
        );
    }

    /**
     * Get single category live data.
     *
     * @param category details/information
     * @return a single category instance/record
     */
    public LiveData<List<Category>>  getSingleCategory(Category category){
            return  mCategoryDao.getSingleCategoryData(category.getCategoryname());
    }


    /**
     * Insert category
     *
     * @param category inserts category detail inn the database
     */
    public void insert (Category category) {
        insertToDb(category);

    }


    /**
     * saves category record to local db
     *
     * @param category the category to save
     */
    public void insertToDb(Category category){
        disposable.add(Completable.fromAction(()->{
            mCategoryDao.insert(category) ;
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
     * clears diposable memory
     */
    public void disposeQ() {
        disposable.clear();
    }
}
