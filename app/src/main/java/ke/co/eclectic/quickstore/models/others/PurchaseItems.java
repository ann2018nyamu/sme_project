package ke.co.eclectic.quickstore.models.others;

public class PurchaseItems {
    private  Double itemcost=0.0;
    private Double quantity=0.0;
    private String productname="";
    private Integer inventoryid =0;

    /**
     * Gets itemcost.
     *
     * @return the itemcost
     */
    public Double getItemcost() {
        return itemcost;
    }

    /**
     * Gets itemcost str.
     *
     * @return the itemcost str
     */
    public String getItemcostStr() {
        return "Ksh. ".concat(itemcost.toString());
    }

    /**
     * Sets itemcost.
     *
     * @param itemcost the itemcost
     */
    public void setItemcost(Double itemcost) {
        this.itemcost = itemcost;
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

        return ((Integer)quantity.intValue()).toString();
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
}
