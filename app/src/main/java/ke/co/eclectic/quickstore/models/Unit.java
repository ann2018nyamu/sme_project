package ke.co.eclectic.quickstore.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


/**
 * Created by David Manduku on 08/10/2018.
 */
@Entity(tableName = "unit")
public class Unit {
    @PrimaryKey
    @NonNull
    Integer unitid=0;
    Integer businessid=0;
    String unitname="";


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

}
