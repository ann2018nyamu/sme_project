package ke.co.eclectic.quickstore.activities.pos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.models.InventoryStock;
import timber.log.Timber;

public class CartEditActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "REPLY";
    public static final String DELETE_REPLY = "DELETE";
    @BindView(R.id.imgCancel)
    ImageView imgCancel;
    @BindView(R.id.imgDelete)
    ImageView imgDelete;

    @BindView(R.id.txtProductPrice)
    TextView txtProductPrice;
    @BindView(R.id.txtOfferDesc)
    TextView txtOfferDesc;
    @BindView(R.id.txtProductName)
    TextView txtProductName;
    @BindView(R.id.btnMinus)
    Button btnMinus;
    @BindView(R.id.btnAdd)
    Button btnAdd;

     @BindView(R.id.etxtInventoryPrice)
     EditText etxtInventoryPrice;
     @BindView(R.id.etxtInventoryQuantity)
     EditText etxtInventoryQuantity;

    private InventoryStock inventoryStock = new InventoryStock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_edit);
        Bundle  extra = getIntent().getExtras();
        if(extra  != null){
            CodingMsg.l("inventoryStock :"+extra.get("inventorystock").toString());
            inventoryStock = new Gson().fromJson(extra.get("inventorystock").toString(),InventoryStock.class);
        }
        ButterKnife.bind(this);
        initTextview();
        initEditTextView();
    }

    /**
     * initializes textview
     */
    private void initTextview() {
        txtProductPrice.setText(inventoryStock.getChoosenPriceStr());
        txtProductName.setText(inventoryStock.getProductname());
        if(inventoryStock.getOffertype().contentEquals("no offer")){
            txtOfferDesc.setText("");
        }
        if(inventoryStock.getOffertype().contentEquals("discount")){
            txtOfferDesc.setText("discount of Ksh. ".concat(String.valueOf(inventoryStock.getOfferamount())));
        }
        if(inventoryStock.getOffertype().contains("least")){
            txtOfferDesc.setText("Least price of Ksh. ".concat(String.valueOf(inventoryStock.getOfferamount())));
        }

    }

    /**
     * listens and perfoms subtraction operation to specific inventory stock
     */
    @OnClick(R.id.btnMinus)
    public void minusQty(){
        Double count =  inventoryStock.minusQty(1.0);
       if(inventoryStock.getChoosenquantity() < 1){
           count +=1;

           CodingMsg.tlw(this,"Please use the delete option to remove the product");
       }
        inventoryStock.setChoosenquantity(count);
        etxtInventoryQuantity.setText(count.toString());
    }
    /**
     * listens and perfoms adding operation to specific inventory stock
     */
    @OnClick(R.id.btnAdd)
    public void addQty() {
        Double count =  inventoryStock.addQty(1.0);
        if(inventoryStock.getChoosenquantity() >  inventoryStock.getQuantity()){
            count -=1;
            CodingMsg.tlw(this,"Stock limit reached");
        }
        inventoryStock.setChoosenquantity(count);
        etxtInventoryQuantity.setText(count.toString());
    }

    /**
     * dismiss the activity and return results
     */
    @OnClick(R.id.imgCancel)
    public void close(){
        Intent replyIntent = new Intent();
        setResult(RESULT_CANCELED, replyIntent);
        finish();
    }

    /**
     * updates qty/price in inventory stock
     */
    @OnClick(R.id.btnUpdate)
    public void update(){
        //validating data
        if(etxtInventoryPrice.getText().toString().trim().length() == 0){
            etxtInventoryPrice.setText(String.valueOf(inventoryStock.getChoosenPrice()));
        }

        if(etxtInventoryQuantity.getText().toString().trim().length() == 0){
            etxtInventoryQuantity.setText(String.valueOf(inventoryStock.getChoosenquantity()));
        }


        inventoryStock.setChoosenquantity(Double.valueOf(etxtInventoryQuantity.getText().toString()));
        inventoryStock.setPurchaseprice(Double.valueOf(etxtInventoryPrice.getText().toString()));
        if(!confirmOfferAmount(inventoryStock)){

           return ;
        }

        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_REPLY, new Gson().toJson(inventoryStock));
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    private boolean confirmOfferAmount(InventoryStock inventoryStock){
        if(inventoryStock.getOffertype().contentEquals("discount")){
            if(inventoryStock.getChoosenPrice() < inventoryStock.getSaleprice() - inventoryStock.getOfferamount()){
                CodingMsg.tle(this,"Invalid sales price");
                return false;
            }else{
                inventoryStock.setItemdiscount(inventoryStock.getSaleprice() - inventoryStock.getChoosenPrice() );
            }
        }

      return true;
    }

    /**
     *invokes the deletion of stock
     */
    @OnClick(R.id.imgDelete)
    public void delete(){
        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_REPLY, new Gson().toJson(inventoryStock));
        replyIntent.putExtra(DELETE_REPLY, "yes");
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    /**
     * initializes edittextviews
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEditTextView() {
        if(!inventoryStock.getOffertype().contentEquals("no offer")){
            etxtInventoryPrice.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });
        }


        etxtInventoryQuantity.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });


        etxtInventoryPrice.setHint(String.valueOf(inventoryStock.getChoosenPrice()));
        etxtInventoryQuantity.setHint(String.valueOf(inventoryStock.getChoosenquantity()));
    }


}
