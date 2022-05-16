package ke.co.eclectic.quickstore.models.others;

public class SalesItems {
    private  Double itemcost=0.0;
    private Double quantity=0.0;
    private Double inventoryquantity=0.0;
    private Double inventorycost=0.0;
    private String productname="";
    private Integer inventoryid =0;
    private Double itemdiscount =0.0;

    /**
     * Gets inventorycost str.
     *
     * @return the inventorycost str
     */
    public String getInventorycostStr() {
        return "Ksh. ".concat(inventorycost.toString());
    }

    /**
     * Gets inventorycost.
     *
     * @return the inventorycost
     */
    public Double getInventorycost() {
        return inventorycost;
    }

    /**
     * Sets inventorycost.
     *
     * @param inventorycost the inventorycost
     */
    public void setInventorycost(Double inventorycost) {
        this.inventorycost = inventorycost;
    }

    /**
     * Gets inventoryquantity.
     *
     * @return the inventoryquantity
     */
    public Double getInventoryquantity() {
        return inventoryquantity;
    }

    /**
     * Sets inventoryquantity.
     *
     * @param inventoryquantity the inventoryquantity
     */
    public void setInventoryquantity(Double inventoryquantity) {
        this.inventoryquantity = inventoryquantity;
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
