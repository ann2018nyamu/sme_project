package ke.co.eclectic.quickstore.activities.creditors;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.adapters.CreditorAdapter;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.Creditor;
import ke.co.eclectic.quickstore.viewModel.CreditorViewModel;
import timber.log.Timber;

public class CreditorsActivity extends AppCompatActivity implements CreditorAdapter.CreditorComm {

    public static final String EXTRA_REPLY = "creditor_REPLY";
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.extcreditorSearch)
    EditText extcreditorSearch;
    @BindView(R.id.imgAddcreditor)
    ImageView imgAddcreditor;
    @BindView(R.id.rootView)
    CoordinatorLayout rootView;
    @BindView(R.id.creditorsRV)
    RecyclerView creditorsRV;
    @BindView(R.id.txtInfo)
    TextView txtInfo;
    @BindView(R.id.creditorSRL)
    SwipeRefreshLayout creditorSRL;

    private List<Creditor> creditorLists = new ArrayList<>();
    private List<Creditor> creditorListsCopy = new ArrayList<>();
    private CreditorAdapter creditorAdapter;
    private CreditorViewModel mCreditorViewModel;
    private String action="";
    private Double totalAmount=0.00;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditors);
        Bundle  extra = getIntent().getExtras();
        if(extra  != null){
            action = extra.get("action").toString();
            totalAmount = Double.valueOf(extra.get("totalAmount").toString());
        }
        Timber.v("CreditorsActivity setContentView");

        ButterKnife.bind(this);
        initToolbar();
        initData();
        initImageview();
        initEdittext();
        initRV();
        initSwipeRefresh();
        initObservable();


    }
    /**
     * initializes swiperefresh layout
     */
    private void initSwipeRefresh() {
        creditorSRL.setOnRefreshListener(() -> {
            HashMap<String,Object> data = new HashMap<>();
            data.put("request_type","allcreditors");
            data.put("businessid", GlobalVariable.getCurrentUser().getBusinessid(this));
            mCreditorViewModel.getAllCreditors(data);
        });
    }
    /**
     * initializes creditor data
     */
    private void initData() {
        mCreditorViewModel = ViewModelProviders.of(this).get(CreditorViewModel.class);
        mCreditorViewModel.totalAmount = totalAmount;
        HashMap<String,Object> data = new HashMap<>();
        data.put("request_type","creditorsbybusinessid");
        data.put("businessid",GlobalVariable.getCurrentUser().getBusinessid(this));

        mCreditorViewModel.getAllCreditors(data).observe(this,creditorList->{
            if( null != creditorList){
                Timber.v("From getAllcreditors "+ creditorList.size());
                if(creditorList.size() ==0){
                    txtInfo.setVisibility(View.VISIBLE);
                    txtInfo.setText("No creditors");

                }else{
                    txtInfo.setVisibility(View.GONE);
                }

                ordercreditorList(creditorList);
            }
        });

        mCreditorViewModel.getApiResponse().observe(this,myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            creditorSRL.setRefreshing(false);

            if(myApiResponses.getStatus().contentEquals("success")){
//                creditorResponse creditorResponse = (creditorResponse) myApiResponses.getObject();
//                if(myApiResponses.getOperation().contains("refresh")) {
//                    if (creditorResponse.getcreditorList().size() == 0) {
//                        CodingMsg.tl(this,"No creditor");
//                    }
//                }
            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(CreditorsActivity.this, myApiResponses.getError(),rootView);
                }
            }
        });


        mCreditorViewModel.getIsInternetConnected().observe(this, aBoolean -> {
            if(!aBoolean){
                //CodingMsg.tle(this, "Something went wrong.Please check your Internet");
            }
        });

    }



    /**
     * initializes view  observables
     */
    private void initObservable() {
        Observable<String> searchObservable = RxTextView.textChanges(extcreditorSearch)
                .skip(1)
                .debounce(500, TimeUnit.MILLISECONDS)
                .map(charSequence -> charSequence.toString());
        //subscribing an observer
        searchObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        filtercreditors(s);
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
     * filters creditor list based on the filter string
     *
     * @param filterStr  string to be searchd for
     */
    private void filtercreditors(String filterStr) {
        if(creditorListsCopy.size() == 0){return;}
        List<Creditor> filterListCopy = new ArrayList<>();
        // Filter data
        for (int i = 0; i < creditorListsCopy.size(); i++) {
            if (creditorListsCopy.get(i).getCreditorname().toLowerCase().contains(filterStr)
            ) {

                filterListCopy.add(creditorListsCopy.get(i));

            }
        }
        creditorLists = filterListCopy;
        if(filterStr.trim().length()<1){
            creditorLists = creditorListsCopy;
        }
        creditorAdapter.refresh(creditorLists);
    }
    /**
     * initializes edittext
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEdittext() {
        extcreditorSearch.setFocusable(true);
        extcreditorSearch.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });
    }

    /**
     * invokes refresh of creditor list in the recylerview
     *
     * @param sList list of creditors
     */
    public void ordercreditorList(List<Creditor> sList){
        creditorLists = sList;
        creditorListsCopy = sList;
        creditorAdapter.refresh(creditorLists);
        //return sList;
    }

    /**
     * initializes recyclerview
     */
    private void initRV(){

        creditorAdapter = new CreditorAdapter(this,creditorLists);
        creditorsRV.setLayoutManager(new LinearLayoutManager(this));
        creditorsRV.setAdapter(creditorAdapter);
    }
    /**
     * initializes imageview
     */
    private void initImageview() {
        if(!GlobalVariable.getCurrentUser().getCurrentBsRule("creditors","cancreate")) {
            imgAddcreditor.setVisibility(View.GONE);
            return;
        }
       // imgAddcreditor.setOnClickListener(v -> GlobalMethod.goToActivity(CreditorsActivity.this, AddCreditorActivity.class));
    }

    /**
     * initializes toolbar
     *
     */
    private void initToolbar() {
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Creditors");
    }


    /**
     * initializes user data
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
     * impementation of creditorAdapter interface
     *
     * @param position of item clicked
     * @param creditor  object clicked
     * @param action operation to be performed to the clicked creditor
     */
    @Override
    public void creditorMessage(int position, Creditor creditor, String action) {
        if(this.action.contentEquals("select")){
            Intent replyIntent = new Intent();
            replyIntent.putExtra(EXTRA_REPLY, new Gson().toJson(creditor));
            setResult(RESULT_OK, replyIntent);
            finish();
        }else{
            HashMap<String,String>  data = new HashMap<>();
            data.put("creditor",new Gson().toJson(creditor));
            //GlobalMethod.goToActivity(this,AddCreditorActivity.class,data);
        }
    }


}
