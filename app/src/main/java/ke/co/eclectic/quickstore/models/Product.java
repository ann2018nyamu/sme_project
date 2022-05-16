package ke.co.eclectic.quickstore.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import ke.co.eclectic.quickstore.helper.GlobalMethod;

@Entity(tableName = "product",indices = {@Index(value = {"productid"}, unique = true)})
public class Product {
    @PrimaryKey
    @NonNull
    Integer productid = 0;
    String productname="";

    Integer categoryid=0;
    Integer unitid=0;
    String barcode="";
    String image="";
    Integer businessid=0;

    String categoryname="";
    String unitname="kg";


    Integer storeid=0;
    Double pprice=0.00;
    Integer quantity = 0;
    Integer pstockQty=10;

    /**
     * Instantiates a new Product.
     *
     * @param pId    the p id
     * @param pName  the p name
     * @param pPrice the p price
     */
    public Product(Integer pId,String pName, Double pPrice) {
        this.productid = pId;
        this.productname = pName;
        this.pprice = pPrice;
    }

    /**
     * Instantiates a new Product.
     */
    public Product() {

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
     * Gets unitid.
     *
     * @return the unitid
     */
    public Integer getUnitid() {
        return unitid;
    }

    /**
     * Sets unitid.
     *
     * @param unitid the unitid
     */
    public void setUnitid(Integer unitid) {
        this.unitid = unitid;
    }

    /**
     * Gets businessid.
     *
     * @return the businessid
     */
    public Integer getBusinessid() {
        return businessid;
    }

    /**
     * Sets businessid.
     *
     * @param businessid the businessid
     */
    public void setBusinessid(Integer businessid) {
        this.businessid = businessid;
    }

    /**
     * Gets productid.
     *
     * @return the productid
     */
    @NonNull
    public Integer getProductid() {
        return productid;
    }

    /**
     * Sets productid.
     *
     * @param productid the productid
     */
    public void setProductid(@NonNull Integer productid) {
        this.productid = productid;
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
     * Gets categoryid.
     *
     * @return the categoryid
     */
    public Integer getCategoryid() {
        return categoryid;
    }

    /**
     * Sets categoryid.
     *
     * @param categoryid the categoryid
     */
    public void setCategoryid(Integer categoryid) {
        this.categoryid = categoryid;
    }

    /**
     * Gets barcode.
     *
     * @return the barcode
     */
    public String getBarcode() {
        return barcode;
    }

    /**
     * Sets barcode.
     *
     * @param barcode the barcode
     */
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    /**
     * Gets image.
     *
     * @return the image
     */
    public String getImage() {
        return image;
    }

    /**
     * Gets image bitmap.
     *
     * @param context the context
     * @return the image bitmap
     */
    public Bitmap getImageBitmap(Context context) {
        return  GlobalMethod.stringToBitMap(image);
    }


    /**
     * Sets image.
     *
     * @param image the image
     */
    public void setImage(String image) {
        this.image = image;
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
     * Gets pprice.
     *
     * @return the pprice
     */
    public Double getPprice() {
        return pprice;
    }

    /**
     * Sets pprice.
     *
     * @param pprice the pprice
     */
    public void setPprice(Double pprice) {
        this.pprice = pprice;
    }

    /**
     * Gets quantity.
     *
     * @return the quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets quantity.
     *
     * @param quantity the quantity
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }


    /**
     * Gets pstock qty.
     *
     * @return the pstock qty
     */
    public Integer getPstockQty() {
        return pstockQty;
    }

    /**
     * Sets pstock qty.
     *
     * @param pstockQty the pstock qty
     */
    public void setPstockQty(Integer pstockQty) {
        this.pstockQty = pstockQty;
    }




}
