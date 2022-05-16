package ke.co.eclectic.quickstore.activities.products;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.jakewharton.rxbinding2.widget.RxAdapterView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.theartofdev.edmodo.cropper.CropImage;
import com.vatsal.imagezoomer.ImageZoomButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.barcode.BarcodeCaptureActivity;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.Category;
import ke.co.eclectic.quickstore.models.Product;
import ke.co.eclectic.quickstore.models.Unit;
import ke.co.eclectic.quickstore.rest.response.UnitResponse;
import ke.co.eclectic.quickstore.viewModel.ProductViewModel;
import ke.co.eclectic.quickstore.viewModel.UnitViewModel;
import timber.log.Timber;

public class AddProductActivity extends AppCompatActivity {
    private static final int CATEGORY_REQUEST_CODE = 2;
    private static final int BARCODE_REQUEST_CODE = 5;
    private static final int GALLERY = 3;
    private static final int CAMERA = 4;
    private static final int RC_BARCODE_CAPTURE = 9001;


    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.spinnerProdUnit)
    Spinner spinnerProdUnit;
    @BindView(R.id.imgAddCategory)
    ImageView imgAddCategory;
    @BindView(R.id.etxtProdName)
    EditText etxtProdName;
    @BindView(R.id.rootView)
    CoordinatorLayout rootView;
    @BindView(R.id.imgPreview)
    ImageView imgPreview;
    @BindView(R.id.imgCodePreview)
    ImageView imgCodePreview;
    @BindView(R.id.picActionLay)
    LinearLayout picActionLay;
    @BindView(R.id.txtPicAction)
    TextView txtPicAction;
    @BindView(R.id.radioUploadPhoto)
    RadioButton radioUploadPhoto;
    @BindView(R.id.radioTakePhoto)
    RadioButton radioTakePhoto;
    @BindView(R.id.radioGenerateCode)
    RadioButton radioGenerateCode;
    @BindView(R.id.radioBarCode)
    RadioButton radioBarCode;

    @BindView(R.id.spinnerGenCode)
    Spinner spinnerGenCode;
    @BindView(R.id.hasCodeLay)
    LinearLayout hasCodeLay;
    @BindView(R.id.generateCodeLay)
    LinearLayout generateCodeLay;
    @BindView(R.id.scanCodeLay)
    LinearLayout scanCodeLay;
    @BindView(R.id.imgPicPreview1)
    ImageZoomButton imgPicPreview1;
    @BindView(R.id.etxtProdCode)
    EditText etxtProdCode;
    @BindView(R.id.btnSaveProduct)
    Button btnSaveProduct;
    @BindView(R.id.txtSaveCode)
    TextView txtSaveCode;
    @BindView(R.id.etxtSelectCategory)
    EditText etxtSelectCategory;

    private ArrayAdapter<String> categorylistSpAdapter ;
    private CompositeDisposable disposable = new CompositeDisposable();
    List<String> categoryStrList =new ArrayList<>();
    List<Category> categoryList =new ArrayList<>();
    HashMap<String,Category> categoryListH =new HashMap<>();
    List<Unit> productUnitList =new ArrayList<>();
    private List<String> productUnitStrList =new ArrayList<>();
    HashMap<String,Unit> productUnitListH =new HashMap<>();

    private String selectedUnit="";
    private Dialog addUnitDialog;
    private UnitViewModel mUnitViewModel;
    private String productCode="";
    private boolean showLargeImage= false;
    private Uri fileUri;
    private String photoStr="";
    private String fileName="";
    private Product currentProduct = new Product();
    private Category selectedCategory= new Category();
    private ProductViewModel mProductViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        Bundle  extra = getIntent().getExtras();
        if(extra  != null){
            Timber.v(extra.get("product").toString());
            currentProduct = new Gson().fromJson(extra.get("product").toString(),Product.class);
            if(null == currentProduct){
                currentProduct = new Product();
            }
        }

        ButterKnife.bind(this);
        initToolbar();
        initImageview();
        initEdittext();
        initSpinnerProductUnit();
        initSpinnerCode();
        initButton();
        initRadioButton();
        initObservables();
        initData();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }
    }

    private void initRadioButton() {
        if(!GlobalVariable.getCurrentUser().getCurrentBsRule("products","cancreate")) {
            radioGenerateCode.setVisibility(View.GONE);
            radioBarCode.setVisibility(View.GONE);
            radioTakePhoto.setVisibility(View.GONE);
            radioUploadPhoto.setVisibility(View.GONE);
        }
    }


    /**
     * initializes buttons
     */
    private void initButton() {
        if(!GlobalVariable.getCurrentUser().getCurrentBsRule("products","cancreate")) {
            btnSaveProduct.setVisibility(View.GONE);
            return;
        }
            if (currentProduct.getProductid() != 0) {
                btnSaveProduct.setText("Update Product");
                Completable.fromAction(() -> validateInfo(false)).delay(5, TimeUnit.SECONDS);
            }

    }
    /**
     * initializes edittext
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEdittext() {
        if(GlobalVariable.getCurrentUser().getCurrentBsRule("products","cancreate")) {
            etxtSelectCategory.setOnClickListener(view -> {
                HashMap<String, String> data = new HashMap<>();
                data.put("action", "select");
                GlobalMethod.goToActivity(this, ProductCategoryActivity.class, data, CATEGORY_REQUEST_CODE);
            });

            etxtProdName.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });

            etxtProdCode.setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });
        }
        //populating data

        if(currentProduct.getProductid()!=0){
            etxtProdCode.setText(currentProduct.getBarcode());
            etxtProdName.setText(currentProduct.getProductname());
            etxtSelectCategory.setText(currentProduct.getCategoryname());
            selectedCategory.setCategoryname(currentProduct.getCategoryname());
            selectedCategory.setCategoryid(currentProduct.getCategoryid());
        }


    }
    /**
     * initializes data
     */
    private void initData() {
        mUnitViewModel = ViewModelProviders.of(this).get(UnitViewModel.class);
        mProductViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
       // getCategory();
        getUnits();
    }

    /**
     *
     * fetches units
     *
     */
    private void getUnits() {
        if(currentProduct.getProductid()!=0){
            selectedUnit = currentProduct.getUnitname();
        }
        Timber.v("From getUnits ");
        HashMap<String,Object> data = new HashMap<>();
        data.put("request_type","getunits");
        data.put("businessid",GlobalVariable.getCurrentUser().getBusinessid(this));
        mUnitViewModel.getAllUnits(data).observe(this,units->{
            try {
                Timber.v("From getAllUnits "+units.size());
                if( null != units){
                    Timber.v("From getAllUnits "+units.size());
                    initUnitSpinnerData(units);
                }
            }catch (Exception e){
                Timber.v("unit error"+ e.getMessage());
            }
        });

        mUnitViewModel.getApiResponse().observe(this,myApiResponses->{
            Timber.v(myApiResponses.getStatus()+"  "+myApiResponses.getOperation());
            if(myApiResponses.getStatus().contentEquals("success")){
                  if(myApiResponses.getOperation().contentEquals("apisave")){
                      UnitResponse unitResponse = (UnitResponse) myApiResponses.getObject();

                      GlobalMethod.showSuccess(this,unitResponse.getMessage(),true);

//                      selectedUnit = unitResponse.getUnit().getUnitname();
//                      productUnitListH.put(unitResponse.getUnit().getUnitname(),unitResponse.getUnit());
//                      productUnitStrList.add(productUnitStrList.size(),selectedUnit);
//                      unitlistSpAdapter.notifyDataSetChanged();
//                      spinnerProdUnit.setSelection(productUnitStrList.size());

                  }
            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(AddProductActivity.this,myApiResponses.getError(),rootView);
                }
            }
        });

        mProductViewModel.getApiResponse().observe(this,myApiResponses->{
            if(myApiResponses.getStatus().contentEquals("success")) {
                if (myApiResponses.getOperation().contentEquals("apisave")) {
                    UnitResponse unitResponse = (UnitResponse) myApiResponses.getObject();
                    if(currentProduct.getProductid() == 0){
                        CodingMsg.tls(AddProductActivity.this,"Product added successfully");
                    }else{
                        CodingMsg.tls(AddProductActivity.this,"Product updated successfully");
                    }

                    disposable.add( Completable.timer(5, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                            .subscribe(AddProductActivity.this::finish));
                }
            }else{
                if (null != myApiResponses.getError()){
                    GlobalMethod.parseCommError(AddProductActivity.this,myApiResponses.getError(),rootView);
                }
            }
        });

       
       
    }

    /**
     *  initializes unit spinner data
     *
     * @param unitList data to be inserted to spinner
     */
    private void initUnitSpinnerData(List<Unit> unitList){
        productUnitStrList.clear();
        productUnitListH.clear();
        productUnitStrList.add("Choose Product Unit");
        productUnitStrList.add("Add unit");
        for (Unit c: unitList){
            productUnitStrList.add(productUnitStrList.size(),c.getUnitname());
            productUnitListH.put(c.getUnitname(),c);
        }

       // unitlistSpAdapter.notifyDataSetChanged();
        initSpinnerProductUnit();

        if(!selectedUnit.contentEquals("")){
            for(int i=0 ;i<productUnitStrList.size();i++ ){
                if(productUnitStrList.get(i).contentEquals(selectedUnit)
                        ){
                    spinnerProdUnit.setSelection(i);
                }
            }
        }

    }

    /**
     * initializes generate code spinner
     */
    private void initSpinnerCode() {
        List<String> codeList =new ArrayList<>() ;
        codeList.add("Generate Code");
        codeList.add("Generate QR Code");
        codeList.add("Generate Barcode");
        //ArrayAdapter<String> adapter = ArrayAdapter.createFromResource(this, locationList, android.R.layout.simple_spinner_item);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  R.layout.spinner_dropdown_item, codeList);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item2);
        if(!GlobalVariable.getCurrentUser().getCurrentBsRule("products","cancreate")) {
            spinnerGenCode.setEnabled(false);
        }

            spinnerGenCode.setAdapter(adapter);


    }

    /**
     * initializes observables
     */
    private void initObservables() {
            //code generator
            disposable.add(
                    RxAdapterView.itemSelections(spinnerGenCode)
                            .skip(1)
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe(integer -> {

                                CheckedTextView t = spinnerGenCode.getSelectedView().findViewById(R.id.text1);
                                if (integer == 0) {
                                    t.setTextColor(ResourcesCompat.getColor(getResources(), R.color.editTextPlaceholderColor, null));
                                } else {
                                    t.setTextColor(ResourcesCompat.getColor(getResources(), R.color.editTextColor, null));
                                }
                                if (etxtProdName.getText().toString().trim().isEmpty()) {
                                    CodingMsg.tle(this, "Enter product name");
                                    return;
                                }

                                autogenerateCode(integer);


                            }));
            //category spinner


            disposable.add(
                    RxAdapterView.itemSelections(spinnerProdUnit)
                            .skip(1)
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe(integer -> {
                                try {
                                    CheckedTextView t = spinnerProdUnit.getSelectedView().findViewById(R.id.text1);


                                    selectedUnit = t.getText().toString();

                                    if (integer == 0) {
                                        selectedUnit = "";
                                        t.setTextColor(ResourcesCompat.getColor(getResources(), R.color.editTextPlaceholderColor, null));
                                    } else {
                                        t.setTextColor(ResourcesCompat.getColor(getResources(), R.color.editTextColor, null));
                                    }

                                    if (integer == 1) {
                                        selectedUnit = "";
                                        //show add unit dialogue
                                        showAddUnitDialogue();
                                    }
                                    validateInfo(false);
                                } catch (Exception e) {
                                    Timber.v(e.getMessage());
                                }

                            }));

            Observable<String> prodnameObservable = RxTextView.textChanges(etxtProdName).skip(1)
                    .debounce(1, TimeUnit.SECONDS)
                    .map(charSequence -> charSequence.toString());

            disposable.add(prodnameObservable
                    .skip(1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<String>() {
                        @Override
                        public void onNext(String str) {
                            Timber.v(str);
                            productCode = str.concat(GlobalVariable.getCurrentUser().getBusinessid(AddProductActivity.this).toString());
                            validateInfo(false);
                            autogenerateCode(1);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }

                    }));


    }

    /**
     * autogenerates codes
     * @param integer  type of code to generate 1- qr code 2- barcode
     */
    private void autogenerateCode(Integer integer) {
        if(!etxtProdName.getText().toString().trim().isEmpty()){
            productCode = etxtProdName.getText().toString().concat(GlobalVariable.getCurrentUser().getBusinessid(this).toString());
            showLargeImage = true;

            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            Bitmap bitmap = null;
            if(integer == 1){
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(productCode, BarcodeFormat.QR_CODE, 200, 200);

                    bitmap = new BarcodeEncoder().createBitmap(bitMatrix);

                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }

            if(integer == 2){
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(productCode, BarcodeFormat.CODE_128, 200, 200);
                    bitmap = new BarcodeEncoder().createBitmap(bitMatrix);
                } catch (WriterException e) {
                    e.printStackTrace();
                    Timber.v("Barcode error "+e.getMessage());
                }

            }
            imgCodePreview.setImageBitmap(bitmap);

        }else{
            showLargeImage = false;
            spinnerGenCode.setSelection(0);
        }
    }

    /**
     * validates user information
     *
     *
     * @param showErrors true to show error and vice versa
     * @return boolean
     */
    private boolean validateInfo(boolean showErrors) {
        String productName = etxtProdName.getText().toString();
        if(etxtProdName.getText().toString().isEmpty()){
            if(showErrors){
                CodingMsg.tle(this,"Please enter a product name");
            }

            return false;
        }
        if(selectedCategory.getCategoryid() == 0){
            if(showErrors) {
                CodingMsg.tle(this, "Please choose a product category");
            }
            return false;
        }

        if(selectedUnit.contentEquals("")){
            if(showErrors) {
                CodingMsg.tle(this, "Please choose a product unit");
            }
            return false;
        }

        updateButton(true);


        currentProduct.setProductname(productName);
        currentProduct.setCategoryid(selectedCategory.getCategoryid());
        currentProduct.setUnitid(productUnitListH.get(selectedUnit).getUnitid());
        currentProduct.setBarcode(productCode);
        currentProduct.setImage(photoStr);
        currentProduct.setBusinessid(GlobalVariable.getCurrentUser().getBusinessid(this));

        return true;
    }
    /**
     * changes button status based on the validity of user inputed data
     *
     * @param valid boolean true to make the button active
     */
    public void updateButton(boolean valid) {
        if (valid) {
            btnSaveProduct.setEnabled(true);
            btnSaveProduct.setBackgroundResource(R.drawable.btn_active);
        }else{
            btnSaveProduct.setEnabled(false);
            btnSaveProduct.setBackgroundResource(R.drawable.btn_inactive);
        }
    }

    /**
     * initializes imageview
     */
    private void initImageview() {
        if(currentProduct.getProductid()!=0){
            if(currentProduct.getImageBitmap(this) != null){
                imgPreview.setImageBitmap(currentProduct.getImageBitmap(this));
            }
        }

        if(!GlobalVariable.getCurrentUser().getCurrentBsRule("products","cancreate")) {
            imgAddCategory.setVisibility(View.GONE);
            picActionLay.setVisibility(View.GONE);
        }


    }

    /**
     * saves generated code to phone
     */
    @OnClick(R.id.txtSaveCode)
    public void save_code(){
        if(showLargeImage){
           Bitmap bt = ((BitmapDrawable)imgCodePreview.getDrawable()).getBitmap();

           File f = GlobalMethod.saveImageToPhone(this,bt,productCode.concat(".jpg"));

           if( f != null){
               CodingMsg.tls(this,"file saved ".concat(f.getAbsolutePath()));
           }else{
               CodingMsg.tls(this,"file not saved ");
           }
        }
    }

    /**
     * displays large image for the code generated
     */
    @OnClick(R.id.imgCodePreview)
    public void largeDisplay(){
        if(showLargeImage){
            imgPicPreview1.setImageDrawable(imgCodePreview.getDrawable());
            imgPicPreview1.performClick();
        }
    }
    /**
     * displays large image for the code generated
     */
    @OnClick(R.id.imgPreview)
    public void largeImageDisplay(){
        if(currentProduct.getImageBitmap(this)!= null){
            imgPicPreview1.setImageDrawable(imgPreview.getDrawable());
            imgPicPreview1.performClick();
        }
    }

    /**
     * opens qr/barcode scanner activity
     */
    @OnClick(R.id.scanCodeLay)
    public void openScanner(){
        Intent intent = new Intent(this, BarcodeCaptureActivity.class);
        intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
       //intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());
        startActivityForResult(intent, RC_BARCODE_CAPTURE);
        GlobalMethod.goToActivity(this,BarCodeActivity.class,null,BARCODE_REQUEST_CODE);
    }

    /**
     * invokes reading  autogenearate code once clicked
     */
    @OnClick(R.id.radioBarCode)
    public void readBarcodeClicked(){
        if(!GlobalVariable.getCurrentUser().getCurrentBsRule("products","cancreate")) {
            return;
        }
        hasCodeLay.setVisibility(View.VISIBLE);
        generateCodeLay.setVisibility(View.GONE);
        if(!currentProduct.getBarcode().contentEquals("")){
            autogenerateCode(1);
        }
    }
    /**
     * invokes autogenearate code once clicked
     */
    @OnClick(R.id.radioGenerateCode)
    public void generateCode(){
        if(!GlobalVariable.getCurrentUser().getCurrentBsRule("products","cancreate")) {
            return;
        }
            hasCodeLay.setVisibility(View.GONE);
        generateCodeLay.setVisibility(View.VISIBLE);
        if(!currentProduct.getBarcode().contentEquals("")){
            autogenerateCode(1);
        }
    }

    /**
     * listens to click event on radioUploadPhoto radiobutton
     */
    @OnClick(R.id.radioUploadPhoto)
    public void uploadPhotoClicked(){
        txtPicAction.setText("My gallery");
    }
    /**
     * listens to click event on radioTakePhoto radiobutton
     */
    @OnClick(R.id.radioTakePhoto)
    public void takePhotoClicked(){
        txtPicAction.setText("My camera");
    }

    @OnClick(R.id.picActionLay)
    public void picActionLay(){
        if(txtPicAction.getText().toString().toLowerCase().contains("gallery")){
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(galleryIntent, "Select App to select images"),GALLERY);
        }else{
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(Intent.createChooser(intent, "Select App to Open camera"),CAMERA);
        }
    }

    /**
     * listens to click event for adding category
     */
    @OnClick(R.id.imgAddCategory)
    public void addCategory(){
        HashMap<String,String> data = new HashMap<>();
        data.put("action","add");
        GlobalMethod.goToActivity(AddProductActivity.this,
        ProductCategoryActivity.class,data,CATEGORY_REQUEST_CODE);
    }

    /**
     * listens to click event for saving product
     */
    @OnClick(R.id.btnSaveProduct)
    public void save(){
        if(validateInfo(true)){
            saveProduct();
        }

    }

    /**
     * cleans up activity memory
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    /**
     * checks if user has enabled given permission
     *
     * @return boolean true if permossion is granted
     */
    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED
                && result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * request for permission
     */
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    //not granted
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * initializes product unit spinner
     */
    private void initSpinnerProductUnit() {
//        productUnitStrList.add("Product Unit");
//        productUnitStrList.add("Add unit");

        ArrayAdapter<String> unitlistSpAdapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown_item, productUnitStrList);
        // Specify the layout to use when the list of choices appears
        unitlistSpAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item2);
        if(!GlobalVariable.getCurrentUser().getCurrentBsRule("products","cancreate")) {
            spinnerProdUnit.setEnabled(false);
        }
        spinnerProdUnit.setAdapter(unitlistSpAdapter);
        if(currentProduct.getProductid() != 0){
            selectedUnit= currentProduct.getUnitname();

        }
    }

    /**
     * shows unit add dialogue
     */
    @SuppressLint("ClickableViewAccessibility")
    private void showAddUnitDialogue() {
        addUnitDialog = new Dialog(this);
        addUnitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addUnitDialog.setContentView(R.layout.dialogue_addunit_layout);
        addUnitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtTitle =  addUnitDialog.findViewById(R.id.txtTitle);
        Button btnSave = addUnitDialog.findViewById(R.id.btnSave);
        Button btnCancel = addUnitDialog.findViewById(R.id.btnCancel);
        EditText etxtName = addUnitDialog.findViewById(R.id.etxtName);
        txtTitle.setText("Add Unit");

        etxtName.setOnTouchListener((v, event) -> {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            return false;
        });

        btnSave.setOnClickListener(v -> {
            String name = "";
            if(etxtName.getText().toString().trim().isEmpty()){
                etxtName.setError("Enter Unit Name");
                return ;
            }
             saveUnit(etxtName.getText().toString());
             addUnitDialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> {
             addUnitDialog.dismiss();
        });

        addUnitDialog.setCancelable(true);
        addUnitDialog.show();

    }

    /**
     * sends product information to mProductViewModel for saving
     */
    private void saveProduct() {
        mProductViewModel.insert(currentProduct);
    }

    /**
     * send unit information to mUnitViewModel for saving
     * @param unitName
     */
    private void saveUnit(String unitName) {

        Unit unit = new Unit();
        unit.setUnitname(unitName);
        unit.setBusinessid(GlobalVariable.getCurrentUser().getBusinessid(this));
        selectedUnit= unitName;
        mUnitViewModel.insert(unit);
    }

    /**
     * initializes toolbar
     */
    private void initToolbar() {

        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(currentProduct.getProductid() == 0){
            setTitle("Add product");
        }else{
            setTitle("View product");
            if(GlobalVariable.getCurrentUser().getCurrentBsRule("products","cancreate")) {
                setTitle("Edit product");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try {
                    fileUri = result.getOriginalUri();result.getUri();
                    Bitmap bitmp = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                    imgPreview.setImageBitmap(bitmp);
                    photoStr = GlobalMethod.bitMapToString(bitmp);
                    GlobalMethod.saveImageToPhone(this,bitmp,fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                Toast.makeText(this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Select profile image again " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }


        if (requestCode == CATEGORY_REQUEST_CODE && resultCode == RESULT_OK) {
            //returns a single category object
            //fetch all category again
            selectedCategory = new Gson().fromJson(data.getStringExtra(ProductCategoryActivity.EXTRA_REPLY),Category.class);
            etxtSelectCategory.setText(selectedCategory.getCategoryname());

            //getCategory();
        }
        if (requestCode == GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                fileName = System.currentTimeMillis()+".jpg";
                CropImage.activity(data.getData())
                        .start(this);
            }
        }
         if (requestCode == CAMERA) {
            Timber.v("CAMERA response");
            //when using custom imaging library
             photoStr = data.getStringExtra(BarCodeActivity.EXTRA_REPLY);
             imgPreview.setImageBitmap(GlobalMethod.stringToBitMap(photoStr));

             Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
             imgPreview.setImageBitmap(thumbnail);
             fileName = System.currentTimeMillis()+".jpg";
             File f =   GlobalMethod.saveImageToPhone(this,thumbnail,fileName);

             CropImage.activity(  Uri.fromFile(f))
                     .start(this);

             Toast.makeText(AddProductActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();

         }


        if (requestCode == BARCODE_REQUEST_CODE && resultCode == RESULT_OK) {
            etxtProdCode.setText(data.getStringExtra(BarCodeActivity.EXTRA_REPLY));
            productCode = data.getStringExtra(BarCodeActivity.EXTRA_REPLY);
        }

        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    etxtProdCode.setText(barcode.displayValue);
                    productCode = barcode.displayValue;
                    Timber.v("Barcode read: " + barcode.displayValue);
                } else {
                    CodingMsg.tle(this,getString(R.string.barcode_failure));
                    Timber.v("No barcode captured, intent data is null");
                }
            } else {
                CodingMsg.tle(this,String.format(getString(R.string.barcode_error),CommonStatusCodes.getStatusCodeString(resultCode)));

            }
        }
    }

    /**
     * listen to menu item click events
     *
     * @param item
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
