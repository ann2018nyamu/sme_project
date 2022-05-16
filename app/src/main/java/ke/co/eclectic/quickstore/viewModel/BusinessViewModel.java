package ke.co.eclectic.quickstore.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import ke.co.eclectic.quickstore.models.Business;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.repository.BusinessRepository;

/**
 * ViewModel holds your app's UI data in a lifecycle-conscious way
 * that survives configuration changes
 */
public class BusinessViewModel extends AndroidViewModel {
    private BusinessRepository mRepository;
    private LiveData<List<Business>> mAllBusiness;
    //implementing singleton


    /**
     * Instantiates a new Business view model.
     *
     * @param application the application
     */
    public BusinessViewModel(Application application) {
        super(application);
        mRepository = new BusinessRepository(application);
        mAllBusiness = mRepository.getAllBusinesss();
    }

    /**
     * gets repository response object
     * @return all errors
     */
    public MutableLiveData<MyApiResponses> getApiResponse() {
        return mRepository.getApiResponse();
    }


    /**
     * getter for all the users records
     *
     * @return all business
     */
    public LiveData<List<Business>> getAllBusiness() {
        return mAllBusiness;
    }

    /**
     * Insert.
     *
     * @param business the business
     */
    public void insert(Business business) { mRepository.insert(business); }

    /**
     * Delete all.
     */
    public void deleteAll() { mRepository.deleteAll(); }

    /**
     * Gets single business.
     *
     * @param business the business
     * @return the single business
     */
    public LiveData<List<Business>> getSingleBusiness(Business business) { return mRepository.getSingleBusiness(business); }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRepository.disposeQ();
    }

    public void getBusinessRole() {
        mRepository.getBusinessRole();
    }
}
