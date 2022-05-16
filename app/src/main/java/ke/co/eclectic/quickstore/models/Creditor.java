package ke.co.eclectic.quickstore.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@Entity(tableName = "creditor",indices = {@Index(value = {"creditorid"}, unique = true)})
public class Creditor {

    @PrimaryKey
    @NonNull
    private Integer creditorid=0;
    private String creditorname="Eclectic Bank";
    private Integer duration=30;//in terms of days
    private Double interest=10.0;//interest rate per month
    private Double amount=0.0;//amount loaned/credited

    @NonNull
    public Integer getCreditorid() {
        return creditorid;
    }

    public void setCreditorid(@NonNull Integer creditorid) {
        this.creditorid = creditorid;
    }

    public String getCreditorname() {
        return creditorname;
    }

    public void setCreditorname(String creditorname) {
        this.creditorname = creditorname;
    }

    public Integer getDuration() {
        return duration;
    }
    public String getDurationStr() {
        return "Duration:\t".concat(duration.toString()).concat(" days ,Due date:\t").concat(getDueDate());
    }


    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Double getInterest() {
        return interest;
    }
    public String getInterestStr() {
        return String.format(Locale.getDefault(),"%.2f",interest).concat("% per month");
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public Double getAmount() {
        return amount;
    }
    public String getAmountStr() {
        return String.format(Locale.getDefault(),"Ksh %.2f ",amount);
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getAmountPayable(){
        return this.amount + getInterestAmount();
    }
    public Double getInterestAmount(){
        return (this.amount * this.interest * this.interest) / (100*30);
    }
    public String getAmountPayableStr() {
        return String.format(Locale.getDefault(),"Ksh %.2f",getAmountPayable());
    }
    public String getDueDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, duration);
        return sdf.format(c.getTime());
    }


}
