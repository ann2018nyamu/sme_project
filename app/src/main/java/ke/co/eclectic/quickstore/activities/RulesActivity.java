package ke.co.eclectic.quickstore.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.adapters.MenuAdapter;
import ke.co.eclectic.quickstore.adapters.RuleAdapter;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.Rule;
import ke.co.eclectic.quickstore.models.User;
import timber.log.Timber;

/**
 * The type Test activity.
 */
public class RulesActivity extends AppCompatActivity implements RuleAdapter.RuleComm {
    @BindView(R.id.ruleRV)
    RecyclerView ruleRV;
    @BindView(R.id.txtMessage)
    TextView txtMessage;
    @BindView(R.id.toolBar)
    Toolbar toolBar;

    private CompositeDisposable disposable = new CompositeDisposable();
    private User updatedUser = new User();
    List<Rule> ruleList =new ArrayList<>();
    boolean isCurrentUser=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule);

        Bundle  extra = getIntent().getExtras();
        if(extra  != null){
            updatedUser =  new Gson().fromJson(extra.get("staffData").toString(),User.class);
        }
        Timber.v(new Gson().toJson(updatedUser));

        ButterKnife.bind(this);
        initToolbar();
        initData();
        initRV();
    }

    private void initData() {
        txtMessage.setText(updatedUser.getFirstname().concat(" permission in ").concat("\"").concat(updatedUser.getStorename()).concat("\"").concat(" store").concat(" as a ").concat(updatedUser.getRolename()));
        if(GlobalVariable.getCurrentUser().getNationalidnumber().contentEquals( updatedUser.getNationalidnumber())){
            isCurrentUser =  true;
            txtMessage.setText("Your permission in ".concat("\"").concat(updatedUser.getStorename()).concat("\"").concat(" store").concat(" as a ").concat(updatedUser.getRolename()));
        }
    }

    private void initRV() {
            ruleList = updatedUser.getCurrentBsRole().getRuleList();
            RuleAdapter ruleAdapter = new RuleAdapter(this, ruleList, isCurrentUser);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
            ruleRV.setLayoutManager(gridLayoutManager);
            ruleRV.setAdapter(ruleAdapter);
    }


    /**
     * initialized toolbar
     */
    private void initToolbar() {
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Rules");
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
     * handles menu item clicks events
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
                finish();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }

    }

    @Override
    public void ruleMessage(Rule rule) {

        Timber.v(" ruleMessage  ");

    }
}