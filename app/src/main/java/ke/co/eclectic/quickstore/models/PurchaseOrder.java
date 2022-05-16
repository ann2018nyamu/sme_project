package ke.co.eclectic.quickstore.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.models.others.PurchaseItems;
import timber.log.Timber;

/**
 * Created by David Manduku on 08/10/2018.
 */
@Entity(tableName = "purchaseorder")
public class PurchaseOrder {
    @PrimaryKey
    @NonNull
    Integer purchaseorderid =0;
    Integer supplierid =0;
    @SerializedName("paymenttype")
    Integer paymentType=0;
    Double totalcost =0.00;
    Integer storeid =0;
    String  suppliername ="";
    String  raisedby ="";
    String  storename ="";
    String  raisedon ="";//2018-11-29T07:43:38.89071Z
    String  lastedit ="";
    String  orderid ="";
    String  itemcount ="";
    String status ="";
    String inventoryStockListStr ="";
    String productListStr ="";
    private Boolean autoapporval;
    @Ignore
    public  List<PurchaseItems> purchase_items = new ArrayList<>();


    /**
     * Gets purchase items.
     *
     * @return the purchase items
     */
    public List<PurchaseItems> getPurchase_items() {
        inventoryStockListStr = new Gson().toJson(getInventoryStockList());
        return purchase_items;
    }

    /**
     * Get inventory stock list list.
     *
     * @return the list
     */
    public List<InventoryStock> getInventoryStockList(){
        ArrayList<InventoryStock> inventoryStockList = new ArrayList<>();
        InventoryStock popitem ;
        for(PurchaseItems isList: purchase_items){
            popitem = new InventoryStock();
            popitem.setInventoryid(isList.getInventoryid());
            popitem.setChoosenPrice(isList.getItemcost());
            popitem.setChoosenquantity( isList.getQuantity());
            popitem.setProductname( isList.getProductname());
            inventoryStockList.add(popitem);
        }

        return inventoryStockList;
    }

    /**
     * Get inventory stock list h hash map.
     *
     * @return the hash map
     */
    public  HashMap<Integer,InventoryStock> getInventoryStockListH(){
        ArrayList<InventoryStock> inventoryStockList = new Gson().fromJson(inventoryStockListStr,new TypeToken<List<InventoryStock>>(){}.getType());
        HashMap<Integer,InventoryStock> selectedInventoryStockH =new HashMap<>() ;

        for(InventoryStock isList: inventoryStockList){
            selectedInventoryStockH.put(isList.getInventoryid(),isList);
        }

        return selectedInventoryStockH;
    }

    /**
     * Sets purchase items.
     *
     * @param purchase_items the purchase items
     */
    public void setPurchase_items(List<PurchaseItems> purchase_items) {
        this.purchase_items = purchase_items;
    }

    /**
     * Gets product list str.
     *
     * @return the product list str
     */
    public String getProductListStr() {
        return productListStr;
    }

    /**
     * Sets product list str.
     *
     * @param productListStr the product list str
     */
    public void setProductListStr(String productListStr) {
        this.productListStr = productListStr;
    }

    /**
     * Gets totalcost.
     *
     * @return the totalcost
     */
    public Double getTotalcost() {
        return totalcost;
    }

    /**
     * Gets totalcost str.
     *
     * @return the totalcost str
     */
    public String getTotalcostStr() {
        return "Ksh. ".concat(totalcost.toString());
    }

    /**
     * Sets totalcost.
     *
     * @param totalcost the totalcost
     */
    public void setTotalcost(Double totalcost) {
        this.totalcost = totalcost;
    }

    /**
     * Gets storeid.
     *
     * @return the storeid
     */
    public Integer getStoreid() {
        return storeid;
    }

    /**
     * Sets storeid.
     *
     * @param storeid the storeid
     */
    public void setStoreid(Integer storeid) {
        this.storeid = storeid;
    }

    /**
     * Get inventory stock list str string.
     *
     * @return the string
     */
    public String getInventoryStockListStr(){
        return inventoryStockListStr;
    }

    /**
     * Sets inventory stock list str.
     *
     * @param inventoryStockListStr the inventory stock list str
     */
    public void setInventoryStockListStr(String inventoryStockListStr) {
        this.inventoryStockListStr = inventoryStockListStr;
    }

    /**
     * Gets storename.
     *
     * @return the storename
     */
    public String getStorename() {
        return storename;
    }

