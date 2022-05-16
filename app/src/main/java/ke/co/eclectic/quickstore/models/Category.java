package ke.co.eclectic.quickstore.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


/**
 * Created by David Manduku on 08/10/2018.
 */
@Entity(tableName = "category")
public class Category {
   @PrimaryKey
   @NonNull
   Integer categoryid = 0;
   String categoryname;
   Integer productcount=0;


    /**
     * Gets productcount.
     *
     * @return the productcount
     */
    public Integer getProductcount() {
        return productcount;
    }

    /**
     * Sets productcount.
     *
     * @param productcount the productcount
     */
    public void setProductcount(Integer productcount) {
        this.productcount = productcount;
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

}
