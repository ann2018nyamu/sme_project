package ke.co.eclectic.quickstore.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.List;

import ke.co.eclectic.quickstore.models.Category;
import ke.co.eclectic.quickstore.models.others.MyApiResponses;
import ke.co.eclectic.quickstore.repository.CategoryRepository;

/**
 * ViewModel holds your app's UI data in a lifecycle-conscious way
 * that survives configuration changes
 */
public class CategoryViewModel extends AndroidViewModel {
    private CategoryRepository mRepository;

    /**
     * Instantiates a new Category view model.
     *
     * @param application the application
     */
    public CategoryViewModel(Application application) {
        super(application);
        mRepository = new CategoryRepository(application);

    }
    /**
     * getter for all the categories records
     *
     * @return all categories
     * @param data request data
     */
    public LiveData<List<Category>> getAllCategories(HashMap<String, Object> data) {
        return mRepository.getAllCategories(data);
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
     * @param category the category
     */
    public void insert(Category category) { mRepository.insert(category); }

    /**
     * Delete all.
     */
    public void deleteAll() { mRepository.deleteAll(); }

    /**
     * Gets single category.
     *
     * @param category the category
     * @param action  the action to perform on the category object
     * @return the single category
     */
    public LiveData<List<Category>> getSingleCategory(Category category,String action) { return mRepository.getSingleCategory(category); }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRepository.disposeQ();
    }
}
