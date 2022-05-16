package ke.co.eclectic.quickstore.activities.products;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.adapters.CategoryAdapter;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.Category;
import ke.co.eclectic.quickstore.rest.ApiClient;
import ke.co.eclectic.quickstore.rest.response.CategoryResponse;
import ke.co.eclectic.quickstore.viewModel.CategoryViewModel;
import timber.log.Timber;

/**
 * The type Product category activity.For displaying categories
 */
public class ProductCategoryActivity extends AppCompatActivity implements CategoryAdapter.CategoryComm {
    public static final String EXTRA_REPLY = "CATEGORY_REPLY";

    /**
     * The Tool bar.
     */
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.categoryRV)
    RecyclerView categoryRV;
    @BindView(R.id.rootView)
     CoordinatorLayout rootView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.txtCategoryTitle)
    TextView txtCategoryTitle;
    @BindView(R.id.extCatSearch)
    EditText extCatSearch;

    private CompositeDisposable disposable = new CompositeDisposable();
    List<Category> categoryList = new ArrayList<>();
    List<Category> categoryListCopy = new ArrayList<>();
    private Dialog addCateogryDialog;
    private CategoryAdapter categoryAdapter;
    private String action="";
    private CategoryViewModel mCategoryViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_category);
        Bundle  extra = getIntent().getExtras();
        if(extra  != null){
            action = extra.get("action").toString();
        }
        ButterKnife.bind(this);

        initToolbar();
        initEdittext();
        initFab();
        initRV();
        initObservable();
        initData();

    }

    /**
     * initializes data
     */
    private void initData() {
        mCategoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        getCategory();
    }

    /**
     * initializes observables used in class
     */
    private void initObservable() {
        Observable<String> searchObservable = RxTextView.textChanges(extCatSearch)
                .skip(1)
                .map(charSequence -> charSequence.toString());
        //subscribing an observer
        searchObservable.subscribe(new DisposableObserver<String>() {
            @Override
            public void onNext(String charSequence) {
                filterCategories(charSequence);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }

        });
    }

    /**
     * filtering categories
     *
     * @param filterStr filtereing string
     */
    private void filterCategories(String filterStr) {
        List<Category> filterListCopy = new ArrayList<>();
        // Filter data
        for (int i = 0; i < categoryListCopy.size(); i++) {
            if (categoryListCopy.get(i).getCategoryname().toLowerCase().contains(filterStr.toLowerCase()) ) {
                    filterListCopy.add(categoryListCopy.get(i));
            }
        }
        categoryList = filterListCopy;
        if(filterStr.trim().length()<1){
            categoryList = categoryListCopy;
        }

        categoryAdapter.refresh(categoryList);
    }

    /**
     * initializing edittext
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEdittext() {
        extCatSearch.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });

    }

    /**
     * initializing floating button
     */
    private void initFab() {
        if(!GlobalVariable.getCurrentUser().getCurrentBsRule("products", "cancreate")) {
            fab.hide();
            return;
        }
            fab.setOnClickListener(view ->{showCatAddDialogue();});
    }

    /**
     * showing category add dialogue
     */
    @SuppressLint("ClickableViewAccessibility")
    private void showCatAddDialogue() {
        addCateogryDialog = new Dialog(this);
        addCateogryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addCateogryDialog.setContentView(R.layout.dialogue_addunit_layout);
        addCateogryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtTitle =  addCateogryDialog.findViewById(R.id.txtTitle);
        Button btnSave = addCateogryDialog.findViewById(R.id.btnSave);
        Button btnCancel = addCateogryDialog.findViewById(R.id.btnCancel);
        EditText etxtName = addCateogryDialog.findViewById(R.id.etxtName);
        txtTitle.setText("Add Category");
        btnSave.setText("Save Category");
        etxtName.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });


        btnSave.setOnClickListener(v -> {
            String name = "";
            if(etxtName.getText().toString().trim().isEmpty()){
                etxtName.setError("Enter Category Name");
                return ;
            }

            saveCategory(etxtName.getText().toString());
            addCateogryDialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> {
            addCateogryDialog.dismiss();
        });

        addCateogryDialog.setCancelable(true);
        addCateogryDialog.show();
    }

    /**
     * saving category
     * @param catname category name to be saved
     */
    private void saveCategory(String catname) {
        //Todo move code to repository
        GlobalMethod.showloader(this,"Saving category.....",true);
        HashMap<String,Object> data = new HashMap<>();
        data.put("request_type","addcategory");
        data.put("categoryname",catname);
        data.put("businessid",GlobalVariable.getCurrentUser().getBusinessid(this));
        // sending request to api
        disposable.add(
                ApiClient.getApi().categoryApi(new JsonParser().parse(new Gson().toJson(data)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<CategoryResponse>(){
                            @Override
                            public void onSuccess(CategoryResponse categoryResponse){
                                GlobalMethod.stoploader(ProductCategoryActivity.this);
                                if(categoryResponse.getStatus().contentEquals("success")){
                                    categoryList.add(categoryList.size(),categoryResponse.getData());
                                    categoryListCopy = categoryList;
                                    txtCategoryTitle.setText("Category name(".concat(((Integer)categoryList.size()).toString().concat(")")));
                                    categoryAdapter.notifyItemInserted(categoryList.size());

                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                GlobalMethod.parseCommError(ProductCategoryActivity.this,e,rootView);
                                //CodingMsg.t(getActivity(),e.getMessage());
                            }
                        })
        );
    }

    /**
     * cleans up memory used by observables
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    /**
     * initializes toolbar
     */
    private void initToolbar() {
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Category");
    }

    /**
     * initializes recycleview
     */
    private void initRV() {
        categoryAdapter = new CategoryAdapter(this,categoryList);
        categoryRV.setLayoutManager(new LinearLayoutManager(this));
        categoryRV.setAdapter(categoryAdapter);
    }

    /**
     * sets data to category spinner
     * @param catList category list to be added to spnner
     */
    private void initCategorySpinnerData(List<Category> catList){
        categoryList.clear();
        categoryList.addAll(catList);
        categoryListCopy = categoryList;
        txtCategoryTitle.setText("Category name(".concat(((Integer)categoryList.size()).toString().concat(")")));
        categoryAdapter.notifyDataSetChanged();
    }

    /**
     * retrieves category data source(db/api)
     */
    private void getCategory() {
        HashMap<String,Object> data = new HashMap<>();
        data.put("request_type","getcategory");
        data.put("businessid",GlobalVariable.getCurrentUser().getBusinessid(this));

        mCategoryViewModel.getAllCategories(data).observe(this,categories->{
            try {
                if( null != categories){
                    initCategorySpinnerData(categories);
                }
            }catch (Exception e){
                Timber.v("categories error"+ e.getMessage());
            }
        });
        mCategoryViewModel.getApiResponse().observe(this,myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            if(myApiResponses.getStatus().contentEquals("success")){

            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(ProductCategoryActivity.this,myApiResponses.getError(),rootView);
                }
            }

        });

    }

    /**
     * implementation of recycler adapter for listening for click events
     *
     * @param position of the clicked item
     * @param category item clicked
     * @param action to be performed to the clicked item
     */
    @Override
    public void catMessage(int position, Category category, String action) {
        Timber.v("category selected");
        if(this.action.contentEquals("select")){
            Intent replyIntent = new Intent();
            replyIntent.putExtra(EXTRA_REPLY, new Gson().toJson(category));
            setResult(RESULT_OK, replyIntent);
            finish();
        }
    }

    /**
     * handles action bar item clicked
     *
     * @param item action bar item clicked
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

}
