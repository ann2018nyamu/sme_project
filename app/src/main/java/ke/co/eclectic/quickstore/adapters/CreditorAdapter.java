package ke.co.eclectic.quickstore.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.models.Creditor;

/**
 * Created by Manduku O. David on 21/12/2019.
 */
public class CreditorAdapter extends RecyclerView.Adapter<CreditorAdapter.ViewHolder> {

    List<Creditor> creditorList;
    Context context;
    CreditorComm creditorComm;

    /**
     * Instantiates a new Creditor adapter.
     *
     * @param context      the context
     * @param creditorList the creditor list
     */
    public CreditorAdapter(Context context, List<Creditor> creditorList) {
        this.creditorList = creditorList;
        this.context = context;
        this.creditorComm= (CreditorComm) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_creditor, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CreditorAdapter.ViewHolder holder, int position) {
        final Creditor creditor = creditorList.get(position);
       // String.format("%s",)
        holder.txtCreditorName.setText(creditor.getCreditorname());
        holder.txtCreditorInterest.setText("Interest: \t".concat(creditor.getInterestStr()));
        holder.txtCreditorDuration.setText(creditor.getDurationStr());
        holder.txtAmountPayable.setText("Payable amount: \t".concat(creditor.getAmountPayableStr()));
        holder.txtAmount.setText("Amount: \t".concat(creditor.getAmountStr()));

        holder.contentLay.setOnClickListener(view -> {
            creditorComm.creditorMessage(position,creditorList.get(position),"requestCredit");
        });

    }

    /**
     * Refresh creditor list.
     *
     * @param sList the creditor  list
     */
    public void refresh(List<Creditor> sList) {
        creditorList = sList;
        notifyDataSetChanged();
    }

    public interface CreditorComm{
        void creditorMessage(int position, Creditor creditor, String action);
    }

    @Override
    public int getItemCount() {
        return creditorList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCreditorName,txtCreditorDuration,txtAmountPayable,txtAmount,txtCreditorInterest;

        LinearLayout contentLay;



        /**
         * Instantiates a new View holder.
         *
         * @param itemView the item view
         */
        public ViewHolder(View itemView) {
            super(itemView);
            txtCreditorName = itemView.findViewById(R.id.txtCreditorName);
            txtCreditorDuration = itemView.findViewById(R.id.txtCreditorDuration);
            txtAmountPayable = itemView.findViewById(R.id.txtAmountPayable);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtCreditorInterest = itemView.findViewById(R.id.txtCreditorInterest);
            contentLay =  itemView.findViewById(R.id.contentLay);
        }

    }
}
