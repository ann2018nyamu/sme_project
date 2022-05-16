package ke.co.eclectic.quickstore.activities.products;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.adapters.AllProductAdapter;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.Product;
import ke.co.eclectic.quickstore.rest.response.ProductResponse;
import ke.co.eclectic.quickstore.viewModel.ProductViewModel;
import timber.log.Timber;


public class AllProductActivity extends AppCompatActivity implements AllProductAdapter.ProdComm {
    public static final String EXTRA_REPLY = "PRODUCT_REPLY";
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.extProductSearch)
    EditText extProductSearch;
    @BindView(R.id.imgAddProduct)
    ImageView imgAddProduct;

    @BindView(R.id.rootView)
    CoordinatorLayout rootView;
    @BindView(R.id.productsRV)
     RecyclerView productsRV;
    @BindView(R.id.txtProductTitle)
     TextView txtProductTitle;

    private CompositeDisposable disposable = new CompositeDisposable();
    private List<Product> productlists = new ArrayList<>();
    private List<Product> productlistsCopy = new ArrayList<>();
    private AllProductAdapter allProductAdapter;
    private String action="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_product);
        ButterKnife.bind(this);
        Bundle  extra = getIntent().getExtras();

        if(extra  != null){
            action = extra.get("action").toString();
            if(null == action){
                action = "";
            }
        }

        initToolbar();
        initImageview();
        initEdittext();
        initRV();
        initObservable();
        initData();
    }
    /**
     * initializes activity data
     */
    private void initData() {
        ProductViewModel mProductViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
        HashMap<String,Object> data = new HashMap<>();
        data.put("request_type","getproducts");
        data.put("businessid",GlobalVariable.getCurrentUser().getBusinessid(this));

        mProductViewModel.getAllProducts(data).observe(this, productList->{
            try {

                if( null != productList ){
                    txtProductTitle.setText("Products(".concat(((Integer)productList.size()).toString()).concat(")"));
                    Timber.v("From getAllProducts "+ productList.size());
                    productlistsCopy = productList;
                    categoriseProductList(productList);
                }

            }catch (Exception e){
                Timber.v("products error"+ e.getMessage());
            }
        });
        mProductViewModel.getApiResponse().observe(this, myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            if(myApiResponses.getStatus().contentEquals("success")){
                if(myApiResponses.getOperation().contains("refresh")) {
                    ProductResponse productResponse = (ProductResponse) myApiResponses.getObject();
                    if (productResponse.getProductList().size() == 0) {
                        CodingMsg.tl(this,"No product");
                    }
                }
            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(this,myApiResponses.getError(),rootView);
                }
            }
        });




        mProductViewModel.getIsInternetConnected().observe(this, aBoolean -> {
            if(!aBoolean){
                //CodingMsg.tle(this, "Something went wrong.Please check your Internet");
            }

        });

    }


    /**
     * initializes activity observables
     */
    private void initObservable() {
        Observable<String> searchObservable = RxTextView.textChanges(extProductSearch)
                .skip(1)
                .debounce(300,TimeUnit.MILLISECONDS)
                .map(charSequence -> charSequence.toString());
        //subscribing an observer
        searchObservable.
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<String>() {
            @Override
            public void onNext(String charSequence) {
                Timber.v("searchObservable  "+charSequence);
               // filterProducts(charSequence.toLowerCase());
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
     * filters product list
     * @param filterStr string to filter products
     */
    private void filterProducts(String filterStr) {
        Timber.v(filterStr +" "+productlistsCopy.size());

        if(productlistsCopy.size() == 0){return;}
        List<Product> filterListCopy = new ArrayList<>();
            // Filter data
            for (int i = 0; i < productlistsCopy.size(); i++) {
                if (productlistsCopy.get(i).getCategoryname().toLowerCase().contains(filterStr)
                        || productlistsCopy.get(i).getProductname().toLowerCase().contains(filterStr)
                        || productlistsCopy.get(i).getPprice().toString().toLowerCase().contains(filterStr)
                        ) {
                    Timber.v(productlistsCopy.get(i).getProductname() +" "+productlistsCopy.size());
                        filterListCopy.add(productlistsCopy.get(i));
                }
            }
        productlists = filterListCopy;
        if(filterStr.trim().length()<1){
            productlists = productlistsCopy;
        }
        categoriseProductList(productlists);
    }
    /**
     * initializes activity edittext
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEdittext() {
        extProductSearch.setFocusable(true);
        extProductSearch.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });
    }

    /**
     * This function add a header in the product list
     * @param pList   product list
     * @return formated product list
     */
    private List<Product> categoriseProductList(List<Product> pList){
        productlists.clear();
        List<Product> pList2 = new ArrayList<>();
        Product productTitle;
        String title = "";
        for(Product p: pList){
            if(!title.toLowerCase().contentEquals(p.getCategoryname().toLowerCase())){
                productTitle = new Product();
                productTitle.setProductname(p.getCategoryname());
                productTitle.setCategoryname(p.getCategoryname());
                pList2.add(productTitle);
                title=p.getCategoryname();
            }
            pList2.add(p);
        }

        productlists =pList2;
        allProductAdapter.refresh(productlists);
        return pList2;

    }
    /**
     * dummy data for testing ui //Todo remove dummy data
     */
  public void dummyData(){
      Product product = new Product(1,"Rewards",0.00);
      product.setProductname("Milk");
      product.setCategoryname("Milk");
      productlists.add(product);
      product = new Product(2,"Rewards",0.00);
      product.setCategoryname("Milk");
      productlists.add(product);
      product = new Product(3,"Rewards",0.00);
      product.setCategoryname("Milk");
      product.setProductname("Brookside  - 500ml");
      productlists.add(product);
      product = new Product(4,"Rewards",0.00);
      product.setCategoryname("Cerials");
      product.setProductname("Cerials");
      productlists.add(product);

      product = new Product(5,"Rewards",0.00);
      product.setCategoryname("Cerials");
      product.setProductname("Millet");
      productlists.add(product);
      product = new Product(6,"Rewards",0.00);
      product.setCategoryname("Cerials");
      product.setProductname("Maize");
      productlists.add(product);
      product = new Product(7,"Rewards",0.00);
      product.setCategoryname("Cerials");
      product.setProductname("Sorghum");
      productlists.add(product);
  }
    /**
     * initializes recyclerview  with data
     */
    private void initRV(){
       // dummyData();
        allProductAdapter = new AllProductAdapter(this,productlists);
        productsRV.setLayoutManager(new LinearLayoutManager(this));
        productsRV.setAdapter(allProductAdapter);
        productlists = categoriseProductList(productlists);
    }
    /**
     * initializes imageview
     */
    private void initImageview() {
        if(!GlobalVariable.getCurrentUser().getCurrentBsRule("products","cancreate")) {
            imgAddProduct.setVisibility(View.GONE);
            return;
        }

            imgAddProduct.setOnClickListener(v -> GlobalMethod.goToActivity(AllProductActivity.this, AddProductActivity.class));

    }

    /**
     * cleans up memory used up by observables
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
        setTitle("Products");
    }

    /**
     * listens to clicked menuitem
     * @param item clicked menu iten
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


    /**
     * implementation of recycler adapter
     *
     * @param position the position of clciked item
     * @param product  the product clicked/choosen
     * @param action   the action to be performed
     */
    @Override
    public void prodMessage(int position, Product product, String action) {
        if(this.action.contentEquals("select")){//checking if user wanted to select product
            Intent replyIntent = new Intent();
            replyIntent.putExtra(EXTRA_REPLY, new Gson().toJson(product));
            setResult(RESULT_OK, replyIntent);
            finish();

        }else{

            if(action.contentEquals("view")){
                HashMap<String ,String> data = new HashMap<>();
                data.put("product", new Gson().toJson(product));
                GlobalMethod.goToActivity(this,AddProductActivity.class,data);
            }
        }
    }
}
