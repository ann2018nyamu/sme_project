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
import ke.co.eclectic.quickstore.models.others.SalesItems;
import timber.log.Timber;

/**
 * Created by David Manduku on 08/10/2018.
 */
@Entity(tableName = "salesorder")
public class SalesOrder {
    @PrimaryKey
    @NonNull
    Integer salesorderid =0;
    Integer customerid =0;
    @SerializedName("paymenttype")
    Integer paymentType =0;
    Double  totalcost =0.00;
    Integer storeid =0;
    Integer active =0;
    String  customername ="";
    String  staffname ="";
    String  storename ="";
    String  createdon ="";//2018-11-29T07:43:38.89071Z
    String  lastedit ="";
    String  orderid ="";
    String  itemcount ="";
    Integer statusid =0;
    String statusname ="";
    String inventoryStockListStr ="";
    String productListStr ="";
    Double totaldiscount = 0.00;

    @Ignore
    String ordertype = "salesorder";//order type can be from pos or from sales order
    @Ignore
    Double paidamount = 0.00;// amount given by the customer . used in pos
    @Ignore
    Double balance = 0.00;//balance/change given to the customer. used in pos

    @Ignore
    private List<SalesItems> salesorder_items = new ArrayList<>();

    /**
     * gets the type of the order (pos or salesorder)
     *
     * @return string
     */
    public String getOrdertype() {
        return ordertype;
    }

    /**
     * sets the type of the order (pos or salesorder)
     *
     * @param ordertype string value of order type
     */
    public void setOrdertype(String ordertype) {
        this.ordertype = ordertype;
    }

