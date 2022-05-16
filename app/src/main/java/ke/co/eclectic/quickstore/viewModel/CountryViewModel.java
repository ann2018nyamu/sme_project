package ke.co.eclectic.quickstore.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import ke.co.eclectic.quickstore.models.Country;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.repository.CountryRepository;

/**
 * ViewModel holds your app's UI data in a lifecycle-conscious way
 * that survives configuration changes
 */
public class CountryViewModel extends AndroidViewModel {
    private CountryRepository mRepository;
    private LiveData<List<Country>> mAllCountrys;
    //implementing singleton
    private static volatile CountryViewModel INSTANCE;

    /**
     * Instantiates a new Country view model.
     *
     * @param application the application
     */
    public CountryViewModel(Application application) {
        super(application);
        mRepository = new CountryRepository(application);
    }

    public void updateImage(String bitmapStr,String code){
          mRepository.updateImage(bitmapStr,code);
    }

    /**
     * getter for all the users records
     *
     * @return all countries
     */
    public LiveData<List<Country>> getAllCountries() {

        return mRepository.getAllCountries();
    }

    /**
     * gets errors object
     *
     * @return all errors
     */
    public MutableLiveData<MyApiResponses> getApiResponse() {
        return mRepository.getApiResponse();
    }

    /**
     * Insert.
     *
     * @param country the country
     */
    public void insert(Country country) { mRepository.insert(country); }

    /**
     * Delete all.
     */
    public void deleteAll() { mRepository.deleteAll(); }

    /**
     * Gets single country.
     *
     * @param country the country
     * @param action  the action
     * @return the single country
     */
    public LiveData<List<Country>> getSingleCountry(Country country,String action) { return mRepository.getSingleCountry(country); }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRepository.disposeQ();
    }
}
