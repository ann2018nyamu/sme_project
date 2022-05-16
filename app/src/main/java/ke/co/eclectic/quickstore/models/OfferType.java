package ke.co.eclectic.quickstore.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


/**
 * Created by David Manduku on 08/10/2018.
 */
@Entity(tableName = "offertype",indices = {@Index(value = {"offertypeid"}, unique = true)})
public class OfferType {
    @PrimaryKey
    @NonNull
    Integer offertypeid=0;
    String offername;
    String description;

    /**
     * Gets offertypeid.
     *
     * @return the offertypeid
     */
    @NonNull
    public Integer getOffertypeid() {
        return offertypeid;
    }

    /**
     * Sets offertypeid.
     *
     * @param offertypeid the offertypeid
     */
    public void setOffertypeid(@NonNull Integer offertypeid) {
        this.offertypeid = offertypeid;
    }

    /**
     * Gets offername.
     *
     * @return the offername
     */
    public String getOffername() {
        return offername;
    }

    /**
     * Sets offername.
     *
     * @param offername the offername
     */
    public void setOffername(String offername) {
        this.offername = offername;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