    /**
     * Gets payment type.
     *
     * @return the payment type
     */
    public Integer getPaymentType() {
        return paymentType;
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
     * Gets paidamount.
     *
     * @return the paidamount
     */
    public Double getPaidamount() {
        return paidamount;
    }

    /**
     * Sets paidamount.
     *
     * @param paidamount the paidamount
     */
    public void setPaidamount(Double paidamount) {
        this.paidamount = paidamount;
    }

    /**
     * Gets balance.
     *
     * @return the balance
     */
    public Double getBalance() {
        return balance;
    }

    /**
     * Gets balance str.
     *
     * @return the balance str
     */
    public String getBalanceStr() {
        return "Ksh. ".concat(balance.toString());
    }

    /**
     * Sets balance.
     *
     * @param balance the balance
     */
    public void setBalance(Double balance) {
        this.balance = balance;
    }

    /**
     * Gets totaldiscount.
     *
     * @return the totaldiscount
     */
    public Double getTotaldiscount() {
        return totaldiscount;
    }

    /**
     * Gets totaldiscount str.
     *
     * @return the totaldiscount str
     */
    public String getTotaldiscountStr() {
        return "Ksh ".concat(totaldiscount.toString());
    }

    /**
     * Sets totaldiscount.
     *
     * @param totaldiscount the totaldiscount
     */
    public void setTotaldiscount(Double totaldiscount) {
        this.totaldiscount = totaldiscount;
    }

    /**
     * Gets customerid.
     *
     * @return the customerid
     */
    public Integer getCustomerid() {
        return customerid;
    }

    /**
     * Sets customerid.
     *
     * @param customerid the customerid
     */
    public void setCustomerid(Integer customerid) {
        this.customerid = customerid;
    }

    /**
     * Gets salesorder items.
     *
     * @return the salesorder items
     */
    public List<SalesItems> getSalesorder_items() {
        inventoryStockListStr = new Gson().toJson(getInventoryStockList());
        return salesorder_items;
    }

    /**
     * Sets salesorder items.
     *
     * @param salesorder_items the salesorder items
     */
    public void setSalesorder_items(List<SalesItems> salesorder_items) {
        this.salesorder_items = salesorder_items;
    }

    /**
     * Get inventory stock list list.
     *
     * @return the list
     */
    public List<InventoryStock> getInventoryStockList(){
        ArrayList<InventoryStock> inventoryStockList = new ArrayList<>();
        InventoryStock popitem ;
        for(SalesItems isList: salesorder_items){
            popitem = new InventoryStock();
            popitem.setInventoryid(isList.getInventoryid());
            popitem.setChoosenPrice(isList.getItemcost());
            popitem.setItemdiscount(isList.getItemdiscount());
            popitem.setChoosenquantity( isList.getQuantity());
            popitem.setQuantity(isList.getInventoryquantity());
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
    public HashMap<Integer,InventoryStock> getInventoryStockListH(){
        ArrayList<InventoryStock> inventoryStockList = new Gson().fromJson(inventoryStockListStr,new TypeToken<List<InventoryStock>>(){}.getType());
        HashMap<Integer,InventoryStock> selectedInventoryStockH =new HashMap<>() ;

        for(InventoryStock isList: inventoryStockList){
            selectedInventoryStockH.put(isList.getInventoryid(),isList);
        }

        return selectedInventoryStockH;
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
     * Gets statusid.
     *
     * @return the statusid
     */
    public Integer getStatusid() {
        return statusid;
    }

    /**
     * Sets statusid.
     *
     * @param statusid the statusid
     */
    public void setStatusid(Integer statusid) {
        this.statusid = statusid;
    }

    /**
     * Gets statusname.
     *
     * @return the statusname
     */
    public String getStatusname() {
        return statusname;
    }

    /**
     * Sets statusname.
     *
     * @param statusname the statusname
     */
    public void setStatusname(String statusname) {
        this.statusname = statusname;
    }

    /**
     * Get status bg color drawable.
     *
     * @param context the context
     * @return the drawable
     */
    public Drawable getStatusBgColor(Context context){
        if(statusname.toLowerCase().contentEquals( "approved") || statusname.toLowerCase().contentEquals( "completed")){
           return ContextCompat.getDrawable(context, R.drawable.round_green_bg);
        }

        if(statusname.toLowerCase().contentEquals( "pending")){
          return  ContextCompat.getDrawable(context, R.drawable.round_black_bg);
        }
        if(statusname.toLowerCase().contentEquals( "cancelled")){
          return  ContextCompat.getDrawable(context, R.drawable.round_red_bg);
        }



        return ContextCompat.getDrawable(context, R.drawable.round_green_bg);
    }

    /**
     * Gets customername.
     *
     * @return the customername
     */
    public String getCustomername() {
        return customername;
    }

    /**
     * Sets customername.
     *
     * @param customername the customername
     */
    public void setCustomername(String customername) {
        this.customername = customername;
    }

    /**
     * Gets staffname.
     *
     * @return the staffname
     */
    public String getStaffname() {
        return staffname;
    }

    /**
     * Sets staffname.
     *
     * @param staffname the staffname
     */
    public void setStaffname(String staffname) {
        this.staffname = staffname;
    }

    /**
     * Gets createdon.
     *
     * @return the createdon
     */
    public String getCreatedon() {
        return createdon;
    }

    /**
     * Sets createdon.
     *
     * @param createdon the createdon
     */
    public void setCreatedon(String createdon) {
        this.createdon = createdon;
    }

    /**
     * Gets active.
     *
     * @return the active
     */
    public Integer getActive() {
        return active;
    }

    /**
     * Sets active.
     *
     * @param active the active
     */
    public void setActive(Integer active) {
        this.active = active;
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
     * Gets salesorderid.
     *
     * @return the salesorderid
     */
    @NonNull
    public Integer getSalesorderid() {
        return salesorderid;
    }

    /**
     * Sets salesorderid.
     *
     * @param salesorderid the salesorderid
     */
    public void setSalesorderid(@NonNull Integer salesorderid) {
        this.salesorderid = salesorderid;
    }

    /**
     * Gets salesorder bsid.
     *
     * @return the salesorder bsid
     */
    public String getSalesorderBsid() {
        return storename.concat(" - ").concat(orderid);
    }


    /**
     * Generate sales order product json element.
     *
     * @param isPos the is pos
     * @return the json element
     */
    public JsonElement generateSOProduct(Boolean isPos){
        List<InventoryStock> inventoryStockList = new Gson().fromJson(getInventoryStockListStr(),new TypeToken<List<InventoryStock>>(){}.getType());
        ArrayList<SalesItems> pop = new ArrayList<>();
        SalesItems salesItems;
        for(InventoryStock isList: inventoryStockList){
            salesItems = new SalesItems();
            salesItems.setInventoryid(isList.getInventoryid());
            salesItems.setItemcost(isList.getChoosenPrice()*isList.getChoosenquantity());
            salesItems.setQuantity(isList.getChoosenquantity());
            salesItems.setItemdiscount(isList.getItemdiscount()*isList.getChoosenquantity());
            pop.add(salesItems);
        }

        //converting to pos item
        String pItems= new Gson().toJson(pop);
        if(isPos){
            pItems =   pItems.replaceAll("itemcost","cost").replaceAll("itemdiscount","discount");
        }

        JsonElement productData = new JsonParser().parse(pItems).getAsJsonArray();
        //Timber.v(productData.toString());
        return productData;
    }

}
