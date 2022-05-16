package ke.co.eclectic.quickstore.activities;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.purchaseOrder.PurchaseOrderActivity;
import ke.co.eclectic.quickstore.adapters.CountryAdapter;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.Country;
import ke.co.eclectic.quickstore.models.PaymentType;
import ke.co.eclectic.quickstore.rest.response.CountryResponse;
import ke.co.eclectic.quickstore.rest.response.PaymentTypeResponse;
import ke.co.eclectic.quickstore.rest.response.PurchaseOrderResponse;
import ke.co.eclectic.quickstore.viewModel.CountryViewModel;
import timber.log.Timber;

/**
 * The  Country code activity.
 */
public class CountryCodeActivity extends AppCompatActivity implements CountryAdapter.CountryComm {

    public static final String EXTRA_REPLY = "REPLY";
    // private Toolbar toolBar;
    //private RecyclerView countryRV;
    private CountryAdapter countryAdapter;
    @BindView(R.id.countryRV)
    RecyclerView countryRV;
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.rootView)
    CoordinatorLayout rootView;

    private CountryAdapter singleViewAdapter;
    private CountryViewModel mCountryViewModel;
    private String action="showcode";
    private SearchView searchView;
    HashMap<String,String> countryListH = new HashMap<>();
    HashMap<String,String> countryListFilterH = new HashMap<>();
    List<Country> countryList = new ArrayList<>();
    List<Country> countryListCopy = new ArrayList<>();
    private boolean refresh=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country);
        ButterKnife.bind(this);
      //  init();
        Bundle  extra = getIntent().getExtras();
        if(extra  != null){
            action =  extra.get("action").toString();

        }

        initToolbar();
        initData();
    }

    /**
     * initializes country data
     */
    private void initData() {
        Timber.v("Contries initData");
        mCountryViewModel = ViewModelProviders.of(this).get(CountryViewModel.class);
        mCountryViewModel.getAllCountries();


        mCountryViewModel.getApiResponse().observe(this, myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            if(myApiResponses.getStatus().contentEquals("success")){

                if(myApiResponses.getOperation().contains("refresh")) {
                    CountryResponse countryResponse = (CountryResponse) myApiResponses.getObject();
                    if (countryResponse.getDataArrayList().size() != 0) {
                        countryListH.clear();
                        countryList.clear();
                        for (Country c : countryResponse.getDataArrayList()){
                            if(!countryListH.containsKey(c.getCode())){
                                countryList.add(c);
                                countryListH.put(c.getCode(),c.getName());
                            }
                        }
                        countryListCopy = countryList;
                        initRV();

                    }
                }
            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(this,myApiResponses.getError(),rootView);
                }
            }
        });


    }


    /**
     * initializes toolbar
     */
    private void initToolbar() {
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(action.contentEquals("showcode")){
            setTitle("Choose Dial Code");
        }else{
            setTitle("Choose Nationality");
        }
        // making all textview use monteserrat font
        for(int i = 0; i < toolBar.getChildCount(); i++)
        {
            View view = toolBar.getChildAt(i);
            if(view instanceof TextView) {
                TextView textView = (TextView) view;
                textView.setTypeface(GlobalVariable.getMontserratMedium(this));
            }
        }

    }

    /**
     * initializes country list
     */
    private void initRV(){
        countryAdapter = new CountryAdapter(this,countryList,action ,mCountryViewModel);
        countryRV.setLayoutManager(new LinearLayoutManager(this));
        countryRV.setAdapter(countryAdapter);
    }


    /**
     * initializes toolbar search
     * @param menu toolbar menu object
     * @return  boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
            searchView.setIconified(true);
            //searchView.setBackgroundColor(Color.WHITE);
            //searchView.setSuggestionsAdapter(myAdapter);
            // Getting selected (clicked) item suggestion
            searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionClick(int position) {
                    // Add clicked text to search box
                    CursorAdapter ca = searchView.getSuggestionsAdapter();
                    Cursor cursor = ca.getCursor();
                    cursor.moveToPosition(position);
                    searchView.setQuery(cursor.getString(cursor.getColumnIndex("UserName")), false);
                    searchView.clearFocus();

                    return true;
                }

                @Override
                public boolean onSuggestionSelect(int position) {
                    return true;
                }
            });
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {

                    List<Country> filterListCopy = new ArrayList<>();
                    filterListCopy.clear();
                    // Filter data
                    for (int i = 0; i < countryListCopy.size(); i++) {
                        if (countryListCopy.get(i).getName().toLowerCase().contains(s.toLowerCase().trim()) ||
                                countryListCopy.get(i).getDialCode().toLowerCase().contains(s.toLowerCase().trim()) ||
                                countryListCopy.get(i).getCode().toLowerCase().contains(s.toLowerCase().trim())
                                ) {
                            filterListCopy.add(countryListCopy.get(i));
                        }
                    }
                    countryList = filterListCopy;

                    if(s.trim().length()<1){
                        countryList = countryListCopy;
                    }
                   if(countryAdapter != null){
                       countryAdapter.refresh(countryList);
                   }

                   // initRV();

                    return false;
                }
            });
        }
        return true;
    }


    /**
     * handles menu item clicks
     * @param item menu item
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
                Intent replyIntent = new Intent();
                setResult(RESULT_CANCELED, replyIntent);
                finish();
                return true;
            }

            default:{
                return super.onOptionsItemSelected(item);
            }

        }
    }

    /**
     * handles country item clicks
     * @param country country item
     * @param action  the action to be performed
     */
    @Override
    public void countryData(Country country, String action) {

        Timber.v("countryData "+EXTRA_REPLY);
        GlobalVariable.choosenCountry = country;
        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_REPLY, new Gson().toJson(country));
        setResult(RESULT_OK, replyIntent);
        finish();

    }
}
