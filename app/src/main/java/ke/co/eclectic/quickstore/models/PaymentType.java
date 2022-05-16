package ke.co.eclectic.quickstore.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by David Manduku on 08/10/2018.
 */
@Entity(tableName = "paymenttype")
public class PaymentType {
    @PrimaryKey
    @NonNull
    Integer paymenttypeid = 0;
    String name= "";

    /**
     * Gets paymenttypeid.
     *
     * @return the paymenttypeid
     */
    @NonNull
    public Integer getPaymenttypeid() {
        return paymenttypeid;
    }

    /**
     * Sets paymenttypeid.
     *
     * @param paymenttypeid the paymenttypeid
     */
    public void setPaymenttypeid(Integer paymenttypeid) {
        this.paymenttypeid = paymenttypeid;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }
}
