package ke.co.eclectic.quickstore.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

/**
 * Created by David Manduku on 08/10/2018.
 */
@Entity(tableName = "inventorystock",indices = {@Index(value = {"inventoryid"}, unique = true)})
public class InventoryStock {
    @PrimaryKey
    @NonNull
    Integer inventoryid=0;
    String productname="";
    Integer productid=0;
    String storename="";
    String categoryname="";
    String unitname="";

    Double saleprice=0.0;
    Double purchaseprice=0.0;
    String offertype="";
    Integer offertypeid=0;
    Double offeramount=0.0;
    Double quantity=0.0;
    Double minquantity=0.0;
    Integer storeid=0;


    @Ignore
    Double choosenquantity=0.0;
    @Ignore
    Double choosenPrice=0.0;
    @Ignore
    Double itemdiscount=0.0;
    @Ignore
    Double  leastsaleprice=0.0;
    @Ignore
    Double  computedsaleprice=0.0;


    public InventoryStock() {

    }
    public String getJson(){
        return new Gson().toJson(this);
    }

    /**
     * Sets leastsaleprice.
     *
     * @param leastsaleprice the leastsaleprice
     */
    public void setLeastsaleprice(Double leastsaleprice) {
        this.leastsaleprice = leastsaleprice;
    }

    /**
     * Gets computedsaleprice.
     *
     * @return the computedsaleprice
     */
    public Double getComputedsaleprice() {
        Double sp = saleprice;
        if(offertype.contentEquals("discount")){
            sp = saleprice - offeramount;
            itemdiscount = offeramount;
        }

        if(offertype.contains("least")){
            sp = saleprice;
            leastsaleprice = offeramount;
        }

        return sp;
    }
    /**
     * Gets computedsaleprice.
     *
     * @return the computedsaleprice
     */
    public Double getComputedPurchaseprice() {
        Double sp = purchaseprice;
        if(offertype.contentEquals("discount")){
            sp = saleprice - offeramount;
            itemdiscount = offeramount;
        }

        if(offertype.contains("least")){
            sp = saleprice;
            leastsaleprice = offeramount;
        }

        return sp;
    }

    /**
     * Sets computedsaleprice.
     *
     * @param computedsaleprice the computedsaleprice
     */
    public void setComputedsaleprice(Double computedsaleprice) {
        this.computedsaleprice = computedsaleprice;
    }

    /**
     * Gets itemdiscount.
     *
     * @return the itemdiscount
     */
    public Double getItemdiscount() {
        return itemdiscount;
    }