    /**
     * Sets storename.
     *
     * @param storename the storename
     */
    public void setStorename(String storename) {
        this.storename = storename;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets status name.
     *
     * @return the status name
     */
    public String getStatusName() {
        return status;
    }

    /**
     * Get status bg color drawable.
     *
     * @param context the context
     * @return the drawable
     */
    public Drawable getStatusBgColor(Context context){
        if(status.toLowerCase().contentEquals( "approved") || status.toLowerCase().contentEquals( "completed")){
           return ContextCompat.getDrawable(context, R.drawable.round_green_bg);
        }

        if(status.toLowerCase().contentEquals( "pending")){
          return  ContextCompat.getDrawable(context, R.drawable.round_black_bg);
        }
        if(status.toLowerCase().contentEquals( "cancelled")){
          return  ContextCompat.getDrawable(context, R.drawable.round_red_bg);
        }

        return ContextCompat.getDrawable(context, R.drawable.round_green_bg);
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets suppliername.
     *
     * @return the suppliername
     */
    public String getSuppliername() {
        return suppliername;
    }

    /**
     * Sets suppliername.
     *
     * @param suppliername the suppliername
     */
    public void setSuppliername(String suppliername) {
        this.suppliername = suppliername;
    }

    /**
     * Gets raisedby.
     *
     * @return the raisedby
     */
    public String getRaisedby() {
        return raisedby;
    }

    /**
     * Sets raisedby.
     *
     * @param raisedby the raisedby
     */
    public void setRaisedby(String raisedby) {
        this.raisedby = raisedby;
    }

    /**
     * Gets raisedon.
     *
     * @return the raisedon
     */
    public String getRaisedon() {
        return raisedon;
    }


    /**
     * Sets raisedon.
     *
     * @param raisedon the raisedon
     */
    public void setRaisedon(String raisedon) {
        this.raisedon = raisedon;
    }

    /**
     * Gets lastedit.
     *
     * @return the lastedit
     */
    public String getLastedit() {
        return lastedit;
    }

    /**
     * Sets lastedit.
     *
     * @param lastedit the lastedit
     */
    public void setLastedit(String lastedit) {
        this.lastedit = lastedit;
    }

    /**
     * Gets orderid.
     *
     * @return the orderid
     */
    public String getOrderid() {
        return orderid;
    }

    /**
     * Sets orderid.
     *
     * @param orderid the orderid
     */
    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    /**
     * Gets itemcount.
     *
     * @return the itemcount
     */
    public String getItemcount() {
        return itemcount;
    }

    /**
     * Sets itemcount.
     *
     * @param itemcount the itemcount
     */
    public void setItemcount(String itemcount) {
        this.itemcount = itemcount;
    }


    /**
     * Gets purchaseorderid.
     *
     * @return the purchaseorderid
     */
    public Integer getPurchaseorderid() {
        return purchaseorderid;
    }

    /**
     * Gets purchaseorder bsid.
     *
     * @return the purchaseorder bsid
     */
    public String getPurchaseorderBsid() {
        return storename.concat(" - ").concat(orderid);
    }

    /**
     * Sets purchaseorderid.
     *
     * @param purchaseorderid the purchaseorderid
     */
    public void setPurchaseorderid(Integer purchaseorderid) {
        this.purchaseorderid = purchaseorderid;
    }

    /**
     * Gets supplierid.
     *
     * @return the supplierid
     */
    public Integer getSupplierid() {
        return supplierid;
    }

    /**
     * Sets supplierid.
     *
     * @param supplierid the supplierid
     */
    public void setSupplierid(Integer supplierid) {
        this.supplierid = supplierid;
    }

    /**
     * Gets autoapporval.
     *
     * @return the autoapporval
     */
    public Boolean getAutoapporval() {
        return autoapporval;
    }

    /**
     * Sets autoapporval.
     *
     * @param autoapporval the autoapporval
     */
    public void setAutoapporval(Boolean autoapporval) {
        this.autoapporval = autoapporval;
    }

    /**
     * Generate po product json element.
     *
     * @return the json element
     */
    public JsonElement generatePOProduct(){
        List<InventoryStock> inventoryStockList = new Gson().fromJson(getInventoryStockListStr(),new TypeToken<List<InventoryStock>>(){}.getType());
        ArrayList<PurchaseItems> pop = new ArrayList<>();
        PurchaseItems popitem ;
        for(InventoryStock isList: inventoryStockList){
            popitem = new PurchaseItems();
            popitem.setInventoryid(isList.getInventoryid());
            popitem.setItemcost(isList.getChoosenPrice());
            popitem.setQuantity(isList.getChoosenquantity());
            pop.add(popitem);
        }

        JsonElement productData = new JsonParser().parse(new Gson().toJson(pop)).getAsJsonArray();
        Timber.v(productData.toString());

        return productData;
    }


    /**
     * Sets payment type.
     *
     * @param paymentType the payment type
     */
    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    /**
     * Gets payment type.
     *
     * @return the payment type
     */
    public Integer getPaymentType() {
        return paymentType;
    }
}