    /**
     * Sets itemdiscount.
     *
     * @param itemdiscount the itemdiscount
     */
    public void setItemdiscount(Double itemdiscount) {

        this.itemdiscount = itemdiscount;
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
     * Gets productid.
     *
     * @return the productid
     */
    public Integer getProductid() {
        return productid;
    }

    /**
     * Sets productid.
     *
     * @param productid the productid
     */
    public void setProductid(Integer productid) {
        this.productid = productid;
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
     * Gets choosen price.
     *
     * @return the choosen price
     */
    public Double getChoosenPrice() {

        return choosenPrice;
    }

    /**
     * Gets choosen price str.
     *
     * @return the choosen price str
     */
    public String getChoosenPriceStr() {

        return "Ksh. ".concat(choosenPrice.toString());
    }

    /**
     * Sets choosen price.
     *
     * @param choosenprice the choosenprice
     */
    public void setChoosenPrice(Double choosenprice) {
        this.choosenPrice = choosenprice;
    }

    /**
     * Gets purchaseprice.
     *
     * @return the purchaseprice
     */
    public Double getPurchaseprice() {
        return purchaseprice;
    }

    /**
     * Gets purchaseprice str.
     *
     * @return the purchaseprice str
     */
    public String getPurchasepriceStr() {
        return "Ksh. ".concat(purchaseprice.toString());
    }

    /**
     * Sets purchaseprice.
     *
     * @param purchaseprice the purchaseprice
     */
    public void setPurchaseprice(Double purchaseprice) {
        this.purchaseprice = purchaseprice;
    }

    /**
     * Gets offertypeid.
     *
     * @return the offertypeid
     */
    public Integer getOffertypeid() {
        return offertypeid;
    }

    /**
     * Sets offertypeid.
     *
     * @param offertypeid the offertypeid
     */
    public void setOffertypeid(Integer offertypeid) {
        this.offertypeid = offertypeid;
    }

    /**
     * Gets inventoryid.
     *
     * @return the inventoryid
     */
    public Integer getInventoryid() {
        return inventoryid;
    }

    /**
     * Sets inventoryid.
     *
     * @param inventoryid the inventoryid
     */
    public void setInventoryid(Integer inventoryid) {
        this.inventoryid = inventoryid;
    }

    /**
     * Gets productname.
     *
     * @return the productname
     */
    public String getProductname() {
        return productname;
    }

    /**
     * Sets productname.
     *
     * @param productname the productname
     */
    public void setProductname(String productname) {
        this.productname = productname;
    }

    /**
     * Gets categoryname.
     *
     * @return the categoryname
     */
    public String getCategoryname() {
        return categoryname;
    }

    /**
     * Sets categoryname.
     *
     * @param categoryname the categoryname
     */
    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    /**
     * Gets unitname.
     *
     * @return the unitname
     */
    public String getUnitname() {
        return unitname;
    }

    /**
     * Sets unitname.
     *
     * @param unitname the unitname
     */
    public void setUnitname(String unitname) {
        this.unitname = unitname;
    }

    /**
     * Gets saleprice.
     *
     * @return the saleprice
     */
    public Double getSaleprice() {
        return saleprice;
    }

    /**
     * Gets leastsaleprice.
     *
     * @return the leastsaleprice
     */
    public Double getLeastsaleprice() {
        return leastsaleprice;
    }


    /**
     * Gets saleprice str.
     *
     * @return the saleprice str
     */
    public String getSalepriceStr() {
        return "Ksh. ".concat(getSaleprice().toString());
    }

    /**
     * Sets saleprice.
     *
     * @param saleprice the saleprice
     */
    public void setSaleprice(Double saleprice) {
        this.saleprice = saleprice;
    }

    /**
     * Gets offertype.
     *
     * @return the offertype
     */
    public String getOffertype() {
        return offertype;
    }

    /**
     * Sets offertype.
     *
     * @param offertype the offertype
     */
    public void setOffertype(String offertype) {
        this.offertype = offertype;
    }

    /**
     * Gets offeramount.
     *
     * @return the offeramount
     */
    public Double getOfferamount() {
        return offeramount;
    }

    /**
     * Sets offeramount.
     *
     * @param offeramount the offeramount
     */
    public void setOfferamount(Double offeramount) {
        this.offeramount = offeramount;
    }

    /**
     * Gets quantity.
     *
     * @return the quantity
     */
    public Double getQuantity() {
        return quantity;
    }

    /**
     * Gets quantity str.
     *
     * @return the quantity str
     */
    public String getQuantityStr() {
        return quantity.toString();
    }

    /**
     * Sets quantity.
     *
     * @param quantity the quantity
     */
    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets minquantity.
     *
     * @return the minquantity
     */
    public Double getMinquantity() {
        return minquantity;
    }

    /**
     * Sets minquantity.
     *
     * @param minquantity the minquantity
     */
    public void setMinquantity(Double minquantity) {
        this.minquantity = minquantity;
    }

    /**
     * Gets choosenquantity.
     *
     * @return the choosenquantity
     */
    public Double getChoosenquantity() {
        return choosenquantity;
    }

    /**
     * Gets choosenquantity str.
     *
     * @return the choosenquantity str
     */
    public String getChoosenquantityStr() {
        return choosenquantity.toString();
    }

    /**
     * Sets choosenquantity.
     *
     * @param choosenquantity the choosenquantity
     */
    public void setChoosenquantity(Double choosenquantity) {
        this.choosenquantity = choosenquantity;
    }

    /**
     * Add qty double.
     *
     * @param qtyAdd the qty to add
     * @return the double
     */
    public Double addQty(Double qtyAdd){
        this.choosenquantity =  this.choosenquantity+qtyAdd;
        return choosenquantity;
    }

    /**
     * Minus qty double.
     *
     * @param qtyMinus the qty to minus
     * @return the double
     */
    public Double minusQty(Double qtyMinus){
        this.choosenquantity =  this.choosenquantity-qtyMinus;
        return choosenquantity;
    }

}
